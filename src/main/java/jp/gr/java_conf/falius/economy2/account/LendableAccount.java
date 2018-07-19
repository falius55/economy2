package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface LendableAccount<T extends Enum<T> & AccountTitle>  extends Account<T>  {

    /**
     * 貸金処理を行う
     */
    public LendableAccount<T> lend(int amount);

    /**
     * 返済を受けた時の処理を行います
     */
    public LendableAccount<T> repaid(int amount);

}
