package jp.gr.java_conf.falius.economy2.player;

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

        assertThat(bank.account().get(PrivateBankAccountTitle.DEPOSIT), is(salary));
        assertThat(bank.account().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(salary));
    }

    @Test
    public void establishBusinessTest() {
        int initialExpenses = 100000;
        PrivateBank bank = new PrivateBank();

        PrivateBusiness farmer =
                new PrivateBusiness(Industry.FARMER, EnumSet.of(Product.RICE), initialExpenses);
        assertThat(bank.account().get(PrivateBankAccountTitle.DEPOSIT), is(initialExpenses));
        assertThat(bank.account().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(initialExpenses));
        PrivateBusiness maker = new PrivateBusiness(Industry.FOOD_MAKER, Industry.FOOD_MAKER.products(),
                initialExpenses);
        assertThat(bank.account().get(PrivateBankAccountTitle.DEPOSIT), is(initialExpenses * 2));
        assertThat(bank.account().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(initialExpenses * 2));
        PrivateBusiness coop =
                new Retail(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products(), initialExpenses);
        assertThat(bank.account().get(PrivateBankAccountTitle.DEPOSIT), is(initialExpenses * 3));
        assertThat(bank.account().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS), is(initialExpenses * 3));

    }

    @Test
    public void distributionTest() {
        PrivateBank bank = new PrivateBank();
        int initialExpenses = 100000;
        PrivateBusiness farmer = new PrivateBusiness(Industry.FARMER, EnumSet.of(Product.RICE), initialExpenses);
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness maker = new PrivateBusiness(Industry.FOOD_MAKER, Industry.FOOD_MAKER.products(),
                initialExpenses);
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness coop = new Retail(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products(), initialExpenses);

        WorkerParson worker = new WorkerParson();
        int salary = 100000;
        worker.getSalary(salary);

        Product product = Product.RICE_BALL;
        int require = 3;
        worker.buy(product, require);

        int countToEndOfMonth = Market.INSTANCE.nowDate().lengthOfMonth()
                - Market.INSTANCE.nowDate().getDayOfMonth();
        IntStream.range(0, countToEndOfMonth).forEach(n -> Market.INSTANCE.nextDay());
        System.out.printf("worker: %s%n", worker.account().toString());
        System.out.printf("farmer: %s%n", farmer.account().toString());
        System.out.printf("maker: %s%n", maker.account().toString());
        System.out.printf("coop: %s%n", coop.account().toString());
        System.out.printf("bank : %s%n", bank.account().toString());

        int loan = 0;
        loan += worker.account().get(WorkerParsonAccountTitle.LOANS_PAYABLE);
        loan += farmer.account().get(PrivateBusinessAccountTitle.LOANS_PAYABLE);
        loan += maker.account().get(PrivateBusinessAccountTitle.LOANS_PAYABLE);
        loan += coop.account().get(PrivateBusinessAccountTitle.LOANS_PAYABLE);
        assertThat(bank.account().get(PrivateBankAccountTitle.LOANS_RECEIVABLE), is(loan));

        int deposit = 0;
        deposit += worker.account().get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT);
        deposit += farmer.account().get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
        deposit += maker.account().get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
        deposit += coop.account().get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
        assertThat(bank.account().get(PrivateBankAccountTitle.DEPOSIT), is(deposit));

        assertThat(bank.account().get(PrivateBankAccountTitle.CASH)
                + bank.account().get(PrivateBankAccountTitle.CHECKING_ACCOUNTS)
                + bank.account().get(PrivateBankAccountTitle.LOANS_RECEIVABLE),
                is(deposit));

    }
}
