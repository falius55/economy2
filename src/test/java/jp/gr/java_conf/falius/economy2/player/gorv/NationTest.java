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
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonTitle;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;
import jp.gr.java_conf.falius.economy2.util.Taxes;

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
        System.out.println("--- collect taxes ---");
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
        int autoCollectConsumptionTax = nation.books().get(GovernmentTitle.CONSUMPTION_TAX);
        int autoCollectIncomeTax = nation.books().get(GovernmentTitle.INCOME_TAX);

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
        allIncomeTax += autoCollectIncomeTax;
        int allConsumptionTax = 0;
         allConsumptionTax+= farmer.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax+= maker.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax+= coop.books().get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);
         allConsumptionTax += autoCollectConsumptionTax;

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

        int workerTax = Market.INSTANCE.entities(WorkerParson.class)
                .map(WorkerParson::books)
                .mapToInt(books -> books.get(WorkerParsonTitle.TAX))
                .sum();
        assertThat(nation.books().get(GovernmentTitle.INCOME_TAX), is(workerTax));
        int consumptionTax = Market.INSTANCE.entities(PrivateBusiness.class)
                .map(PrivateBusiness::books)
                .mapToInt(books -> books.get(PrivateBusinessTitle.TAX))
                .sum();
        assertThat(nation.books().get(GovernmentTitle.CONSUMPTION_TAX), is(consumptionTax));

        System.out.println("--- collect taxes ---");
    }

    @Test
    public void orderTest() {
        System.out.println("--- order ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        int salary = cbank.paySalary(founder);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness business = founder.establish(Industry.ARCHITECTURE, capital).get();

        int price = nation.order(Product.BUILDINGS).getAsInt();

        System.out.println("支出負担分、国債を発行する。");
        nation.closeEndOfMonth();
        System.out.printf("nation: %s%n", nation.books().toString());
        int collectedTaxes = nation.books().get(GovernmentTitle.INCOME_TAX) + nation.books().get(GovernmentTitle.CONSUMPTION_TAX);
        int bondsOnGovernmentBooks = nation.books().get(GovernmentTitle.GOVERNMENT_BOND);
        int bondsOnCentral = cbank.books().get(CentralBankTitle.GOVERNMENT_BOND);
        int bondsOnPB = bank.books().get(PrivateBankTitle.GOVERNMENT_BOND);
        assertThat(bondsOnGovernmentBooks, is(greaterThan(0)));
        assertThat(bondsOnGovernmentBooks, is(bondsOnCentral + bondsOnPB));
        assertThat(nation.deposit(), is(bondsOnGovernmentBooks + collectedTaxes));
        assertThat(nation.expenditureBurden(), is(price));
        checkAccount(nation);

        System.out.println("分割払いで支払い");
        IntStream.range(0, 6).forEach(n -> Market.INSTANCE.nextEndOfMonth());
        int newCollectedTaxes = nation.books().get(GovernmentTitle.INCOME_TAX) + nation.books().get(GovernmentTitle.CONSUMPTION_TAX);
        System.out.printf("nation: %s%n", nation.books().toString());
        int paid = price - nation.expenditureBurden();
        assertThat(nation.expenditureBurden(), is(lessThan(price)));
        assertThat(nation.books().get(GovernmentTitle.FIXEDASSET_SUSPENSE_ACCOUNT), is(paid));
        assertThat(nation.deposit(), is(bondsOnGovernmentBooks + newCollectedTaxes - paid));
        checkAccount(nation);

        System.out.println("払いきると建物を引き替え");
        while(true) {
            Market.INSTANCE.nextEndOfMonth();
            if (nation.expenditureBurden() <= 0) {
                break;
            }
        }
        System.out.printf("nation: %s%n", nation.books().toString());
        int lastCollectedTaxes = nation.books().get(GovernmentTitle.INCOME_TAX) + nation.books().get(GovernmentTitle.CONSUMPTION_TAX);
        assertThat(nation.books().get(GovernmentTitle.BUILDINGS), is(price));
        assertThat(nation.books().get(GovernmentTitle.FIXEDASSET_SUSPENSE_ACCOUNT), is(0));
        assertThat(nation.expenditureBurden(), is(0));
        assertThat(nation.deposit(), is(bondsOnGovernmentBooks + lastCollectedTaxes - price));
        checkAccount(nation);
        System.out.println("--- order ---");
    }

    @Test
    public void orderAndFixedAssetsTest() {
        System.out.println("--- fixed assets ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        int salary = cbank.paySalary(founder);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness business = founder.establish(Industry.ARCHITECTURE, capital).get();
        int price = nation.order(Product.BUILDINGS).getAsInt();
        while(true) {
            Market.INSTANCE.nextEndOfMonth();
            if (nation.expenditureBurden() <= 0) {
                break;
            }
        }
        System.out.printf("nation: %s%n", nation.books().toString());
        assertThat(nation.books().get(GovernmentTitle.BUILDINGS), is(price));


        IntStream.range(0, 3).forEach(n -> Market.INSTANCE.nextEndOfMonth());
        System.out.println("after depreciation");
        System.out.printf("nation: %s%n", nation.books().toString());
        int depreciation = nation.books().get(GovernmentTitle.DEPRECIATION);
        assertThat(depreciation, is(not(0)));
        if (nation.books().byDirect()) {
            assertThat(nation.books().get(GovernmentTitle.BUILDINGS), is(price - depreciation));
        } else {
            assertThat(nation.books().get(GovernmentTitle.ACCUMULATED_DEPRECIATION), is(-depreciation));
        }
        assertThat(nation.books().fixedAssetsValue(), is(price - depreciation));

        System.out.println("--- fixed assets ---");
    }
}
