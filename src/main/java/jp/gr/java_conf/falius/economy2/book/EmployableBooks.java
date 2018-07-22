package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

/**
 *
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface EmployableBooks<T extends Enum<T> & AccountTitle> extends Books<T>  {

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public EmployableBooks<T> paySalary(int amount);

    /**
     * 預かり金を納税します。
     * @since 1.0
     */
    public EmployableBooks<T> payIncomeTax(int amount);

}
