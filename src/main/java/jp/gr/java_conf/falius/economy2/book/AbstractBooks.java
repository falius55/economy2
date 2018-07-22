package jp.gr.java_conf.falius.economy2.book;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;

/**
 * 会計帳簿を表すすべてのクラスの基底クラス
 * @param T 勘定科目一覧を定義した列挙型。AccountTitleインターフェースを実装していなければならない
 * @since 1.0
 */
public abstract class AbstractBooks<T extends Enum<T> & AccountTitle> implements MutableBooks<T> {
    private final Class<T> mClass;
    private final Map<AccountType, Map<T, Integer>> mAccountsBook; // 帳簿(EnumMap) 科目種別から、勘定科目からその金額へのマップ、へのマップ

    /**
     *
     * @param clazz
     * @since 1.0
     */
    protected AbstractBooks(Class<T> clazz) {
        mClass = clazz;
        mAccountsBook = initBook(clazz);
    }

    /**
     * 引数の会計を、自分の会計に吸収併合します。結婚、合併など
     * @since 1.0
     */
    @Override
    public AbstractBooks<T> merge(Books<T> another) {
        if (!(another instanceof AbstractBooks)) {
            throw new IllegalArgumentException();
        }
        for (T item : mClass.getEnumConstants()) {
            increase(item, another.get(item));
        }
        return this;
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
     * @since 1.0
     */
    public void clearBook() {
        mAccountsBook.values().stream()
                .forEach(m -> m.forEach((k, v) -> m.compute(k, (t, i) -> 0)));
    }

    /**
     * 特定科目の金額を増加します。
     * @param item
     * @param amount
     * @since 1.0
     */
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
     * @since 1.0
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
     * @since 1.0
     */
    @Override
    public final int get(T item) {
        Map<T, Integer> itemMap = mAccountsBook.get(item.type());
        return itemMap.get(item).intValue();
    }

    /**
     * @since 1.0
     */
    @Override
    public final int benefit() {
        int ret = get(AccountType.REVENUE)
                - get(AccountType.EXPENSE);
        return ret;
    }

    /**
     * 帳簿内容の文字列表現を返します
     * @since 1.0
     */
    @Override
    public String toString() {
        return mAccountsBook.toString();
    }
}
