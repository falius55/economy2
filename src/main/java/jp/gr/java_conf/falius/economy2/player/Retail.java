package jp.gr.java_conf.falius.economy2.player;

import java.util.Set;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;

public class Retail extends PrivateBusiness {

    public Retail(Industry industry, Set<Product> products) {
        super(industry, products);
    }

    PrivateBusinessAccountTitle saleAccount() {
        return PrivateBusinessAccountTitle.CASH;
    }
}
