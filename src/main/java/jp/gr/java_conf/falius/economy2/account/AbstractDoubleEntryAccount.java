package jp.gr.java_conf.falius.economy2.account;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;

public abstract class AbstractDoubleEntryAccount<T extends Enum<T> & AccountTitle>
        extends AbstractAccount<T> implements DoubleEntryAccount<T> {
    private final Map<AccountType, Map<T, Integer>> mAccountsBook; // 帳簿(EnumMap) 科目種別から、勘定科目からその金額へのマップ、へのマップ
    private final FixedAssetManager mFixedAssetManager;

    protected AbstractDoubleEntryAccount(Class<T> clazz) {
        mAccountsBook = initBook(clazz);
        mFixedAssetManager = new FixedAssetManager();
    }

    // 帳簿を初期化する
    private Map<AccountType, Map<T, Integer>> initBook(Class<T> clazz) {
        // 0.13ミリ秒
        return Arrays.stream(clazz.getEnumConstants())
                .collect(Collectors.groupingBy( // 分類関数の戻り値をキーとしたマップに各要素を格納するが、同じ戻値となった要素は第三引数でひとつのコレクション等に格納する
                        e -> e.type(), // 分類関数 戻り値をキーとしてマップに格納される(同じ値なら同じグループ:科目種別のマップ)
                        () -> new EnumMap<AccountType, Map<T, Integer>>(AccountType.class), // 一番上のマップの形式
                        Collectors.toMap( // 分類したものをさらにひとつのマップにまとめる(リダクションする)ためのコレクター(勘定科目からその金額へのマップ)
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
    private final void add(AccountType.RL rl, T item, int amount) {
        if (item.type().rl().equals(rl)) {
            increase(item, amount);
        } else {
            decrease(item, amount);
        }
    }

    /**
     * 標準資産科目(defaultItem()によって定義)を相手科目として、指定された科目を増加させます
     * @param item 勘定科目
     * @param amount 金額
     * @throws IllegalArgumentException サブタイプで定義した標準科目が資産科目でない場合
     */
    @Override
    public final void add(T item, int amount) {
        T defaultItem = defaultItem();
        if (!defaultItem.type().equals(AccountType.ASSETS)) { throw new IllegalArgumentException("defaultItem is not Assets"); }
        add(item.type().rl(), item, amount);
        add(item.type().rl().inverse(), defaultItem, amount);
    }

    /**
     * 借方に記入します
     * @param item 勘定科目
     * @param amount 金額
     */
    @Override
    public final void addLeft(T item, int amount) {
        add(AccountType.RL.LEFT, item, amount);
    }

    /**
     * 貸方に記入します
     * @param item 勘定科目
     * @param amount 金額
     */
    @Override
    public final void addRight(T item, int amount) {
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
        mFixedAssetManager.add(dateOfAcquisition, acquisitionCost, serviceLife);
        // mFixedAssets.add(new FixedAsset(dateOfAcquisition, acquisitionCost, serviceLife));
    }

    /**
     * 所有している固定資産全てにおいて、減価償却の処理を行います
     * より具体的には、dateが償却日である固定資産のみ減価償却し、その償却費の総額を返します。
     * ただし、帳簿への記帳処理は行いません
     * @param date 記入日
     * @return その日の償却額
     */
    protected final int recordFixedAssets(LocalDate date) {
        return mFixedAssetManager.record(date);
    }

    /**
     * 保有している固定資産の現在価値の総額を計算します
     */
    protected final int fixedAssetsValue() {
        return mFixedAssetManager.presentValue();
    }

}
