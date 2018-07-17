package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface LendableAccount<T extends Enum<T> & AccountTitle>  extends Account<T>  {

    /**
     * 貸金処理を行う
     */
    public LendableAccount<T> lend(int amount);

}
