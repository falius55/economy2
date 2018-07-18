package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class PrivateBusinessTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    @Test
    public void employerTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        WorkerParson founder2 = new WorkerParson();
        int capital = cbank.paySalary(founder);
        int capital2 = cbank.paySalary(founder2);

        PrivateBusiness liblio = founder.establish(Industry.LIBLIO, capital).get();
        PrivateBusiness superMarket = founder2.establish(Industry.SUPER_MARKET, capital2).get();

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
    public void establishTest() {
        CentralBank centralBank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = centralBank.paySalary(worker);
        int initial = salary / 2;

        PrivateBusiness farmer = worker.establish(Industry.FARMER, initial).get();
        assertThat(farmer.accountBook().get(PrivateBusinessAccountTitle.CAPITAL_STOCK), is(initial));
        assertThat(farmer.deposit(), is(initial));
    }

    @Test
    public void borrowTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int capital = cbank.paySalary(worker);
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();

        farmer.borrow(capital);
        assertThat(farmer.deposit(), is(capital * 2));
        assertThat(farmer.accountBook().get(PrivateBusinessAccountTitle.LOANS_PAYABLE), is(capital));
        assertThat(farmer.accountBook().get(PrivateBusinessAccountTitle.CAPITAL_STOCK), is(capital));
    }

    @Test
    public void distributionTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        WorkerParson worker2 = new WorkerParson();
        WorkerParson worker3 = new WorkerParson();
        int capital = cbank.paySalary(worker);
        int capital2 = cbank.paySalary(worker2);
        int capital3 = cbank.paySalary(worker3);
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness maker = worker2.establish(Industry.FOOD_MAKER, capital2).get();
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness coop = worker3.establish(Industry.SUPER_MARKET, capital3).get();

        cbank.paySalary(worker);

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
