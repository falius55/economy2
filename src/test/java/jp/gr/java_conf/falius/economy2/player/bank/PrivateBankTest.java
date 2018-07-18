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

        WorkerParson worker = new WorkerParson();
        worker.getSalary(salary);

        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(salary));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary));
    }

    @Test
    public void establishBusinessTest() {
        System.out.println("--- establish business ---");
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int capital = cbank.paySalary(worker);

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
        int capital = cbank.paySalary(worker);
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
        int capital = cbank.paySalary(worker);
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
        int capital = cbank.paySalary(worker);
        int capital2 = cbank.paySalary(worker2);
        int capital3 = cbank.paySalary(worker3);
        int initialExpenses = 100000;
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
        int moneyStock = cbank.paySalary(worker);

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.println(bank.accountBook().toString());
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
        int amount = Math.min(salary, price * count);
        cbank.operateSelling(amount);
        System.out.println(bank.accountBook().toString());

        assertThat(bank.accountBook().get(PrivateBankAccountTitle.GOVERNMENT_BOND), is(amount));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary - amount));
        System.out.println("operate selling");
    }

    @Test
    public void operateBuyingTest() {
        System.out.println("operate buying");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int moneyStock = cbank.paySalary(worker);
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
}
