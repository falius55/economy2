package jp.gr.java_conf.falius.economy2.book;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.gr.java_conf.falius.economy2.market.Market;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
final class FixedAssetManager {
    private final Set<FixedAsset> mFixedAssets; // TODO:建物は科目が別なので、別に保持する
    private LocalDate mLastUpdate;

    FixedAssetManager() {
        mFixedAssets = new TreeSet<>();
        mLastUpdate = LocalDate.now(); // Nationなどstaticイニシャライザ内で実体化されるため、Market.INSTANCE.nowDate()を使うと初期化エラー
    }

    void clear() {
        mLastUpdate = LocalDate.now();
        mFixedAssets.clear();
    }

    /**
     * 固定資産を追加します
     * @param mDateOfAcquisition 取得日
     * @param mAcquisitionCost 取得原価
     * @param mServiceLife 耐用年数
     * @since 1.0
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
     * @since 1.0
     */
    private int record(LocalDate date) {
        return mFixedAssets.stream()
                .mapToInt(asset -> asset.record(date))
                .sum();
    }

    /**
     * @return 償却額合計
     * @since 1.0
     */
    int update() {
        LocalDate today = Market.INSTANCE.nowDate();
        int ret = Market.dateStream(mLastUpdate.plusDays(1), today.plusDays(1))
                .mapToInt(this::record)
                .sum();
        mLastUpdate = today;
        return ret;
    }

    /**
     * 保有している固定資産の現在価値の総額を計算します
     * @since 1.0
     */
    int presentValue() {
        return mFixedAssets.stream()
                .mapToInt(FixedAsset::presentValue)
                .sum();
    }

    /**
     * @since 1.0
     */
    void printAll() {
        mFixedAssets.forEach(FixedAsset::print);
    }

    /**
     * @since 1.0
     */
    int depreciatedBalance() {
        return mFixedAssets.stream()
                .mapToInt(FixedAsset::depreciatedBalance)
                .sum();
    }

    /**
     * @since 1.0
     */
    int unDepreciatedBalance() {
        return mFixedAssets.stream()
                .mapToInt(FixedAsset::unDepreciatedBalance)
                .sum();
    }

    // テスト用メソッド
    Set<FixedAsset> assets() {
        return Collections.unmodifiableSet(mFixedAssets);
    }

    /**
     * 固定資産の減価償却の計算を行うクラス
     * 土地は減価償却しないので土地以外
     * @since 1.0
     */
    static class FixedAsset implements Comparable<FixedAsset> {
        /**
         * 取得原価に対する残存価額の割合(%)
         */
        static final int RESIDUAL_PERCENT = 10;
        /**
         * 取得日
         */
        private final LocalDate mDateOfAcquisition;
        /**
         * 取得原価
         */
        private final int mAcquisitionCost;
        /**
         * 耐用年数
         */
        private final int mServiceLife;
        /**
         * 残存価額
         */
        private final int mResidualValue;
        /**
         * 定額法における、償却月額
         */
        private final int mFixedAmountOfMonths;
        /**
         * 償却日から、未償却額へのマップ
         */
        private final SortedMap<LocalDate, Integer> mRecordMap;

        /**
         * @param dateOfAcquisition 取得日
         * @param acquisitionCost 取得原価
         * @param serviceLife 耐用年数
         * @since 1.0
         */
        private FixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
            mDateOfAcquisition = dateOfAcquisition;
            mAcquisitionCost = acquisitionCost;
            mServiceLife = serviceLife;
            mResidualValue = acquisitionCost * RESIDUAL_PERCENT / 100; // 切り捨て
            mFixedAmountOfMonths = (int) Math.ceil((double) (acquisitionCost - mResidualValue) / (serviceLife * 12));

            int amortizable = acquisitionCost - mResidualValue; // 償却可能額
            mRecordMap = new TreeMap<LocalDate, Integer>(); //  償却日から残存額へのマップ 償却日でソートされる
            mRecordMap.put(dateOfAcquisition, amortizable);
        }

        /**
         * この固定資産の現在の価値を返します
         * @since 1.0
         */
        private int presentValue() {
            return mResidualValue + unDepreciatedBalance();
        }

        /**
         * この固定資産の償却額を返します。
         * @return
         * @since 1.0
         */
        private int depreciatedBalance() {
            // 取得原価　ー　未償却額　ー　残存価額
            return mAcquisitionCost - unDepreciatedBalance() - mResidualValue;
        }

        /**
         * この固定資産の未償却額を返します。
         * @return
         * @since 1.0
         */
        private int unDepreciatedBalance() {
            return mRecordMap.get(lastRecordedDate());
        }

        private LocalDate lastRecordedDate() {
            return mRecordMap.lastKey();
        }

        /**
         * その日が新規計上すべき日であるかを返します(毎月。営業日無視)
         * @since 1.0
         */
        private boolean shouldRecord(LocalDate date) {
            if (date.isBefore(mDateOfAcquisition)) {
                return false;
            } // 取得日より前
              // TODO: 営業日を考慮する
              // 償却が終わっている
            if (unDepreciatedBalance() <= 0) {
                return false;
            }
            if (mRecordMap.containsKey(date)) {
                return false;
            }
            // 対応する日がない
            if (date.lengthOfMonth() < mDateOfAcquisition.getDayOfMonth()) {
                return date.getDayOfMonth() == date.lengthOfMonth(); // その日が月末かどうか
            }
            // 対応する日がある
            return date.getDayOfMonth() == mDateOfAcquisition.getDayOfMonth(); // その日が取得日と同じかどうか
        }

        /**
         * 減価償却を計上します。引数で渡された日付が計上日でない場合、あるいは未償却残高がすでにない場合は何もせず０を返します
         * @param date 計上日
         * @return 計上月額
         * @since 1.0
         */
        private int record(LocalDate date) {
            if (!shouldRecord(date)) {
                return 0;
            }
            int undepreciatedBalance = unDepreciatedBalance();
            int amount = Math.min(undepreciatedBalance, mFixedAmountOfMonths);
            mRecordMap.put(date, undepreciatedBalance - amount); // 記録
            return amount;
        }

        /**
         *
         * @return
         * @since 1.0
         */
        private LocalDate dateOfAcquisition() {
            return mDateOfAcquisition;
        }

        /**
         * 状態を表形式で表示します
         * @return 最終計上日
         * @since 1.0
         */
        void print() {
            System.out.printf("get:%s, all-amount:%d円, per-amount:%d, life:%d年%n", mDateOfAcquisition, mAcquisitionCost,
                    mFixedAmountOfMonths, mServiceLife);
            mRecordMap.forEach((date, amount) -> System.out.printf("%s %d円%n", date, amount));
        }

        /**
         * @since 1.0
         */
        @Override
        public int compareTo(FixedAsset another) {
            return this.dateOfAcquisition().compareTo(another.dateOfAcquisition());
        }

        // テスト用メソッド
        Map<LocalDate, Integer> recordMap() {
            return Collections.unmodifiableMap(mRecordMap);
        }
    }

}
