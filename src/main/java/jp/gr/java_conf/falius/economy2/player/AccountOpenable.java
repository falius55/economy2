package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.player.bank.Bank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 */
public interface AccountOpenable extends Entity {

    /**
     *
     * @return
     * @since 1.0
     */
    public Bank mainBank();

    /**
     *
     * @return
     * @since 1.0
     */
    public int cash();

    /**
     *
     * @return
     * @since 1.0
     */
    public int deposit();

    /**
     * 貯金します
     * 対象はメインバンク
     * 銀行が実行すると中央銀行に預けます
     * @since 1.0
     */
    public AccountOpenable saveMoney(int amount);

    /**
     * お金をおろします
     * 対象はメインバンク
     * 銀行が実行すると中央銀行からおろします
     * @since 1.0
     */
    public AccountOpenable downMoney(int amount);

}
