package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface LendableBooks<T extends Enum<T> & AccountTitle>  extends Books<T>  {

    /**
     * 貸金処理を行う
     */
    public LendableBooks<T> lend(int amount);

    /**
     * 返済を受けた時の処理を行います
     */
    public LendableBooks<T> repaid(int amount);

}
