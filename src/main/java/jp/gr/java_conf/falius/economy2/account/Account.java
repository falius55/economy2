package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;

/**
 * 会計帳簿を表すインターフェース
 * @param T 科目一覧の列挙型
 */
public interface Account<T extends Enum<T> & AccountTitle> {
    /**
     * 引数の会計を、自分の会計に吸収併合する。結婚、合併など
     */
    public Account<T> merge(Account<T> another);

    /**
     * 扱っている科目一覧を返します
     */
    public T[] items();

    /**
     * 指定がないときに増減させる標準資産科目を返します。通常は現金を想定していますが、サブタイプごとに定義してください
     * @return 標準資産科目
     */
    public T defaultItem();

    /**
     * 標準資産科目(defaultItem()によって定義)を相手科目として、指定された科目を増加させます
     * @param item 勘定科目
     * @param amount 金額
     * @throws IllegalArgumentException サブタイプで定義した標準科目が資産科目でない場合
     */
    public void add(T item, int amount);

    /**
     * お金を銀行に預けた時の処理を行います
     */
    public Account<T> saveMoney(int amount);

    /**
     * お金をおろした時の処理を行います
     */
    public Account<T> downMoney(int amount);
    /**
     * 借金処理を行います
     */
    public Account<T> borrow(int amount);
    /**
     * 返済処理を行います
     */
    public Account<T> repay(int amount);
    /**
     * 貸金処理を行います
     */
    public Account<T> lend(int amount);
    /**
     * 返済を受けた時の処理を行います
     */
    public Account<T> repaid(int amount);

    default public <E extends AccountTitle> Account<T> saleBy(E item, int amount) {
        throw new UnsupportedOperationException();
    }

    /**
     * 納税処理を行います
     * 公的機関ではサポートされません
     * @throws UnssuportedOperationException 公的機関の会計で実行された場合
     */
    public Account<T> payTax(int amount);

    /**
     * 指定した科目種別の総額を集計します
     * @param type 科目種別
     * @return 集計結果
     */
    public int get(AccountType type);

    /**
     * 指定した勘定科目の金額を返します
     * @param item 勘定科目
     * @return 指定した勘定科目の金額
     */
    public int get(T item);

}
