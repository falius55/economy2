package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface AccountOpenableAccount<T extends Enum<T> & AccountTitle>  extends Account<T>  {

    /**
     * お金を口座に預けたときの処理
     * @param amount
     * @return
     */
    public AccountOpenableAccount<T> saveMoney(int amount);

    /**
     * お金を口座から下ろしたときの処理
     * @param amount
     * @return
     */
    public AccountOpenableAccount<T> downMoney(int amount);
}
