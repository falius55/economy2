package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface EmployableAccount<T extends Enum<T> & AccountTitle> extends Account<T>  {

    public EmployableAccount<T> paySalary(int amount);

    /**
     * 預かり金を納税します。
     */
    public EmployableAccount<T> payIncomeTax(int amount);

}
