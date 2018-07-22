package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.book.BorrowableBooks;

public interface Borrowable extends Entity {

    public BorrowableBooks<?> books();

    public boolean borrow(int amount);

    /**
     * 借金を返済します
     */
    public void repay(int amount);

}
