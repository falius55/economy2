package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.enumpack.Product;

public interface Parson extends Entity {

    public void buy(Product product);

    public void buy(Product product, int require);

    public int cash();

    public int deposit();

}
