package jp.gr.java_conf.falius.economy2.player;

import java.util.OptionalInt;

import jp.gr.java_conf.falius.economy2.enumpack.Product;

public interface Parson extends Entity {

    public OptionalInt buy(Product product);

    public OptionalInt buy(Product product, int require);

    public int cash();

    public int deposit();

}
