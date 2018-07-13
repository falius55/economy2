package jp.gr.java_conf.falius.economy2.account;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.util.table.TableBuilder;
import jp.gr.java_conf.falius.util.timer.Timer;

public abstract class AbstractDoubleEntryAccount<T extends Enum<T> & AccountTitle>
        extends AbstractAccount<T> implements DoubleEntryAccount<T> {
    private final Map<AccountType, Map<T, Integer>> mAccountsBook; // 帳簿(EnumMap) 科目種別から、勘定科目からその金額へのマップ、へのマップ
    private final Set<FixedAsset> mFixedAssets; // TODO:建物は科目が別なので、別に保持する

    protected AbstractDoubleEntryAccount(Class<T> clazz) {
        mAccountsBook = initBook(clazz);
        mFixedAssets = new HashSet<FixedAsset>();
    }

    // 帳簿を初期化する
    private Map<AccountType, Map<T, Integer>> initBook(Class<T> clazz) {
        // 0.13ミリ秒
        return Arrays.stream(clazz.getEnumConstants())
            .collect(Collectors.groupingBy( // 分類関数の戻り値をキーとしたマップに各要素を格納するが、同じ戻り値となった要素は第三引数でひとつのコレクション等に格納する
                        e -> e.type(), // 分類関数 戻り値をキーとしてマップに格納される
                        () -> new EnumMap<AccountType, Map<T, Integer>>(AccountType.class), // 中間生成物を作成する処理 要は、何に入れる 一番上のマップの形式
                        Collectors.toMap( // 分類したものをさらにひとつのマップにまとめる(リダクションする)ためのコレクター
                            e -> e, // キーを作るための関数
                            e -> 0, // 値を作るための関数
                            (t, u) -> t, // キーが同じ要素が出てきたらどうするか
                            () -> new EnumMap<T, Integer>(clazz)) // 中間生成物
                        ));
    }

    /**
     * 帳簿に記入します
     * @param rl 記入箇所。借方(LEFT)か貸し方(RIGHT)か
     * @param item 勘定科目
     * @param amount 金額
     */
    protected final void add(AccountType.RL rl, T item, int amount) {
        if (item.type().rl().equals(rl))
            increase(item, amount);
        else
            decrease(item, amount);
    }

    /**
     * 標準資産科目(defaultItem()によって定義)を相手科目として、指定された科目を増加させます
     * @param item 勘定科目
     * @param amount 金額
     * @throws IllegalArgumentException サブタイプで定義した標準科目が資産科目でない場合
     */
    protected final void add(T item, int amount) {
        T defaultItem = defaultItem();
        if (!defaultItem.type().equals(AccountType.ASSETS)) throw new IllegalArgumentException("defaultItem is not Assets");
        add(item.type().rl(), item, amount);
        add(item.type().rl().inverse(), defaultItem, amount);
    }

    /**
     * 借方に記入します
     * @param item 勘定科目
     * @param amount 金額
     */
    protected final void addLeft(T item, int amount) {
        add(AccountType.RL.LEFT, item, amount);
    }

    /**
     * 貸方に記入します
     * @param item 勘定科目
     * @param amount 金額
     */
    protected final void addRight(T item, int amount) {
        add(AccountType.RL.RIGHT, item, amount);
    }

    // 特定科目の金額を増加する
    @Override
    protected final void increase(T item, int amount) {
        Map<T, Integer> itemMap = mAccountsBook.get(item.type());
        itemMap.put(item, itemMap.get(item).intValue() + amount);
    }

    protected final void decrease(T item, int amount) {
        Map<T, Integer> itemMap = mAccountsBook.get(item.type());
        itemMap.put(item, itemMap.get(item).intValue() - amount);
    }

    /**
     * 指定した科目種別の総額を計算します
     * @param type 科目種別
     * @return 集計結果
     */
    @Override
    public final int get(AccountType type) {
        return mAccountsBook.get(type).values().stream()
            .mapToInt(integer -> integer.intValue()).sum();
    }

    /**
     * 指定した勘定科目の金額を返します
     * @param item 勘定科目
     * @return 指定した勘定科目の金額
     */
    @Override
    public final int get(T item) {
        Map<T, Integer> itemMap = mAccountsBook.get(item.type());
        return itemMap.get(item).intValue();
    }

    /**
     * 帳簿内容の文字列表現を返します
     */
    @Override
    public String toString() {
        return mAccountsBook.toString();
    }


    // 以下は固定資産

    /**
     * 固定資産を追加します
     * @param mDateOfAcquisition 取得日
     * @param mAcquisitionCost 取得原価
     * @param mServiceLife 耐用年数
     */
    protected final void addFixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
        mFixedAssets.add(new FixedAsset(dateOfAcquisition, acquisitionCost, serviceLife));
    }

    /**
     * 所有している固定資産全てにおいて、減価償却の処理を行います
     * より具体的には、dateが償却日である固定資産のみ減価償却し、その償却費の総額を返します。
     * ただし、帳簿への記帳処理は行いません
     * @param date 記入日
     * @return その日の償却額
     */
    protected final int recordFixedAssets(LocalDate date) {
        return mFixedAssets.stream()
            .collect(Collectors.summingInt(asset -> asset.record(date)));
    }

    /**
     * 保有している固定資産の現在価値の総額を計算します
     */
    protected final int fixedAssetsValue() {
        return mFixedAssets.stream()
            .collect(Collectors.summingInt(FixedAsset::presentValue));
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
        private Map<LocalDate, Integer> mRecordMap; // 償却日から未償却額へのマップ

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
            mFixedAmountOfMonths = (int)Math.ceil((double)(acquisitionCost - mResidualValue) / (serviceLife * 12));
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
            if (date.isAfter(mLastRecordedDate) || mRecordMap.containsKey(date)) return false;
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
            if (!isRecordedDate(date)) return 0;
            if (mUndepreciatedBalance <= 0) return 0;
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
        private LocalDate print() {
            System.out.printf("get:%s, all-amount:%d円, per-amount:%d, life:%d年%n", mDateOfAcquisition, mAcquisitionCost, mFixedAmountOfMonths, mServiceLife);
            TableBuilder tb = new TableBuilder("償却回", "日付", "金額");
            int cnt = 1;
            for (LocalDate date : mRecordMap.keySet())
                tb.insert(cnt++)
                    .add(1, date)
                    .add(2, mRecordMap.get(date));
            tb.print();
            System.out.printf("最終償却日の合致:%b%n", ((SortedMap<LocalDate, Integer>)mRecordMap).lastKey().equals(mLastRecordedDate));

            return ((SortedMap<LocalDate, Integer>)mRecordMap).lastKey();
        }
    }

    /**
     * テスト用メソッド
     */
    public void test_fixedAssets(int count) {
        // 固定資産の追加と減価償却
        LocalDate date = LocalDate.now();
        int depreciatedBalance = 0;
        for (int i = 0; i < 100; i++) {
            addFixedAsset(date, 100000*(i+1), i%10+1);
            depreciatedBalance += recordFixedAssets(date);
            date = date.plusDays(1);
        }

        depreciatedBalance += Stream.iterate(date, d -> d.plusDays(1)).limit(3000)
            .mapToInt(this::recordFixedAssets)
            .sum();

        Timer timer = new Timer();
        // 結果の表示
        timer.start("print()");
        mFixedAssets.stream().sorted((a1, a2) -> a1.dateOfAcquisition().compareTo(a2.mDateOfAcquisition)) // 取得日でソート
            .map(FixedAsset::print) // 最終計上日でマップされる
            .collect(Collectors.maxBy((d1, d2) -> d1.compareTo(d2))).get();
        timer.end("print()");
        System.out.printf("現在日: %s, 減価償却累計額:%d%n", date, depreciatedBalance);

        timer.start("addFixedAsset()");
        addFixedAsset(date, 100000, 8);
        timer.end("addFixedAsset()");
        timer.start("plusDays");
        date.plusDays(1);
        timer.end("plusDays");
        timer.start("record");
        recordFixedAssets(date);
        timer.end("record");
    }

}
