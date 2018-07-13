package jp.gr.java_conf.falius.economy2.account;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

final class FixedAssetManager {
    private final Set<FixedAsset> mFixedAssets; // TODO:建物は科目が別なので、別に保持する

    FixedAssetManager() {
        mFixedAssets = new HashSet<FixedAsset>();
    }

    /**
     * 固定資産を追加します
     * @param mDateOfAcquisition 取得日
     * @param mAcquisitionCost 取得原価
     * @param mServiceLife 耐用年数
     */
    void add(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
        mFixedAssets.add(new FixedAsset(dateOfAcquisition, acquisitionCost, serviceLife));
    }

    /**
     * 所有している固定資産全てにおいて、減価償却の処理を行います
     * より具体的には、dateが償却日である固定資産のみ減価償却し、その償却費の総額を返します。
     * ただし、帳簿への記帳処理は行いません
     * @param date 記入日
     * @return その日の償却額
     */
    int record(LocalDate date) {
        return mFixedAssets.stream()
                .collect(Collectors.summingInt(asset -> asset.record(date)));
    }

    /**
     * 保有している固定資産の現在価値の総額を計算します
     */
    int presentValue() {
        return mFixedAssets.stream()
                .collect(Collectors.summingInt(FixedAsset::presentValue));
    }

    void printAll() {
        mFixedAssets.stream().sorted((a1, a2) -> a1.dateOfAcquisition().compareTo(a2.mDateOfAcquisition)) // 取得日でソート
                .map(FixedAsset::print) // 最終計上日でマップされる
                .collect(Collectors.maxBy((d1, d2) -> d1.compareTo(d2))).get();
    }

    /**
     * 固定資産の減価償却の計算を行うクラス
     * 土地は減価償却しないので土地以外
     */
    private static class FixedAsset {
        private static final int RESIDUAL_PERCENT = 10; // 取得原価に対する残存価額の割合(%)
        private final LocalDate mDateOfAcquisition; // 取得日
        private final int mAcquisitionCost; // 取得原価
        private final int mServiceLife; // 耐用年数
        private final int mResidualValue; // 残存価額
        private final int mFixedAmountOfMonths; // 定額法における、償却月額
        private final LocalDate mLastRecordedDate; // 最終計上日 TODO: 営業日を考慮する
        private int mUndepreciatedBalance; // 未償却残高
        private final SortedMap<LocalDate, Integer> mRecordMap; // 償却日から未償却額へのマップ

        /**
         * @param mDateOfAcquisition 取得日
         * @param mAcquisitionCost 取得原価
         * @param mServiceLife 耐用年数
         */
        private FixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
            mDateOfAcquisition = dateOfAcquisition;
            mAcquisitionCost = acquisitionCost;
            mServiceLife = serviceLife;
            mLastRecordedDate = dateOfAcquisition.plusYears(serviceLife).minusMonths(1);
            mResidualValue = acquisitionCost * RESIDUAL_PERCENT / 100; // 切り捨て
            mFixedAmountOfMonths = (int) Math.ceil((double) (acquisitionCost - mResidualValue) / (serviceLife * 12));
            mUndepreciatedBalance = acquisitionCost - mResidualValue;

            mRecordMap = new TreeMap<LocalDate, Integer>(); // 償却日でソートされる
        }

        /**
         * この固定資産の現在の価値を返します
         */
        private int presentValue() {
            return mResidualValue + mUndepreciatedBalance;
        }

        /**
         * その日が計上日であるかを返します(毎月。営業日無視)
         */
        private boolean isRecordedDate(LocalDate date) {
            // TODO: 営業日を考慮する
            // 償却が終わっている
            if (date.isAfter(mLastRecordedDate) || mRecordMap.containsKey(date))
                return false;
            // 対応する日がない
            if (date.lengthOfMonth() < mDateOfAcquisition.getDayOfMonth())
                return date.getDayOfMonth() == date.lengthOfMonth();
            // 対応する日がある
            return date.getDayOfMonth() == mDateOfAcquisition.getDayOfMonth();
        }

        /**
         * 減価償却を計上します。引数で渡された日付が計上日でない場合、あるいは未償却残高がすでにない場合は何もせず０を返します
         * @param date 計上日
         * @return 計上月額
         */
        private int record(LocalDate date) {
            if (!isRecordedDate(date)) { return 0; }
            if (mUndepreciatedBalance <= 0) { return 0; }
            int amount = mFixedAmountOfMonths;
            amount = mUndepreciatedBalance < amount ? mUndepreciatedBalance : amount;
            mUndepreciatedBalance -= amount;
            mRecordMap.put(date, mUndepreciatedBalance); // 記録
            return amount;
        }

        private LocalDate dateOfAcquisition() {
            return mDateOfAcquisition;
        }

        /**
         * 状態を表形式で表示します
         * @return 最終計上日
         */
        LocalDate print() {
            System.out.printf("get:%s, all-amount:%d円, per-amount:%d, life:%d年%n", mDateOfAcquisition, mAcquisitionCost,
                    mFixedAmountOfMonths, mServiceLife);

            mRecordMap.forEach(new BiConsumer<LocalDate, Integer>() {

                @Override
                public void accept(LocalDate depreciateDate, Integer undepreciatedAmount) {
                    System.out.printf("%s　%d円%n", depreciateDate, undepreciatedAmount);
                }
            });

            System.out.printf("最終償却日の合致:%b%n",
                    mRecordMap.lastKey().equals(mLastRecordedDate));

            return mRecordMap.lastKey();
        }
    }

}
