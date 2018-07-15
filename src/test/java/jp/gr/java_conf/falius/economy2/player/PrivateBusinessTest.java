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
import jp.gr.java_conf.falius.economy2.market.MarketInfomation;

public class PrivateBusinessTest {

    @BeforeClass
    public static void first() {
        Bank bank = new PrivateBank();
    }

    @AfterClass
    public static void end() {
        PrivateBank.clear();
    }

    @After
    public void clearBusiness() {
        PrivateBusiness.clear();
    }

    @Test
    public void employerTest() {
        PrivateBusiness liblio =
                new PrivateBusiness(Industry.LIBLIO, Industry.LIBLIO.products(), 10000);
        PrivateBusiness superMarket =
                new PrivateBusiness(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products(), 10000);


        Worker worker = new WorkerParson();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(false));

        worker.seekJob();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(true));

        worker.retireJob();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(false));

        worker.seekJob();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(true));

        worker.seekJob();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(true));
    }

    @Test
    public void distributionTest() {
        int initialExpenses = 100000;
        PrivateBusiness farmer =
                new PrivateBusiness(Industry.FARMER, EnumSet.of(Product.RICE), initialExpenses);
        IntStream.range(0, 380).forEach(n -> MarketInfomation.INSTANCE.nextDay());

        PrivateBusiness maker =
                new PrivateBusiness(Industry.FOOD_MAKER, Industry.FOOD_MAKER.products(), initialExpenses);
        IntStream.range(0, 5).forEach(n -> MarketInfomation.INSTANCE.nextDay());

        PrivateBusiness coop =
                new Retail(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products(), initialExpenses);

        WorkerParson worker = new WorkerParson();

        int salary = 100000;
        worker.getPaied(salary);

        Product product = Product.RICE_BALL;
        int require = 3;
        OptionalInt optPrice = worker.buy(product, require);
        int price = optPrice.getAsInt();
        assertThat(price, is(not(0)));

        farmer.calcPurchase();
        maker.calcPurchase();
        coop.calcPurchase();

        System.out.println(farmer.account().toString());
        System.out.println(maker.account().toString());
        System.out.println(coop.account().toString());
        System.out.println(price);

        int farmerSales = farmer.account().get(PrivateBusinessAccountTitle.SALES);
        int makerSales = maker.account().get(PrivateBusinessAccountTitle.SALES);
        int coopSales = coop.account().get(PrivateBusinessAccountTitle.SALES);
        assertThat(farmerSales, is(greaterThan(0)));

        assertThat(farmerSales, is(maker.account().get(PrivateBusinessAccountTitle.PURCHESES)));
        assertThat(makerSales, is(coop.account().get(PrivateBusinessAccountTitle.PURCHESES)));
        assertThat(coopSales, is(price));
        int coopCash = coop.account().get(PrivateBusinessAccountTitle.CASH);
        assertThat(coopCash, is(price));

        accountCheck(farmer);
        accountCheck(maker);
        accountCheck(coop);
    }

    private void accountCheck(PrivateBusiness pb) {
        int expense = pb.account().get(AccountType.EXPENSE);
        int revenue = pb.account().get(AccountType.REVENUE);
        int liabilities = pb.account().get(AccountType.LIABILITIES);
        int equity = pb.account().get(AccountType.EQUITY);
        int assets = pb.account().get(AccountType.ASSETS);

        int benefit = revenue - expense;

        assertThat(expense + benefit, is(revenue));
        assertThat(assets, is(liabilities + equity + benefit));
    }

}
