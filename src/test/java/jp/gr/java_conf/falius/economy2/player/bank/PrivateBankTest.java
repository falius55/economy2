package jp.gr.java_conf.falius.economy2.player.bank;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

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

public class PrivateBankTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    @Test
    public void workerSalaryTest() {
        PrivateBank bank = new PrivateBank();
        int salary = 100000;
        int tax = Taxes.computeIncomeTaxFromManthly(salary);

        WorkerParson worker = new WorkerParson();
        worker.getSalary(salary);

        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(salary - tax));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary - tax));
    }

    @Test
    public void establishBusinessTest() {
        System.out.println("--- establish business ---");
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;

        System.out.println(bank.accountBook().toString());

        PrivateBusiness pb = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(capital));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(capital));

        System.out.println("--- establish business ---");
    }

    @Test
    public void borrowToWorkerTest() {
        System.out.println("borrow");
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();

        System.out.println(bank.accountBook().toString());
        int amount = capital;
        worker.borrow(amount);
        System.out.println(bank.accountBook().toString());
        assertThat(bank.deposit(), is(capital - amount + amount));  // 自分に振り込むので変化なし
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.LOANS_RECEIVABLE), is(amount));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(capital + amount));

        System.out.println(cbank.accountBook().toString());

        System.out.println("borrow");
    }

    @Test
    public void borrowToBusinessTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();

        int amount = capital;
        farmer.borrow(amount);
        assertThat(bank.deposit(), is(capital - amount + amount));  // 自分に振り込むので変化なし
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.LOANS_RECEIVABLE), is(amount));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(capital + amount));
    }

    @Test
    public void distributionTest() {
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

        int countToEndOfMonth = Market.INSTANCE.nowDate().lengthOfMonth()
                - Market.INSTANCE.nowDate().getDayOfMonth();
        IntStream.range(0, countToEndOfMonth).forEach(n -> Market.INSTANCE.nextDay());
        System.out.printf("worker: %s%n", worker.accountBook().toString());
        System.out.printf("farmer: %s%n", farmer.accountBook().toString());
        System.out.printf("maker: %s%n", maker.accountBook().toString());
        System.out.printf("coop: %s%n", coop.accountBook().toString());
        System.out.printf("bank : %s%n", bank.accountBook().toString());

        int loan = 0;
        loan += worker.accountBook().get(WorkerParsonAccountTitle.LOANS_PAYABLE);
        loan += farmer.accountBook().get(PrivateBusinessAccountTitle.LOANS_PAYABLE);
        loan += maker.accountBook().get(PrivateBusinessAccountTitle.LOANS_PAYABLE);
        loan += coop.accountBook().get(PrivateBusinessAccountTitle.LOANS_PAYABLE);
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.LOANS_RECEIVABLE), is(loan));

        int deposit = 0;
        deposit += worker.accountBook().get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT);
        deposit += farmer.accountBook().get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
        deposit += maker.accountBook().get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
        deposit += coop.accountBook().get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(deposit));

        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CASH)
                + bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS)
                + bank.accountBook().get(PrivateBankAccountTitle.LOANS_RECEIVABLE),
                is(deposit));

    }

    @Test
    public void nationBondTest() {
        System.out.println("nationBond");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;

        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.printf("worker: %s%n", worker.accountBook().toString());
        System.out.printf("bank: %s%n", bank.accountBook().toString());
        System.out.printf("cbank: %s%n", cbank.accountBook().toString());
        System.out.printf("nation: %s%n", nation.accountBook().toString());
        assertThat(bank.deposit(), is(moneyStock - price * count));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.GOVERNMENT_BOND), is(price * count));

        System.out.println("nationBond");
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
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int amount = Math.min(salary - tax, price * count);
        cbank.operateSelling(amount);
        System.out.println(bank.accountBook().toString());

        assertThat(bank.accountBook().get(PrivateBankAccountTitle.GOVERNMENT_BOND), is(amount));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary - tax - amount));
        System.out.println("operate selling");
    }

    @Test
    public void operateBuyingTest() {
        System.out.println("operate buying");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;
        System.out.println(bank.accountBook().toString());

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);
        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.println(bank.accountBook().toString());

        cbank.operateBuying(price * count);
        System.out.println(bank.accountBook().toString());
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(moneyStock));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.GOVERNMENT_BOND), is(0));


        System.out.println("operate buying");
    }

    @Test
    public void paySalaryTest() {
        System.out.println("paySalary");
        CentralBank central = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = central.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary - tax));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(salary - tax));

        int beforeChecking = salary - tax;
        int beforeDeposit = salary - tax;
        int bankSalary = bank.paySalary(worker);
        int bankTax = Taxes.computeIncomeTaxFromManthly(bankSalary);
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.SALARIES_EXPENSE), is(bankSalary));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSITS_RECEIVED), is(bankTax));
        // 自分に振り込まれるので変わらず ↓
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(beforeChecking));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(beforeDeposit + bankSalary - bankTax));

        System.out.println("--- end paySalary ---");
    }
}
