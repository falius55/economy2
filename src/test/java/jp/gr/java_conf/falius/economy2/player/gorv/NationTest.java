package jp.gr.java_conf.falius.economy2.player.gorv;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
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

    @Test
    public void centralBankBondsTest() {
        Nation nation = Nation.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeUnderwriteBonds(CentralBank.INSTANCE);
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
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.println(nation.accountBook().toString());
        assertThat(nation.accountBook().get(GovernmentAccountTitle.GOVERNMENT_BOND), is(count * price));
        assertThat(nation.accountBook().get(GovernmentAccountTitle.DEPOSIT), is(count * price));

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
        allIncomeTax += cbank.accountBook().get(CentralBankAccountTitle.DEPOSITS_RECEIVED);
        allIncomeTax += bank.accountBook().get(PrivateBankAccountTitle.DEPOSITS_RECEIVED);
        allIncomeTax += farmer.accountBook().get(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED);
        allIncomeTax += maker.accountBook().get(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED);
        allIncomeTax += coop.accountBook().get(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED);
        int allConsumptionTax = 0;
         allConsumptionTax+= farmer.accountBook().get(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax+= maker.accountBook().get(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax+= coop.accountBook().get(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX);

        System.out.println(nation.accountBook().toString());
        nation.collectTaxes();
        System.out.println(nation.accountBook().toString());

        assertThat(nation.accountBook().get(GovernmentAccountTitle.INCOME_TAX), is(greaterThan(0)));
        assertThat(nation.accountBook().get(GovernmentAccountTitle.INCOME_TAX), is(allIncomeTax));
        assertThat(nation.accountBook().get(GovernmentAccountTitle.CONSUMPTION_TAX), is(greaterThan(0)));
        assertThat(nation.accountBook().get(GovernmentAccountTitle.CONSUMPTION_TAX), is(allConsumptionTax));
        assertThat(nation.accountBook().get(GovernmentAccountTitle.INCOME_TAX)
                + nation.accountBook().get(GovernmentAccountTitle.CONSUMPTION_TAX),
                is(nation.accountBook().get(GovernmentAccountTitle.DEPOSIT) ));

        System.out.println("collect taxes");
    }
}
