package jp.gr.java_conf.falius.economy2.player.bank;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
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

    @Test
    public void keepPaidOutTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank  bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, capital).get();
        System.out.printf("bank: %s%n", bank.accountBook().toString());
        System.out.printf("cbank: %s%n", cbank.accountBook().toString());

        int amount = 2000;
        bank.downMoney(amount);
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(capital - amount));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.BANK_NOTE), is(amount));
        System.out.printf("bank2: %s%n", bank.accountBook().toString());
        System.out.printf("cbank2: %s%n", cbank.accountBook().toString());

        bank.saveMoney(amount);
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(capital));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.BANK_NOTE), is(0));
        System.out.printf("bank3: %s%n", bank.accountBook().toString());
        System.out.printf("cbank3: %s%n", cbank.accountBook().toString());
    }

    @Test
    public void establishPrivateBusinessTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank  bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;

        int deposit = cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT);
        PrivateBusiness farmer = worker.establish(Industry.FARMER, capital).get();
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(capital));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(deposit));  // 中央銀行には影響せず
    }

    @Test
    public void paySalaryTest() {
        System.out.println("paySalary");
        CentralBank central = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = central.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        System.out.printf("central: %s%n", central.accountBook().toString());
        System.out.printf("bank: %s%n", bank.accountBook().toString());
        System.out.printf("worker: %s%n", worker.accountBook().toString());

        assertThat(central.accountBook().get(CentralBankAccountTitle.SALARIES_EXPENSE), is(salary));
        assertThat(central.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(salary - tax));
        assertThat(central.accountBook().get(CentralBankAccountTitle.DEPOSITS_RECEIVED), is(tax));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary - tax));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(salary - tax));
        assertThat(worker.accountBook().get(WorkerParsonAccountTitle.SALARIES), is(salary));
        assertThat(worker.accountBook().get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT), is(salary - tax));
        assertThat(worker.accountBook().get(WorkerParsonAccountTitle.TAX), is(tax));
        System.out.println("--- end paySalary ---");
    }

    @Test
    public void underwriteBondsTest() {
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeUnderwriteBonds(cbank);
        System.out.println(cbank.accountBook().toString());

        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_BOND), is(price * count));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_DEPOSIT), is(price * count));
    }

    @Test
    public void operateSellingTest() {
        System.out.println("operate selling");
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
        System.out.println(cbank.accountBook().toString());

        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_BOND), is(price * count - amount));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(salary - tax - amount));
        System.out.println("operate selling");
    }

    @Test
    public void operateBuyingTest() {
        System.out.println("operate buying");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;
        System.out.println(cbank.accountBook().toString());

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);
        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.println(cbank.accountBook().toString());

        cbank.operateBuying(price * count);
        System.out.println(cbank.accountBook().toString());
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_BOND), is(price * count));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(moneyStock));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_DEPOSIT), is(price * count));

        System.out.println("operate buying");
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
        System.out.println(cbank.accountBook().toString());
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_DEPOSIT), is(count * price));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(moneyStock - count * price));

        System.out.println("advertise");
    }

    @Test
    public void collectTaxesTest() {
        System.out.println("collect taxes");
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
        allIncomeTax += cbank.accountBook().get(CentralBankAccountTitle.DEPOSITS_RECEIVED);
        allIncomeTax += bank.accountBook().get(PrivateBankAccountTitle.DEPOSITS_RECEIVED);
        allIncomeTax += farmer.accountBook().get(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED);
        allIncomeTax += maker.accountBook().get(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED);
        allIncomeTax += coop.accountBook().get(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED);
        int allConsumptionTax = 0;
         allConsumptionTax+= farmer.accountBook().get(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax+= maker.accountBook().get(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax+= coop.accountBook().get(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX);

        System.out.println(cbank.accountBook().toString());
        nation.collectTaxes();
        System.out.println(cbank.accountBook().toString());

        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSITS_RECEIVED), is(0));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_DEPOSIT), is(allIncomeTax + allConsumptionTax));

        System.out.println("collect taxes");
    }
}
