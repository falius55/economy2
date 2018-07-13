package jp.gr.java_conf.falius.economy2.account;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;

public class AbstractDoubleEntryAccountTest {

    @Test
    public void addTest() {
        Account<PrivateBusinessAccountTitle> account = PrivateBusinessAccount.newInstance();
        account.add(PrivateBusinessAccountTitle.SALES, 2000);  // 売上が２０００円

        int sales = account.get(PrivateBusinessAccountTitle.SALES);  // 売上
        int checkingAccount = account.get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);  // 当座預金(標準資産科目)
        assertThat(sales, is(2000));
        assertThat(checkingAccount, is(2000));

        System.out.println(account);
    }

    @Test
    public void mainTest() {
        Product.printAll();
    }

}
