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
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonTitle;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;
import jp.gr.java_conf.falius.economy2.util.Taxes;

public class WorkerParsonTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    private void check(WorkerParson worker) {
        assertThat(worker.mainBank().account(worker).amount(),
                is(worker.books().get(WorkerParsonTitle.ORDINARY_DEPOSIT)));
    }

    @Test
    public void jobTest() {
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        WorkerParson founder2 = new WorkerParson();
        int salary = cbank.paySalary(founder);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        int salary2 = cbank.paySalary(founder2);
        int tax2 = Taxes.computeIncomeTaxFromManthly(salary2);
        int capital2 = salary2 - tax2;

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
        int salary1 = cbank.paySalary(founder);
        int tax = Taxes.computeIncomeTaxFromManthly(salary1);
        int capital = salary1 - tax;
        int salary2 = cbank.paySalary(founder2);
        int tax2 = Taxes.computeIncomeTaxFromManthly(salary2);
        int capital2 = salary2 - tax2;
        int salary3 = cbank.paySalary(founder3);
        int tax3 = Taxes.computeIncomeTaxFromManthly(salary3);
        int capital3 = salary3 - tax3;
        check(founder);
        check(founder2);
        check(founder3);

        PrivateBusiness farmar = founder.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());

        PrivateBusiness maker = founder2.establish(Industry.FOOD_MAKER, capital2).get();
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());

        PrivateBusiness coop = founder3.establish(Industry.SUPER_MARKET, capital3).get();
        check(founder);
        check(founder2);
        check(founder3);

        WorkerParson worker = new WorkerParson();
        assertThat(worker.cash(), is(0));
        assertThat(worker.deposit(), is(0));

        int salary = cbank.paySalary(worker);
        assertThat(worker.cash(), is(0));
        assertThat(worker.deposit(), is(salary - tax));
        check(worker);

        Product product = Product.RICE_BALL;
        int require = 3;
        OptionalInt optPrice = worker.buy(product, require);
        int price = optPrice.getAsInt();
        assertThat(price, is(not(0)));
        check(worker);

        Optional<WorkerParsonTitle> optTitle = WorkerParsonTitle.titleFrom(product);
        WorkerParsonTitle title = optTitle.get();
        int expense = worker.books().get(title);

        System.out.println(worker.books().toString());
        assertThat(expense, is(price));
        assertThat(worker.cash() + worker.deposit(), is(salary - tax - expense));
        assertThat(worker.deposit(), is(lessThan(salary - tax)));
    }

    @Test
    public void establishTest() {
        CentralBank centralBank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = centralBank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;
        int initial = moneyStock / 2;
        assertThat(worker.deposit(), is(salary - tax));

        PrivateBusiness farmer = worker.establish(Industry.FARMER, initial).get();
        assertThat(farmer.books().get(PrivateBusinessTitle.CAPITAL_STOCK), is(initial));

        assertThat(worker.deposit(), is(salary - tax - initial));
        assertThat(worker.books().get(WorkerParsonTitle.ESTABLISH_EXPENSES), is(initial));
        check(worker);

    }

    @Test
    public void borrowTest() {
        System.out.println("--- borrow ---");
        CentralBank cbank = CentralBank.INSTANCE;
        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        assertThat(worker.cash() + worker.deposit(), is(0));
        check(worker);

        worker.borrow(capital);
        System.out.println(worker.books().toString());
        assertThat(worker.deposit(), is(capital));
        assertThat(worker.books().get(WorkerParsonTitle.LOANS_PAYABLE), is(capital));
        check(worker);

        System.out.println("--- borrow ---");
    }

}
