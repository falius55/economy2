package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
import jp.gr.java_conf.falius.economy2.market.Market;

public class WorkerParsonTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    @Test
    public void jobTest() {
        Bank bank = new PrivateBank();
        PrivateBusiness liblio = new PrivateBusiness(new WorkerParson(), Industry.LIBLIO, 10000);
        PrivateBusiness superMarket = new PrivateBusiness(new WorkerParson(), Industry.SUPER_MARKET, 10000);

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
    public void buyTest() {
        Bank bank = new PrivateBank();
        int initialExpenses = 10000;
        PrivateBusiness farmar = new PrivateBusiness(new WorkerParson(), Industry.FARMER, EnumSet.of(Product.RICE), initialExpenses);
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());

        PrivateBusiness maker = new PrivateBusiness(new WorkerParson(), Industry.FOOD_MAKER, initialExpenses);
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());

        PrivateBusiness coop = new PrivateBusiness(new WorkerParson(), Industry.SUPER_MARKET, initialExpenses);

        WorkerParson worker = new WorkerParson();
        assertThat(worker.cash(), is(0));
        assertThat(worker.deposit(), is(0));

        int salary = 100000;
        worker.getSalary(salary);
        assertThat(worker.cash(), is(0));
        assertThat(worker.deposit(), is(salary));

        Product product = Product.RICE_BALL;
        int require = 3;
        OptionalInt optPrice = worker.buy(product, require);
        int price = optPrice.getAsInt();
        assertThat(price, is(not(0)));

        Optional<WorkerParsonAccountTitle> optTitle = WorkerParsonAccountTitle.titleFrom(product);
        WorkerParsonAccountTitle title = optTitle.get();
        int expense = worker.accountBook().get(title);

        System.out.println(worker.accountBook().toString());
        assertThat(expense, is(price));
        assertThat(worker.cash() + worker.deposit(), is(salary - expense));
        assertThat(worker.deposit(), is(lessThan(salary)));
    }

    @Test
    public void establishTest() {
        CentralBank centralBank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = centralBank.paySalary(worker);
        int initial = salary / 2;

        Optional<PrivateBusiness> opt = worker.establish(Industry.FARMER, initial);
        assertThat(opt.isPresent(), is(true));
        PrivateBusiness farmer = opt.get();

        assertThat(farmer.accountBook().get(PrivateBusinessAccountTitle.CAPITAL_STOCK), is(initial));
    }

}
