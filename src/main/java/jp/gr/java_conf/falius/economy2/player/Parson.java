package jp.gr.java_conf.falius.economy2.player;

import java.util.OptionalInt;

import jp.gr.java_conf.falius.economy2.enumpack.Product;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Parson extends Entity {

    /**
     *
     * @param product
     * @return
     * @since 1.0
     */
    public OptionalInt buy(Product product);

    /**
     *
     * @param product
     * @param require
     * @return
     * @since 1.0
     */
    public OptionalInt buy(Product product, int require);

}
