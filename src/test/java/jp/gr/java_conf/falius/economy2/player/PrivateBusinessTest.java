package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.market.Market;

public class PrivateBusinessTest {

    @BeforeClass
    public static void first() {
        Bank bank = new PrivateBank();
        bank.keep(1000000);
    }

    @AfterClass
    public static void end() {
        Market.INSTANCE.clear();
    }

    @After
    public void clearBusiness() {
        PrivateBusiness.clear();
    }

    @Test
    public void employerTest() {
        PrivateBusiness liblio =
                new PrivateBusiness(new WorkerParson(), Industry.LIBLIO, 10000);
        PrivateBusiness superMarket =
                new PrivateBusiness(new WorkerParson(), Industry.SUPER_MARKET, 10000);


        Worker worker = new WorkerParson();
        assertThat(Market.INSTANCE.employables().anyMatch(ep -> ep.has(worker)), is(false));

        worker.seekJob();
        assertThat(Market.INSTANCE.employables().anyMatch(ep -> ep.has(worker)), is(true));

        worker.retireJob();
        assertThat(Market.INSTANCE.employables().anyMatch(ep -> ep.has(worker)), is(false));

        worker.seekJob();
        assertThat(Market.INSTANCE.employables().anyMatch(ep -> ep.has(worker)), is(true));

        worker.seekJob();
        assertThat(Market.INSTANCE.employables().anyMatch(pb -> pb.has(worker)), is(true));
    }

    @Test
    public void distributionTest() {
        WorkerParson worker = new WorkerParson();
        int salary = 100000;
        worker.getSalary(salary);

        int initialExpenses = 100000;
        PrivateBusiness farmer =
                new PrivateBusiness(new WorkerParson(), Industry.FARMER, EnumSet.of(Product.RICE), initialExpenses);
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());

        PrivateBusiness maker =
                new PrivateBusiness(new WorkerParson(), Industry.FOOD_MAKER, initialExpenses);
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());

        PrivateBusiness coop =
                new PrivateBusiness(new WorkerParson(), Industry.SUPER_MARKET, initialExpenses);

        Product product = Product.RICE_BALL;
        int require = 3;
        OptionalInt optPrice = worker.buy(product, require);
        int price = optPrice.getAsInt();
        assertThat(price, is(not(0)));

        int countToEndOfMonth = Market.INSTANCE.nowDate().lengthOfMonth() - Market.INSTANCE.nowDate().getDayOfMonth();
        IntStream.range(0, countToEndOfMonth + 10).forEach(n -> Market.INSTANCE.nextDay());
        System.out.println(farmer.accountBook().toString());
        System.out.println(maker.accountBook().toString());
        System.out.println(coop.accountBook().toString());

        int farmerSales = farmer.accountBook().get(PrivateBusinessAccountTitle.SALES);
        int makerSales = maker.accountBook().get(PrivateBusinessAccountTitle.SALES);
        int coopSales = coop.accountBook().get(PrivateBusinessAccountTitle.SALES);
        assertThat(farmerSales, is(greaterThan(0)));

        assertThat(farmerSales, is(maker.accountBook().get(PrivateBusinessAccountTitle.PURCHESES)));
        assertThat(makerSales, is(coop.accountBook().get(PrivateBusinessAccountTitle.PURCHESES)));
        assertThat(coopSales, is(price));
        int coopCash = coop.accountBook().get(PrivateBusinessAccountTitle.CASH);
        assertThat(coopCash, is(price));

        accountCheck(farmer);
        accountCheck(maker);
        accountCheck(coop);
    }

    private void accountCheck(PrivateBusiness pb) {
        int expense = pb.accountBook().get(AccountType.EXPENSE);
        int revenue = pb.accountBook().get(AccountType.REVENUE);
        int liabilities = pb.accountBook().get(AccountType.LIABILITIES);
        int equity = pb.accountBook().get(AccountType.EQUITY);
        int assets = pb.accountBook().get(AccountType.ASSETS);

        int benefit = revenue - expense;

        assertThat(expense + benefit, is(revenue));
        assertThat(assets, is(liabilities + equity + benefit));
    }

}
