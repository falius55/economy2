package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface BorrowableAccount<T extends Enum<T> & AccountTitle>  extends Account<T> {

    /**
     * 借金処理
     */
    public BorrowableAccount<T> borrow(int amount);

}
