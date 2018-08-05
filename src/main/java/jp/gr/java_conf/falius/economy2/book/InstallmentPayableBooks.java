package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;

public interface InstallmentPayableBooks<T extends Enum<T> & Title> extends AccountOpenableBooks<T> {

    public InstallmentPayableBooks<T> payInstallment(int amount);
}
