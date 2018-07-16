package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;
import jp.gr.java_conf.falius.economy2.market.Market;

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
        int capital = 10000;
        PrivateBank  bank = new PrivateBank();
        PrivateBusiness farmer = new PrivateBusiness(Industry.FARMER, capital);
        System.out.printf("bank: %s%n", bank.account().toString());
        System.out.printf("cbank: %s%n", cbank.account().toString());

        int amount = 2000;
        bank.saveMoney(amount);
        assertThat(cbank.account().get(CentralBankAccountTitle.DEPOSIT), is(amount));
        System.out.printf("bank2: %s%n", bank.account().toString());
        System.out.printf("cbank2: %s%n", cbank.account().toString());

        bank.downMoney(amount);
        assertThat(cbank.account().get(CentralBankAccountTitle.DEPOSIT), is(0));
        System.out.printf("bank3: %s%n", bank.account().toString());
        System.out.printf("cbank3: %s%n", cbank.account().toString());
    }

    @Test
    public void paySalaryTest() {
        System.out.println("paySalary");
        CentralBank central = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = central.paySalary(worker);
        System.out.printf("central: %s%n", central.account().toString());
        System.out.printf("bank: %s%n", bank.account().toString());
        System.out.printf("worker: %s%n", worker.account().toString());
        System.out.println("--- end paySalary ---");

        assertThat(central.account().get(CentralBankAccountTitle.SALARIES_EXPENSE), is(salary));
        assertThat(central.account().get(CentralBankAccountTitle.DEPOSIT), is(salary));
        assertThat(bank.account().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary));
//        assertThat(bank.account().get(PrivateBankAccountTitle.DEPOSIT), is(salary));
//        assertThat(worker.account().get(WorkerParsonAccountTitle.SALARIES), is(salary));
//        assertThat(worker.account().get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT), is(salary));

    }
}
