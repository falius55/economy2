package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.book.LendableBooks;

public interface Lendable extends Entity {

    public boolean canLend(int amount);

    public LendableBooks<?> books();

}
