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
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

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
        int initialExpenses = 100000;
        PrivateBank bank = new PrivateBank();

        PrivateBusiness farmer =
                new PrivateBusiness(new WorkerParson(), Industry.FARMER, EnumSet.of(Product.RICE), initialExpenses);
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(initialExpenses));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(initialExpenses));
        PrivateBusiness maker =
                new PrivateBusiness(new WorkerParson(), Industry.FOOD_MAKER, initialExpenses);
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(initialExpenses * 2));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(initialExpenses * 2));
        PrivateBusiness coop =
                new PrivateBusiness(new WorkerParson(), Industry.SUPER_MARKET, initialExpenses);
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.DEPOSIT), is(initialExpenses * 3));
        assertThat(bank.accountBook().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(initialExpenses * 3));

    }

    @Test
    public void distributionTest() {
        PrivateBank bank = new PrivateBank();
        int initialExpenses = 100000;
        PrivateBusiness farmer = new PrivateBusiness(new WorkerParson(), Industry.FARMER, EnumSet.of(Product.RICE), initialExpenses);
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness maker = new PrivateBusiness(new WorkerParson(), Industry.FOOD_MAKER, initialExpenses);
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness coop = new PrivateBusiness(new WorkerParson(), Industry.SUPER_MARKET, initialExpenses);

        WorkerParson worker = new WorkerParson();
        int salary = 100000;
        worker.getSalary(salary);

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
}
