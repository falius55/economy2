package jp.gr.java_conf.falius.economy2.player.gorv;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankTitle;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankTitle;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class NationTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    private void checkAccount(Nation nation) {
        assertThat(nation.mainBank().nationAccount().amount(), is(nation.books().get(GovernmentTitle.DEPOSIT)));
    }

    @Test
    public void centralBankBondsTest() {
        Nation nation = Nation.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeUnderwriteBonds(CentralBank.INSTANCE);
        System.out.println(nation.books().toString());
        assertThat(nation.books().get(GovernmentTitle.GOVERNMENT_BOND), is(count * price));
        assertThat(nation.books().get(GovernmentTitle.DEPOSIT), is(count * price));
        checkAccount(nation);
    }

    @Test
    public void advertiseBondsTest() {
        System.out.println("advertise");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;

        Bank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.println(nation.books().toString());
        assertThat(nation.books().get(GovernmentTitle.GOVERNMENT_BOND), is(count * price));
        assertThat(nation.books().get(GovernmentTitle.DEPOSIT), is(count * price));
        checkAccount(nation);

        System.out.println("advertise");
    }

    @Test
    public void collectTaxesTest() {
        System.out.println("collect taxes");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        WorkerParson worker2 = new WorkerParson();
        WorkerParson worker3 = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        int salary2 = cbank.paySalary(worker2);
        int tax2 = Taxes.computeIncomeTaxFromManthly(salary2);
        int capital2 = salary2 - tax2;
        int salary3 = cbank.paySalary(worker3);
        int tax3 = Taxes.computeIncomeTaxFromManthly(salary3);
        int capital3 = salary3 - tax3;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        IntStream.range(0, 380).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness maker = worker2.establish(Industry.FOOD_MAKER, capital2).get();
        IntStream.range(0, 5).forEach(n -> Market.INSTANCE.nextDay());
        PrivateBusiness coop = worker3.establish(Industry.SUPER_MARKET, capital3).get();

        cbank.paySalary(worker);

        Product product = Product.RICE_BALL;
        int require = 3;
        worker.buy(product, require);

        int allIncomeTax = 0;
        allIncomeTax += cbank.books().get(CentralBankTitle.DEPOSITS_RECEIVED);
        allIncomeTax += bank.books().get(PrivateBankTitle.DEPOSITS_RECEIVED);
        allIncomeTax += farmer.books().get(PrivateBusinessTitle.DEPOSITS_RECEIVED);
        allIncomeTax += maker.books().get(PrivateBusinessTitle.DEPOSITS_RECEIVED);
        allIncomeTax += coop.books().get(PrivateBusinessTitle.DEPOSITS_RECEIVED);
        int allConsumptionTax = 0;
         allConsumptionTax+= farmer.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax+= maker.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax+= coop.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);

        System.out.println(nation.books().toString());
        nation.collectTaxes();
        System.out.println(nation.books().toString());

        assertThat(nation.books().get(GovernmentTitle.INCOME_TAX), is(greaterThan(0)));
        assertThat(nation.books().get(GovernmentTitle.INCOME_TAX), is(allIncomeTax));
        assertThat(nation.books().get(GovernmentTitle.CONSUMPTION_TAX), is(greaterThan(0)));
        assertThat(nation.books().get(GovernmentTitle.CONSUMPTION_TAX), is(allConsumptionTax));
        assertThat(nation.books().get(GovernmentTitle.INCOME_TAX)
                + nation.books().get(GovernmentTitle.CONSUMPTION_TAX),
                is(nation.books().get(GovernmentTitle.DEPOSIT) ));
        checkAccount(nation);

        System.out.println("collect taxes");
    }
}
