package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;

/**
 *
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface BankBooks<T extends Enum<T> & Title> extends EmployableBooks<T> {

    /**
     * お金を預かる
     * @since 1.0
     */
    public BankBooks<T> keep(int amount);

    /**
     * 預金返済処理
     * @since 1.0
     */
    public BankBooks<T> paidOut(int amount);

    /**
     * 国債を引き受けます。
     * @param amount
     * @return
     * @since 1.0
     */
    public BankBooks<T> acceptGovernmentBond(int amount);

    /**
     * 保有国債が償還されます。
     * @param amount
     * @return
     * @since 1.0
     */
    public BankBooks<T> redeemedGovernmentBond(int amount);

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public BankBooks<T> buyGorvementBond(int amount);

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public BankBooks<T> sellGovernmentBond(int amount);

    /**
     *
     * @param accountOpenable
     * @return
     * @since 1.0
     */
    public BankBooks<T> createAccount(AccountOpenable accountOpenable);
}
