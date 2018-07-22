package jp.gr.java_conf.falius.economy2.player.gorv;

import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.player.Entity;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Government extends Entity {

    /**
     * 国債を発行する。
     * @param amount
     * @return
     * @since 1.0
     */
    public Government issueBonds(int amount);

    /**
     * 国債を償還する。
     * @param amount
     * @return
     * @since 1.0
     */
    public Government redeemBonds(int amount);

    /**
     * 徴税する。
     * @return
     * @since 1.0
     */
    public Government collectTaxes();

    /**
     * 公共事業を発注する。
     * @param product
     * @return
     * @since 1.0
     */
    public Government order(Product product);

}
