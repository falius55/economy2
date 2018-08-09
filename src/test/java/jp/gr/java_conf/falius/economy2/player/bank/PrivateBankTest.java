package jp.gr.java_conf.falius.economy2.player.bank;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.EnumSet;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankTitle;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

public class PrivateBankTest {

    @After
    public void clear() {
        Market.INSTANCE.clear();
    }

    private void checkAccount(PrivateBank bank) {
        assertThat(bank.mainBank().account(bank).amount(), is(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS)));
        assertThat(bank.realDeposits(), is(bank.books().get(PrivateBankTitle.DEPOSIT)));
    }

    @Test
    public void establishBusinessTest() {
        System.out.println("--- establish business ---");
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        checkAccount(bank);

        System.out.println(bank.books().toString());

        PrivateBusiness pb = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(capital));
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(capital));
        checkAccount(bank);

        System.out.println("--- establish business ---");
    }

    @Test
    public void borrowToWorkerTest() {
        System.out.println("--- borrow ---");
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        checkAccount(bank);

        System.out.println(bank.books().toString());
        int amount = capital;
        worker.borrow(amount);
        System.out.println(bank.books().toString());
        assertThat(bank.deposit(), is(capital - amount + amount)); // 自分に振り込むので変化なし
        assertThat(bank.books().get(PrivateBankTitle.LOANS_RECEIVABLE), is(amount));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(capital + amount));
        checkAccount(bank);

        System.out.println(cbank.books().toString());

        System.out.println("--- borrow ---");
    }

    @Test
    public void borrowToBusinessTest() {
        System.out.println("--- borrow to business ---");
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int capital = salary - tax;
        PrivateBusiness farmer = worker.establish(Industry.FARMER, EnumSet.of(Product.RICE), capital).get();
        checkAccount(bank);

        int amount = capital;
        farmer.borrow(amount);
        assertThat(bank.deposit(), is(capital - amount + amount)); // 自分に振り込むので変化なし
        assertThat(bank.books().get(PrivateBankTitle.LOANS_RECEIVABLE), is(amount));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(capital + amount));
        checkAccount(bank);
        System.out.println("--- borrow to business ---");
    }

    @Test
    public void distributionTest() {
        System.out.println("--- distribution ---");
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

        int countToEndOfMonth = Market.INSTANCE.nowDate().lengthOfMonth()
                - Market.INSTANCE.nowDate().getDayOfMonth();
        IntStream.range(0, countToEndOfMonth).forEach(n -> Market.INSTANCE.nextDay());
        System.out.printf("worker: %s%n", worker.books().toString());
        System.out.printf("farmer: %s%n", farmer.books().toString());
        System.out.printf("maker: %s%n", maker.books().toString());
        System.out.printf("coop: %s%n", coop.books().toString());
        System.out.printf("bank : %s%n", bank.books().toString());

        int loan = 0;
        loan += worker.books().get(WorkerParsonTitle.LOANS_PAYABLE);
        loan += farmer.books().get(PrivateBusinessTitle.LOANS_PAYABLE);
        loan += maker.books().get(PrivateBusinessTitle.LOANS_PAYABLE);
        loan += coop.books().get(PrivateBusinessTitle.LOANS_PAYABLE);
        assertThat(bank.books().get(PrivateBankTitle.LOANS_RECEIVABLE), is(loan));

        int deposit = 0;
        deposit += worker.books().get(WorkerParsonTitle.ORDINARY_DEPOSIT);
        deposit += farmer.books().get(PrivateBusinessTitle.CHECKING_ACCOUNTS);
        deposit += maker.books().get(PrivateBusinessTitle.CHECKING_ACCOUNTS);
        deposit += coop.books().get(PrivateBusinessTitle.CHECKING_ACCOUNTS);
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(deposit));

        assertThat(bank.books().get(PrivateBankTitle.CASH)
                + bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS)
                + bank.books().get(PrivateBankTitle.LOANS_RECEIVABLE),
                is(deposit));
        checkAccount(bank);

        System.out.println("--- distribution ---");
    }

    @Test
    public void nationBondTest() {
        System.out.println("--- nationBond ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;

        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);
        checkAccount(bank);

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.printf("worker: %s%n", worker.books().toString());
        System.out.printf("bank: %s%n", bank.books().toString());
        System.out.printf("cbank: %s%n", cbank.books().toString());
        System.out.printf("nation: %s%n", nation.books().toString());
        assertThat(bank.deposit(), is(moneyStock - price * count));
        assertThat(bank.books().get(PrivateBankTitle.GOVERNMENT_BOND), is(price * count));
        checkAccount(bank);

        System.out.println("--- nationBond ---");
    }

    @Test
    public void operateSellingTest() {
        System.out.println("--- operate selling ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        int count = 10;
        int price = 1000;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.makeUnderwriteBonds(cbank);
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        checkAccount(bank);
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int amount = Math.min(salary - tax, price * count);
        cbank.operateSelling(amount);
        System.out.println(bank.books().toString());

        assertThat(bank.books().get(PrivateBankTitle.GOVERNMENT_BOND), is(amount));
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(salary - tax - amount));
        checkAccount(bank);
        System.out.println("--- operate selling ---");
    }

    @Test
    public void operateBuyingTest() {
        System.out.println("--- operate buying ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;
        System.out.println(bank.books().toString());
        checkAccount(bank);

        int count = 10;
        int price = (int) (moneyStock * PrivateBank.BOND_RATIO / count);
        IntStream.range(0, count).forEach(n -> nation.issueBonds(price));
        nation.advertiseBonds();
        System.out.println(bank.books().toString());
        checkAccount(bank);

        cbank.operateBuying(price * count);
        System.out.println(bank.books().toString());
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(moneyStock));
        assertThat(bank.books().get(PrivateBankTitle.GOVERNMENT_BOND), is(0));
        checkAccount(bank);

        System.out.println("--- operate buying ---");
    }

    @Test
    public void paySalaryTest() {
        System.out.println("--- paySalary ---");
        CentralBank central = CentralBank.INSTANCE;
        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = central.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(salary - tax));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(salary - tax));

        int beforeChecking = salary - tax;
        int beforeDeposit = salary - tax;
        int bankSalary = bank.paySalary(worker);
        int bankTax = Taxes.computeIncomeTaxFromManthly(bankSalary);
        assertThat(bank.books().get(PrivateBankTitle.SALARIES_EXPENSE), is(bankSalary));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSITS_RECEIVED), is(bankTax));
        // 自分に振り込まれるので変わらず ↓
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(beforeChecking));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(beforeDeposit + bankSalary - bankTax));
        checkAccount(bank);

        System.out.println("--- paySalary ---");
    }

    @Test
    public void advertiseBondsTest() {
        System.out.println("--- advertise ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;

        PrivateBank bank = new PrivateBank();
        WorkerParson worker = new WorkerParson();
        int salary = cbank.paySalary(worker);
        int tax = Taxes.computeIncomeTaxFromManthly(salary);
        int moneyStock = salary - tax;
        checkAccount(bank);

        int count = 10;
        int faceValue = (int) (moneyStock * PrivateBank.BOND_RATIO / count);
        int allAmount = faceValue * count;

        IntStream.range(0, count).forEach(n -> nation.issueBonds(faceValue));
        nation.advertiseBonds();
        System.out.println(nation.books().toString());
        assertThat(bank.books().get(PrivateBankTitle.GOVERNMENT_BOND), is(allAmount));
        assertThat(bank.deposit(), is(moneyStock - allAmount));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(moneyStock));
        checkAccount(bank);

        System.out.println("--- advertise ---");
    }

    @Test
    public void nationOrderTest() {
        System.out.println("--- order ---");
        Nation nation = Nation.INSTANCE;
        CentralBank cbank = CentralBank.INSTANCE;

        PrivateBank bank = new PrivateBank();
        WorkerParson founder = new WorkerParson();
        IntStream.range(0, 100).map(n -> cbank.paySalary(founder)).sum();
        int capital = founder.deposit();

        PrivateBusiness business = founder.establish(Industry.ARCHITECTURE, capital).get();
        System.out.println("創業時");
        System.out.printf("bank: %s%n", bank.books().toString());

        int price = nation.order(Product.BUILDINGS).getAsInt();

        System.out.println("支出負担分、国債を発行する。");
        nation.closeEndOfMonth();
        System.out.printf("bank: %s%n", bank.books().toString());

        System.out.println("分割払いで支払い");
        int oldDeposit = business.deposit();
        IntStream.range(0, 6).forEach(n -> Market.INSTANCE.nextEndOfMonth());
        System.out.printf("bank: %s%n", bank.books().toString());
        System.out.printf("business: %s%n", business.books().toString());
        int paid = price - nation.expenditureBurden();
        int bondsAmount = bank.books().get(PrivateBankTitle.GOVERNMENT_BOND);
        int expectCentralAccount = capital - bondsAmount + paid;
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(expectCentralAccount));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(oldDeposit + paid));
        checkAccount(bank);

        System.out.println("払いきると建物を引き替え");
        while (true) {
            Market.INSTANCE.nextEndOfMonth();
            if (nation.expenditureBurden() <= 0) {
                break;
            }
        }
        System.out.printf("bank: %s%n", bank.books().toString());
        System.out.printf("business: %s%n", business.books().toString());
        assertThat(bank.books().get(PrivateBankTitle.CHECKING_ACCOUNTS), is(capital - bondsAmount + price));
        assertThat(bank.books().get(PrivateBankTitle.DEPOSIT), is(oldDeposit + price));
        assertThat(bank.deposit() + bank.books().get(PrivateBankTitle.GOVERNMENT_BOND),
                is(bank.books().get(PrivateBankTitle.DEPOSIT)));
        checkAccount(bank);

        System.out.println("--- order ---");
    }
}
