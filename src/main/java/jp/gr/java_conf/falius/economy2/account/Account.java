package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;

/**
 * 会計帳簿を表すインターフェース
 * @param T 科目一覧の列挙型
 */
public interface Account<T extends Enum<T> & AccountTitle> {

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

    /**
     * 引数の会計を、自分の会計に吸収併合する。結婚、合併など
     */
    public Account<T> merge(Account<T> another);

    /**
     * お金を銀行に預けた時の処理を行います
     */
    public Account<T> saveMoney(int amount);

    /**
     * お金をおろした時の処理を行います
     */
    public Account<T> downMoney(int amount);

    /**
     * 貸金処理を行います
     */
    public Account<T> lend(int amount);

    /**
     * 借金処理を行います
     */
    public Account<T> borrow(int amount);

    /**
     * 返済処理を行います
     */
    public Account<T> repay(int amount);

    /**
     * 返済を受けた時の処理を行います
     */
    public Account<T> repaid(int amount);

}
