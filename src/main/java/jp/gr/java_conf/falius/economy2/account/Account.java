package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Account {

    /**
     *
     * @return
     * @since 1.0
     */
    public Bank bank();

    /**
     *
     * @return
     * @since 1.0
     */
    public int amount();

    /**
     *
     * @param target
     * @param amount
     * @return
     * @since 1.0
     */
    public int transfer(Account target, int amount);

    /**
     *
     * @param central
     * @param amount
     * @return
     * @since 1.0
     */
    public int transfer(CentralBank central, int amount);

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public int increase(int amount);

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public int decrease(int amount);
}
