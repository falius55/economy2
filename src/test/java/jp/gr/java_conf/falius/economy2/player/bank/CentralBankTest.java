package jp.gr.java_conf.falius.economy2.player.bank;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankTitle;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

public class CentralBankTest {

    @Before
    public void before() {
    }

    @After
    public void after() {
        Market.INSTANCE.clear();
    }

    private void check(CentralBank bank) {
        assertThat(bank.nationAccount().amount(), is(bank.books().get(CentralBankTitle.GOVERNMENT_DEPOSIT)));
        assertThat(bank.realDeposits(), is(bank.books().get(CentralBankTitle.DEPOSIT)));
    }

    @Test
    public void keepPaidOutTest() {
        System.out.println("--- keep paid out ---");
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        check(cbank);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, capital).get();
        check(cbank);
        System.out.printf("bank: %s%n", bank.books().toString());
        System.out.printf("cbank: %s%n", cbank.books().toString());

        int amount = 2000;
        bank.downMoney(amount);
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(capital - amount));
        assertThat(cbank.books().get(CentralBankTitle.BANK_NOTE), is(amount));
        System.out.printf("bank2: %s%n", bank.books().toString());
        System.out.printf("cbank2: %s%n", cbank.books().toString());
        check(cbank);

        bank.saveMoney(amount);
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(capital));
        assertThat(cbank.books().get(CentralBankTitle.BANK_NOTE), is(0));
        System.out.printf("bank3: %s%n", bank.books().toString());
        System.out.printf("cbank3: %s%n", cbank.books().toString());
        check(cbank);

        System.out.println("--- keep paid out ---");
    }

    @Test
    public void establishPrivateBusinessTest() {
        System.out.println("--- establish private business ---");
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;

        int deposit = cbank.books().get(CentralBankTitle.DEPOSIT);
        PrivateBusiness farmer = worker.establish(Industry.FARMER, capital).get();
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(capital));
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(deposit)); // 中央銀行には影響せず
        check(cbank);

        System.out.println("--- establish private business ---");
    }

    @Test
    public void paySalaryTest() {
        System.out.println("paySalary");
        CentralBank central = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = central.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        System.out.printf("central: %s%n", central.books().toString());
        System.out.printf("bank: %s%n", bank.books().toString());
        System.out.printf("worker: %s%n", worker.books().toString());

        assertThat(central.books().get(CentralBankTitle.SALARIES_EXPENSE), is(salary));
        assertThat(central.books().get(CentralBankTitle.DEPOSIT), is(salary - tax));
        assertThat(central.books().get(CentralBankTitle.DEPOSITS_RECEIVED), is(tax));
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(salary - tax));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(salary - tax));
        assertThat(worker.books().get(WorkerParsonTitle.SALARIES), is(salary));
        assertThat(worker.books().get(WorkerParsonTitle.ORDINARY_DEPOSIT), is(salary - tax));
        assertThat(worker.books().get(WorkerParsonTitle.TAX), is(tax));
        check(central);
        System.out.println("--- end paySalary ---");
    }

    @Test
    public void underwriteBondsTest() {
        System.out.println("--- underwrite bonds ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeUnderwriteBonds(cbank);
        System.out.println(cbank.books().toString());
        check(cbank);

        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_BOND), is(price * count));
        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_DEPOSIT), is(price * count));

        System.out.println("--- underwrite bonds ---");
    }

    @Test
    public void operateSellingTest() {
        System.out.println("--- operate selling ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeUnderwriteBonds(cbank);
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int amount = Math.min(salary - tax, price * count);
        cbank.operateSelling(amount);
        System.out.println(cbank.books().toString());

        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_BOND), is(price * count - amount));
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(salary - tax - amount));
        check(cbank);
        System.out.println("--- operate selling ---");
    }

    @Test
    public void operateBuyingTest() {
        System.out.println("--- operate buying ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;
        System.out.println(cbank.books().toString());

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);
        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.println(cbank.books().toString());

        cbank.operateBuying(price * count);
        System.out.println(cbank.books().toString());
        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_BOND), is(price * count));
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(moneyStock));
        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_DEPOSIT), is(price * count));
        check(cbank);

        System.out.println("--- operate buying ---");
    }

    @Test
    public void advertiseBondsTest() {
        System.out.println("--- advertise ---");
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
        System.out.println(cbank.books().toString());
        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_DEPOSIT), is(count * price));
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(moneyStock - count * price));
        check(cbank);

        System.out.println("--- advertise ---");
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

        Product product = Product.RICE_BALL;
        int require = 3;
        worker.buy(product, require);

        int allIncomeTax = 0;
        allIncomeTax += cbank.books().get(CentralBankTitle.DEPOSITS_RECEIVED);
        allIncomeTax += bank.books().get(PrivateBankTitle.DEPOSITS_RECEIVED);
        allIncomeTax += farmer.books().get(PrivateBusinessTitle.DEPOSITS_RECEIVED);
        allIncomeTax += maker.books().get(PrivateBusinessTitle.DEPOSITS_RECEIVED);
        allIncomeTax += coop.books().get(PrivateBusinessTitle.DEPOSITS_RECEIVED);
        int allConsumptionTax = 0;
        allConsumptionTax += farmer.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);
        allConsumptionTax += maker.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);
        allConsumptionTax += coop.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);

        System.out.println(cbank.books().toString());
        nation.collectTaxes();
        System.out.println(cbank.books().toString());

        assertThat(cbank.books().get(CentralBankTitle.DEPOSITS_RECEIVED), is(0));
        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_DEPOSIT), is(allIncomeTax + allConsumptionTax));
        check(cbank);

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
        System.out.printf("cbank: %s%n", cbank.books().toString());
        System.out.printf("bank: %s%n", bank.books().toString());

        int price = nation.order(Product.BUILDINGS).getAsInt();

        System.out.println("支出負担分、国債を発行する。");
        nation.closeEndOfMonth();
        System.out.printf("cbank: %s%n", cbank.books().toString());
        System.out.printf("bank: %s%n", bank.books().toString());
        int issued = cbank.books().get(CentralBankTitle.GOVERNMENT_BOND)
                + bank.books().get(PrivateBankTitle.GOVERNMENT_BOND);
        assertThat(issued, is(nation.deposit()));

        System.out.println("分割払いで支払い");
        IntStream.range(0, 6).forEach(n -> Market.INSTANCE.nextEndOfMonth());
        System.out.printf("cbank: %s%n", cbank.books().toString());
        System.out.printf("bank: %s%n", bank.books().toString());
        System.out.printf("business: %s%n", business.books().toString());
        int paid = price - nation.expenditureBurden();
        int pbBonds = bank.books().get(PrivateBankTitle.GOVERNMENT_BOND);
        int expectCentralAccount = capital - pbBonds + paid;
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(expectCentralAccount));
        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_DEPOSIT), is(issued - paid));
        check(cbank);

        System.out.println("払いきると建物を引き替え");
        while (true) {
            Market.INSTANCE.nextEndOfMonth();
            if (nation.expenditureBurden() <= 0) {
                break;
            }
        }
        System.out.printf("cbank: %s%n", cbank.books().toString());
        System.out.printf("bank: %s%n", bank.books().toString());
        System.out.printf("business: %s%n", business.books().toString());
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT), is(capital - pbBonds + price));
        assertThat(cbank.books().get(CentralBankTitle.DEPOSIT),
                is(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS)));
        assertThat(cbank.books().get(CentralBankTitle.GOVERNMENT_DEPOSIT), is(issued - price));
        check(cbank);

        System.out.println("--- order ---");
    }
}
