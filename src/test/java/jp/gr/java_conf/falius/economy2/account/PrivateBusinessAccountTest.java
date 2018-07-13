package jp.gr.java_conf.falius.economy2.account;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;

public class PrivateBusinessAccountTest {

    @Test
    public void mainTest() {
        assertThat(55, is(55));

        PrivateBusinessAccount account = PrivateBusinessAccount.newInstance();
        account.add(PrivateBusinessAccountTitle.SALES, 2000);
        System.out.println(account);
        account.test_fixedAssets(1000);
        Product.printAll();
    }
}
