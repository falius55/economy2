package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface EmployableBooks<T extends Enum<T> & AccountTitle> extends Books<T>  {

    public EmployableBooks<T> paySalary(int amount);

    /**
     * 預かり金を納税します。
     */
    public EmployableBooks<T> payIncomeTax(int amount);

}
