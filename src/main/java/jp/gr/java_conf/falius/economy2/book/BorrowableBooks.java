package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface BorrowableBooks<T extends Enum<T> & AccountTitle>  extends Books<T> {

    /**
     * 借金処理
     */
    public BorrowableBooks<T> borrow(int amount);

    /**
     * 返済処理を行います
     */
    public BorrowableBooks<T> repay(int amount);

}
