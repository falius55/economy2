package jp.gr.java_conf.falius.economy2.market.aggre;

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
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;
import jp.gr.java_conf.falius.economy2.util.Taxes;

public class MarketAggregaterTest {

    @After
    public void after() {
        Market.INSTANCE.clear();
    }

    private void check() {
        // account check
        int realDepositsOfCentralBank = CentralBank.INSTANCE.realDeposits();
        int depositsOfCentralBankOfBooks = CentralBank.INSTANCE.books().get(CentralBankTitle.DEPOSIT);
        assertThat(realDepositsOfCentralBank, is(depositsOfCentralBankOfBooks));
        int checkingAccountsOfPrivateBanksOfBooks = Market.INSTANCE.entities(PrivateBank.class)
                .mapToInt(pb -> pb.books().get(PrivateBankTitle.CHECKING_ACCOUNTS)).sum();
        assertThat(checkingAccountsOfPrivateBanksOfBooks, is(realDepositsOfCentralBank));
        int realDepositsOfPrivateBank = Market.INSTANCE.entities(PrivateBank.class).mapToInt(PrivateBank::realDeposits)
                .sum();
        int depositsOfPrivateBankOfBooks = Market.INSTANCE.entities(PrivateBank.class).map(PrivateBank::books)
                .mapToInt(book -> book.get(PrivateBankTitle.DEPOSIT)).sum();
        assertThat(realDepositsOfPrivateBank, is(depositsOfPrivateBankOfBooks));
        int workerDeposits = Market.INSTANCE.entities(WorkerParson.class).mapToInt(WorkerParson::deposit).sum();
        int businessDeposits = Market.INSTANCE.entities(PrivateBusiness.class).mapToInt(PrivateBusiness::deposit).sum();
        assertThat(workerDeposits + businessDeposits, is(realDepositsOfPrivateBank));
        Market.INSTANCE.entities(WorkerParson.class)
                .forEach(worker -> assertThat(worker.mainBank().account(worker).amount(), is(worker.deposit())));
        Market.INSTANCE.entities(PrivateBusiness.class)
                .forEach(pb -> assertThat(pb.mainBank().account(pb).amount(), is(pb.deposit())));
        Market.INSTANCE.entities(PrivateBank.class)
                .forEach(pb -> assertThat(pb.mainBank().account(pb).amount(), is(pb.deposit())));
        int nationDepositOfCentralOfBooks = CentralBank.INSTANCE.books().get(CentralBankTitle.GOVERNMENT_DEPOSIT);
        assertThat(CentralBank.INSTANCE.nationAccount().amount(), is(nationDepositOfCentralOfBooks));
        assertThat(Nation.INSTANCE.deposit(), is(nationDepositOfCentralOfBooks));

        // GDP check
        MarketAggregater aggregater = Market.INSTANCE.aggregater();
        System.out.printf("GDP: %d%n", aggregater.GDP());
        System.out.printf("sGDP: %d%n", aggregater.sGDP());
        System.out.printf("GDE: %d%n", aggregater.GDE());
        assertThat(aggregater.GDP(), is(aggregater.sGDP()));
        assertThat(aggregater.sGDP(), is(aggregater.GDE()));
        assertThat(aggregater.GDP(), is(aggregater.GDE()));

        System.out.printf("M: %d%n", aggregater.M());
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
        check();
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
        check();
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
        System.out.printf("central account: %d%n", CentralBank.INSTANCE.realDeposits());
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

        check();
        System.out.println("--- establish ---");
    }

    @Test
    public void paySalaryFromBusiness() {
        System.out.println("--- pay salary from business ---");
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        WorkerParson worker2 = new WorkerParson();
        WorkerParson worker3 = new WorkerParson();
        System.out.printf("central account: %d%n", CentralBank.INSTANCE.realDeposits());
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

        farmer.paySalary(worker);
        maker.paySalary(worker2);
        coop.paySalary(worker3);

        check();
        System.out.println("--- pay salary from business ---");
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

        check();
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

        check();
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
        System.out.println(worker.books().toString());
        int coTax = Taxes.computeIncomeTaxFromManthly(coSalary);
        check();
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
        System.out.println(bank.books().toString());
        System.out.printf("M: %d%n", Market.INSTANCE.aggregater().M());
        assertThat(Market.INSTANCE.aggregater().M(), is(capital + capital));

        check();
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
        check();
        System.out.println("--- business borrow money stock ---");
    }

    @Test
    public void centralBankBondsTest() {
        Nation nation = Nation.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeUnderwriteBonds(CentralBank.INSTANCE);
        check();
    }

    @Test
    public void advertiseBondsTest() {
        System.out.println("advertise");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;

        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();

        check();
        System.out.println("advertise");
    }

    @Test
    public void collectTaxesTest() {
        System.out.println("--- collect taxes ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
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
        farmer.paySalary(worker);
        maker.paySalary(worker2);
        coop.paySalary(worker3);

        Product product = Product.RICE_BALL;
        int require = 3;
        worker.buy(product, require);

        nation.collectTaxes();
        check();
        System.out.println("--- collect taxes ---");
    }

    @Test
    public void nationOrderTest() {
        System.out.println("--- order ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;

        PrivateBank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        IntStream.range(0, 100).map(n -> cbank.paySalary(founder)).sum();
        int capital = founder.deposit();

        PrivateBusiness business = founder.establish(Industry.ARCHITECTURE, capital).get();
        System.out.println("創業時");
        check();

        int price = nation.order(Product.BUILDINGS).getAsInt();
        check();

        System.out.println("支出負担分、国債を発行する。");
        nation.closeEndOfMonth();
        check();

        System.out.println("分割払いで支払い");
        IntStream.range(0, 6).forEach(n -> Market.INSTANCE.nextEndOfMonth());
        check();

        System.out.println("払いきると建物を引き替え");
        while (true) {
            Market.INSTANCE.nextEndOfMonth();
            if (nation.expenditureBurden() <= 0) {
                break;
            }
        }
        check();

        System.out.println("--- order ---");
    }

    @Test
    public void orderAndFixedAssetsTest() {
        System.out.println("--- fixed assets ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        int salary = cbank.paySalary(founder);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness business = founder.establish(Industry.ARCHITECTURE, capital).get();
        check();
        int price = nation.order(Product.BUILDINGS).getAsInt();
        while (true) {
            Market.INSTANCE.nextEndOfMonth();
            if (nation.expenditureBurden() <= 0) {
                break;
            }
        }
        check();
        IntStream.range(0, 3).forEach(n -> Market.INSTANCE.nextEndOfMonth());
        check();

        System.out.println("--- fixed assets ---");
    }
}
