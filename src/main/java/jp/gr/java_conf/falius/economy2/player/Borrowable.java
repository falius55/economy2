package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.book.BorrowableBooks;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Borrowable extends Entity {

    /**
     * @since   1.0
     */
    public BorrowableBooks<?> books();

    /**
     * @since   1.0
     */
    public boolean borrow(int amount);

    /**
     * 借金を返済します
     * @since   1.0
     */
    public void repay(int amount);

}
