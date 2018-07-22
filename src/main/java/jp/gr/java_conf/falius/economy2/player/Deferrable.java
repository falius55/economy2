package jp.gr.java_conf.falius.economy2.player;

import java.util.Optional;

import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.loan.Deferment;

/**
 * 買掛、売掛ができる
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Deferrable extends Entity {

    /**
     *
     * @param product
     * @param require
     * @return
     * @since 1.0
     */
    public Optional<Deferment> saleByReceivable(Product product, int require);

}
