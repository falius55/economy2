package jp.gr.java_conf.falius.economy2.player.gorv;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class NationTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    @Test
    public void centralBankBondsTest() {
        Nation nation = Nation.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeCentralBankUnderwriteBond();
        System.out.println(nation.accountBook().toString());
        assertThat(nation.accountBook().get(GovernmentAccountTitle.GOVERNMENT_BOND), is(count * price));
        assertThat(nation.accountBook().get(GovernmentAccountTitle.DEPOSIT), is(count * price));
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
        System.out.println(nation.accountBook().toString());
        assertThat(nation.accountBook().get(GovernmentAccountTitle.GOVERNMENT_BOND), is(count * price));
        assertThat(nation.accountBook().get(GovernmentAccountTitle.DEPOSIT), is(count * price));

        System.out.println("advertise");
    }
}
