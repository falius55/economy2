package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.book.InstallmentReceivableBooks;

public interface InstallmentReceivable extends Entity {

    public InstallmentReceivableBooks<?> books();
}
