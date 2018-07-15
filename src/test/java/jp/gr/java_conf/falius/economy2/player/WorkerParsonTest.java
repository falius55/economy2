package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
import jp.gr.java_conf.falius.economy2.market.MarketInfomation;

public class WorkerParsonTest {

    @BeforeClass
    public static void first() {
        Bank bank = new PrivateBank();
    }

    @AfterClass
    public static void end() {
        PrivateBank.clear();
    }

    @After
    public void clearBusiness() {
        PrivateBusiness.clear();
    }

    @Test
    public void jobTest() {
        PrivateBusiness liblio = new PrivateBusiness(Industry.LIBLIO, Industry.LIBLIO.products(), 10000);
        PrivateBusiness superMarket = new PrivateBusiness(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products(), 10000);

        Worker worker = new WorkerParson();
        assertThat(worker.hasJob(), is(false));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
        worker.retireJob();
        assertThat(worker.hasJob(), is(false));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
    }

    @Test
    public void jobTest2() {
        PrivateBusiness liblio = new PrivateBusiness(Industry.LIBLIO, Industry.LIBLIO.products(), 10000);

        Worker worker = new WorkerParson();
        assertThat(worker.hasJob(), is(false));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
        worker.retireJob();
        assertThat(worker.hasJob(), is(false));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
        assertThat(worker.seekJob(), is(false));  // 今働いている会社以外が存在しないため、転職失敗
        assertThat(worker.hasJob(), is(true));  // 転職に失敗したので、もとの会社も辞めない
    }

    @Test
    public void buyTest() {
        int initialExpenses = 10000;
        PrivateBusiness farmar = new PrivateBusiness(Industry.FARMER, EnumSet.of(Product.RICE), initialExpenses);
        IntStream.range(0, 380).forEach(n -> MarketInfomation.INSTANCE.nextDay());

        PrivateBusiness maker = new PrivateBusiness(Industry.FOOD_MAKER, Industry.FOOD_MAKER.products(), initialExpenses);
        IntStream.range(0, 5).forEach(n -> MarketInfomation.INSTANCE.nextDay());

        PrivateBusiness coop = new Retail(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products(), initialExpenses);

        WorkerParson worker = new WorkerParson();
        assertThat(worker.cash(), is(0));
        assertThat(worker.deposit(), is(0));

        int salary = 100000;
        worker.getPaied(salary);
        assertThat(worker.cash(), is(0));
        assertThat(worker.deposit(), is(salary));

        Product product = Product.RICE_BALL;
        int require = 3;
        OptionalInt optPrice = worker.buy(product, require);
        int price = optPrice.getAsInt();
        assertThat(price, is(not(0)));

        Optional<WorkerParsonAccountTitle> optTitle = WorkerParsonAccountTitle.titleFrom(product);
        WorkerParsonAccountTitle title = optTitle.get();
        int expense = worker.account().get(title);

        System.out.println(worker.account().toString());
        assertThat(expense, is(price));
        assertThat(worker.cash() + worker.deposit(), is(salary - expense));
        assertThat(worker.deposit(), is(lessThan(salary)));

    }

}
