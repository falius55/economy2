package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;

public interface InstallmentReceivableBooks<T extends Enum<T> & Title> extends AccountOpenableBooks<T> {

    public InstallmentReceivableBooks<T> receiveInstallment(int amount);

}
