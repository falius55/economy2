package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankTitle;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.TitleType;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class PrivateBusinessTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    private void checkAccount(PrivateBusiness pb) {
        assertThat(pb.mainBank().account(pb).amount(),
                is(pb.books().get(PrivateBusinessTitle.CHECKING_ACCOUNTS)));

        int expense = pb.books().get(TitleType.EXPENSE);
        int revenue = pb.books().get(TitleType.REVENUE);
        int liabilities = pb.books().get(TitleType.LIABILITIES);
        int equity = pb.books().get(TitleType.EQUITY);
        int assets = pb.books().get(TitleType.ASSETS);

        int benefit = revenue - expense;

        assertThat(expense + benefit, is(revenue));
        assertThat(assets, is(liabilities + equity + benefit));
    }

    @Test
    public void employerTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        WorkerParson founder2 = new WorkerParson();
        int salary = cbank.paySalary(founder);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        int salary2 = cbank.paySalary(founder2);
        int tax2 = Taxes.computeIncomeTaxFromManthly(salary2);
        int capital2 = salary2 - tax2;

        PrivateBusiness liblio = founder.establish(Industry.LIBLIO, capital).get();
        PrivateBusiness superMarket = founder2.establish(Industry.SUPER_MARKET, capital2).get();
        checkAccount(liblio);
        checkAccount(superMarket);

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
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;
        int initial = moneyStock / 2;

        PrivateBusiness farmer = worker.establish(Industry.FARMER, initial).get();
        assertThat(farmer.books().get(PrivateBusinessTitle.CAPITAL_STOCK), is(initial));
        assertThat(farmer.deposit(), is(initial));
        checkAccount(farmer);
    }

    @Test
    public void borrowTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();

        farmer.borrow(capital);
        assertThat(farmer.deposit(), is(capital * 2));
        assertThat(farmer.books().get(PrivateBusinessTitle.LOANS_PAYABLE), is(capital));
        assertThat(farmer.books().get(PrivateBusinessTitle.CAPITAL_STOCK), is(capital));
        checkAccount(farmer);
    }

    @Test
    public void distributionTest() {
        System.out.println("--- distribution ---");
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        WorkerParson worker2 = new WorkerParson();
        WorkerParson worker3 = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        int salary2 = cbank.paySalary(worker2);
        int tax2 = Taxes.computeIncomeTaxFromManthly(salary2);
        int capital2 = salary2 - tax2;
        int salary3 = cbank.paySalary(worker3);
        int tax3 = Taxes.computeIncomeTaxFromManthly(salary3);
        int capital3 = salary3 - tax3;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness maker = worker2.establish(Industry.FOOD_MAKER, capital2).get();
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness coop = worker3.establish(Industry.SUPER_MARKET, capital3).get();
        checkAccount(farmer);
        checkAccount(maker);
        checkAccount(coop);

        cbank.paySalary(worker);

        Product product = Product.RICE_BALL;
        int require = 3;
        OptionalInt optPrice = worker.buy(product, require);
        int price = optPrice.getAsInt();
        assertThat(price, is(not(0)));
        checkAccount(farmer);
        checkAccount(maker);
        checkAccount(coop);

        int countToEndOfMonth = Market.INSTANCE.nowDate().lengthOfMonth() - Market.INSTANCE.nowDate().getDayOfMonth();
        IntStream.range(0, countToEndOfMonth + 10).forEach(n -> Market.INSTANCE.nextDay());
        System.out.printf("farmer: %s%n", farmer.books().toString());
        System.out.printf("maker: %s%n", maker.books().toString());
        System.out.printf("coop: %s%n", coop.books().toString());

        int farmerSales = farmer.books().get(PrivateBusinessTitle.SALES);
        int makerSales = maker.books().get(PrivateBusinessTitle.SALES);
        int coopSales = coop.books().get(PrivateBusinessTitle.SALES);
        assertThat(farmerSales, is(greaterThan(0)));

        assertThat(farmerSales,
                is(maker.books().get(PrivateBusinessTitle.PURCHESES)));
        assertThat(makerSales,
                is(coop.books().get(PrivateBusinessTitle.PURCHESES)));
        assertThat(coopSales, is(price));
        int coopCash = coop.books().get(PrivateBusinessTitle.CASH);
        assertThat(coopCash, is(price));

        checkAccount(farmer);
        checkAccount(maker);
        checkAccount(coop);
        System.out.println("--- distribution ---");
    }

    @Test
    public void paySalaryTest() {
        System.out.println("paySalary");
        CentralBank central = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = central.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        assertThat(central.books().get(CentralBankTitle.DEPOSIT), is(salary - tax));
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(salary - tax));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(salary - tax));

        int capital = salary - tax;
        PrivateBusiness company = worker.establish(Industry.FARMER, capital).get();
        assertThat(company.books().get(PrivateBusinessTitle.CHECKING_ACCOUNTS), is(capital));
        int coSalary = company.paySalary(worker);
        int coTax = Taxes.computeIncomeTaxFromManthly(coSalary);
        assertThat(company.books().get(PrivateBusinessTitle.SALARIES_EXPENSE), is(coSalary));
        assertThat(company.books().get(PrivateBusinessTitle.DEPOSITS_RECEIVED), is(coTax));
        assertThat(company.books().get(PrivateBusinessTitle.CHECKING_ACCOUNTS),
                is(capital - (coSalary - coTax)));

        checkAccount(company);

        System.out.println("--- end paySalary ---");
    }

}
