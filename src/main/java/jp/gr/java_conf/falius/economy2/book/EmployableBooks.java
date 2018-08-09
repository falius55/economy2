package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;

/**
 *
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface EmployableBooks<T extends Enum<T> & Title> extends Books<T>  {

    /**
     *
     * @param amount 額面
     * @return 手取額
     * @since 1.0
     */
    public int paySalary(int amount);

    /**
     * 預かり金を納税します。
     * @since 1.0
     */
    public EmployableBooks<T> payIncomeTax(int amount);

}
