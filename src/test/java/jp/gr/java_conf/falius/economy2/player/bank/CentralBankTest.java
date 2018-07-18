package jp.gr.java_conf.falius.economy2.player.bank;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
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
        int capital = cbank.paySalary(worker);
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
        int capital = cbank.paySalary(worker);

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
        System.out.printf("central: %s%n", central.accountBook().toString());
        System.out.printf("bank: %s%n", bank.accountBook().toString());
        System.out.printf("worker: %s%n", worker.accountBook().toString());
        System.out.println("--- end paySalary ---");

        assertThat(central.accountBook().get(CentralBankAccountTitle.SALARIES_EXPENSE), is(salary));
        assertThat(central.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(salary));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(salary));
        assertThat(worker.accountBook().get(WorkerParsonAccountTitle.SALARIES), is(salary));
        assertThat(worker.accountBook().get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT), is(salary));
    }

    @Test
    public void underwriteBondsTest() {
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeCentralBankUnderwriteBond();
        System.out.println(cbank.accountBook().toString());

        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_BOND), is(price * count));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_DEPOSIT), is(price * count));
    }

    @Test
    public void advertiseBondsTest() {
        System.out.println("advertise");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;

        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int moneyStock = cbank.paySalary(worker);

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.println(cbank.accountBook().toString());
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_DEPOSIT), is(count * price));
        assertThat(cbank.accountBook().get(CentralBankAccountTitle.DEPOSIT), is(moneyStock - count * price));

        System.out.println("advertise");
    }
}
