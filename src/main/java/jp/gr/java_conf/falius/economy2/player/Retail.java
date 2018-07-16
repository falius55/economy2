package jp.gr.java_conf.falius.economy2.player;

import java.util.Set;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;

public class Retail extends PrivateBusiness {

    public Retail(Worker founder, Industry industry, Set<Product> products, int initialExpenses) {
        super(founder, industry, products, initialExpenses);
    }

    public Retail(Worker founder, Industry industry, int initialExpenses) {
        super(founder, industry, initialExpenses);
    }

    @Override
    PrivateBusinessAccountTitle saleAccount() {
        return PrivateBusinessAccountTitle.CASH;
    }
}
