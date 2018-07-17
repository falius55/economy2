package jp.gr.java_conf.falius.economy2.player.gorv;

import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.player.Entity;
import jp.gr.java_conf.falius.economy2.player.PrivateEntity;

public interface Government extends Entity {

    /**
     * 国債を発行する。
     * @param amount
     * @return
     */
    public Government issueBonds(int amount);

    /**
     * 国債を償還する。
     * @param amount
     * @return
     */
    public Government redeemBonds(int amount);

    /**
     * 徴税する。
     * @param entity
     * @return
     */
    public Government collectTaxes(PrivateEntity entity);

    /**
     * 公共事業を発注する。
     * @param product
     * @return
     */
    public Government order(Product product);

}
