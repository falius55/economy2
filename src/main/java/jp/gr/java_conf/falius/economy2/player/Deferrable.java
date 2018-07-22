package jp.gr.java_conf.falius.economy2.player;

import java.util.Optional;

import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.loan.Deferment;

/**
 * 買掛、売掛ができる
 * @author "ymiyauchi"
 *
 */
public interface Deferrable extends Entity {

    public Optional<Deferment> saleByReceivable(Product product, int require);

}
