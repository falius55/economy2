package jp.gr.java_conf.falius.economy2.market.aggre;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class MarketAggregaterTest {

    @After
    public void after() {
        Market.INSTANCE.clear();
    }

    private void checkGDP() {
        MarketAggregater aggregater = Market.INSTANCE.aggregater();
        System.out.printf("GDP: %d%n", aggregater.GDP());
        System.out.printf("sGDP: %d%n", aggregater.sGDP());
        System.out.printf("GDE: %d%n", aggregater.GDE());
        assertThat(aggregater.GDP(), is(aggregater.sGDP()));
        assertThat(aggregater.sGDP(), is(aggregater.GDE()));
        assertThat(aggregater.GDP(), is(aggregater.GDE()));
    }

    @Test
    public void GDPTest() {
        System.out.println("--- GDP test ---");
        CentralBank centralBank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = centralBank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;
        int initial = moneyStock / 2;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, initial).get();
        checkGDP();
        System.out.println("--- GDP test ---");
    }

    @Test
    public void paySalalyGDPTest() {
        System.out.println("--- pay salary ---");
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        WorkerParson worker2 = new WorkerParson();
        WorkerParson worker3 = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int salary2 = cbank.paySalary(worker2);
        int salary3 = cbank.paySalary(worker3);
        checkGDP();
        System.out.println("--- pay salary ---");
    }

    @Test
    public void establishGDPTest() {
        System.out.println("--- establish ---");
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

        checkGDP();
        System.out.println("--- establish ---");
    }

    @Test
    public void buyGDPTest() {
        System.out.println("--- buy ---");
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

        cbank.paySalary(worker);

        Product product = Product.RICE_BALL;
        int require = 3;
        OptionalInt optPrice = worker.buy(product, require);
        int price = optPrice.getAsInt();
        checkGDP();
        System.out.println("--- buy ---");
    }

    @Test
    public void endOfMonthGDPTest() {
        System.out.println("--- end of month ---");
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

        cbank.paySalary(worker);

        Product product = Product.RICE_BALL;
        int require = 3;
        OptionalInt optPrice = worker.buy(product, require);
        int price = optPrice.getAsInt();

        int countToEndOfMonth = Market.INSTANCE.nowDate().lengthOfMonth() - Market.INSTANCE.nowDate().getDayOfMonth();
        IntStream.range(0, countToEndOfMonth + 10).forEach(n -> Market.INSTANCE.nextDay());

        checkGDP();
        System.out.println("--- end of month ---");
    }

    @Test
    public void businessPaySalaryGDPTest() {
        System.out.println("--- business paySalary GDP ---");
        CentralBank central = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = central.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        assertThat(Market.INSTANCE.aggregater().M(), is(salary - tax));

        int capital = salary - tax;
        PrivateBusiness company = worker.establish(Industry.FARMER, capital).get();
        int coSalary = company.paySalary(worker);
        System.out.println(worker.accountBook().toString());
        int coTax = Taxes.computeIncomeTaxFromManthly(coSalary);
        checkGDP();
        assertThat(Market.INSTANCE.aggregater().GDP(), is(salary));
        assertThat(Market.INSTANCE.aggregater().M(), is(salary - tax));
        System.out.println("--- business paySalary GDP ---");
    }

    @Test
    public void workerborrowMoneystockTest() {
        System.out.println("--- worker borrow money stock ---");
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        assertThat(Market.INSTANCE.aggregater().M(), is(capital));

        worker.borrow(capital);
        System.out.println(bank.accountBook().toString());
        System.out.printf("M: %d%n", Market.INSTANCE.aggregater().M());
        assertThat(Market.INSTANCE.aggregater().M(), is(capital + capital));

        System.out.println("--- worker borrow money stock ---");
    }

    @Test
    public void businessborrowMoneystockTest() {
        System.out.println("--- business borrow money stock ---");
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        assertThat(Market.INSTANCE.aggregater().M(), is(capital));

        farmer.borrow(capital);
        assertThat(Market.INSTANCE.aggregater().M(), is(capital + capital));
        System.out.println("--- business borrow money stock ---");
    }
}
