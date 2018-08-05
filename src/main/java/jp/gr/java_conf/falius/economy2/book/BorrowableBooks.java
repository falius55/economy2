package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;

/**
 *
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface BorrowableBooks<T extends Enum<T> & Title>  extends Books<T> {

    /**
     * 借金処理
     * @since 1.0
     */
    public BorrowableBooks<T> borrow(int amount);

    /**
     * 返済処理を行います
     * @since 1.0
     */
    public BorrowableBooks<T> repay(int amount);

}
