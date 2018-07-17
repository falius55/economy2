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
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class WorkerParsonTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    @Test
    public void jobTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        WorkerParson founder2 = new WorkerParson();
        int capital = cbank.paySalary(founder);
        int capital2 = cbank.paySalary(founder2);

        PrivateBusiness liblio = founder.establish(Industry.LIBLIO, capital).get();
        PrivateBusiness superMarket = founder2.establish(Industry.SUPER_MARKET, capital2).get();

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
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        WorkerParson founder2 = new WorkerParson();
        WorkerParson founder3 = new WorkerParson();
        int capital = cbank.paySalary(founder);
        int capital2 = cbank.paySalary(founder2);
        int capital3 = cbank.paySalary(founder3);

        PrivateBusiness farmar = founder.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());

        PrivateBusiness maker = founder2.establish(Industry.FOOD_MAKER, capital2).get();
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());

        PrivateBusiness coop = founder3.establish(Industry.SUPER_MARKET, capital3).get();

        WorkerParson worker = new WorkerParson();
        assertThat(worker.cash(), is(0));
        assertThat(worker.deposit(), is(0));

        int salary = cbank.paySalary(worker);
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
