package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface MutableAccount<T extends Enum<T> & AccountTitle>  extends Account<T> {

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
}
