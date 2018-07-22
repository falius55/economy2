package jp.gr.java_conf.falius.economy2.player.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.account.NationAccount;
import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.book.PrivateBankBooks;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;
import jp.gr.java_conf.falius.economy2.loan.Bond;
import jp.gr.java_conf.falius.economy2.loan.Loan;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.Employable;
import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.player.Lendable;
import jp.gr.java_conf.falius.economy2.player.PrivateEntity;
import jp.gr.java_conf.falius.economy2.player.Worker;

public class PrivateBank implements Bank, AccountOpenable, PrivateEntity, Lendable {
    /**
     * 債券購入に充てられる最高割合(保有している現預金のうち何割までなら債券購入に使っていいか)
     */
    public static final double BOND_RATIO = 0.3d;

    private static final int SALARY = 50000;
    private static final List<PrivateBank> sOwns = new ArrayList<PrivateBank>();

    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final PrivateBankBooks mBooks = PrivateBankBooks.newInstance();
    private final Set<Loan> mLoans = new HashSet<>();
    private final Map<AccountOpenable, PrivateAccount> mAccounts = new HashMap<>();

    public static Stream<PrivateBank> stream() {
        return sOwns.stream();
    }

    public static void clear() {
        sOwns.clear();
    }

    public PrivateBank() {
        sOwns.add(this);
        Market.INSTANCE.aggregater().add(this);
        mainBank().createAccount(this);
    }

    @Override
    public PrivateBankBooks books() {
        return mBooks;
    }

    @Override
    public CentralBank mainBank() {
        return CentralBank.INSTANCE;
    }

    public PrivateAccount account(AccountOpenable accountOpenable) {
        return mAccounts.get(accountOpenable);
    }

    public void createAccount(AccountOpenable accountOpenable) {
        PrivateAccount account = new PrivateAccount(this, accountOpenable);
        mAccounts.put(accountOpenable, account);
    }

    @Override
    public int cash() {
        return mBooks.get(PrivateBankAccountTitle.CASH);
    }

    @Override
    public int deposit() {
        return mBooks.get(PrivateBankAccountTitle.CHECKING_ACCOUNTS);
    }

    @Override
    public boolean isRecruit() {
        return mStuffManager.isRecruit();
    }

    @Override
    public boolean has(Worker worker) {
        return mStuffManager.has(worker);
    }

    @Override
    public boolean canLend(int amount) {
        int cash = mBooks.get(PrivateBankAccountTitle.CASH);
        int checking = mBooks.get(PrivateBankAccountTitle.CHECKING_ACCOUNTS);
        return cash + checking >= amount;
    }

    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void keep(AccountOpenable openable, int amount) {
        mBooks.keep(amount);
        mAccounts.get(openable).increase(amount);
    }

    @Override
    public void paidOut(AccountOpenable openable, int amount) {
        int cash = mBooks.get(PrivateBankAccountTitle.CASH);
        if (cash < amount) {
            downMoney(amount - cash);
        }
        mBooks.paidOut(amount);
        mAccounts.get(openable).decrease(amount);
    }

    @Override
    public Set<Bond> searchBonds(Set<Bond> bondMarket) {
        Set<Bond> successed = new HashSet<>();
        int budget = (int) ((cash() + deposit()) * BOND_RATIO);
        bondMarket.stream()
                .filter(bond -> !bond.isConcluded() || bond.ofCentralBank())
                .forEach(new Consumer<Bond>() {
                    private int mBudget = budget;

                    @Override
                    public void accept(Bond bond) {
                        if (bond.amount() > mBudget) {
                            return;
                        }
                        if (bond.ofCentralBank()) {
                            bond.sellTo(mBooks);
                        }
                        if (!bond.isConcluded()) {
                            bond.accepted(mBooks);
                        }
                        mBudget -= bond.amount();
                        successed.add(bond);
                    }

                });
        return successed;
    }

    @Override
    public int transfer(NationAccount target, int amount) {
        return mainBank().account(this).transfer(target, amount);
    }

    @Override
    public AccountOpenable saveMoney(int amount) {
        mBooks.saveMoney(amount);
        mainBank().keep(this, amount);
        return this;
    }

    @Override
    public AccountOpenable downMoney(int amount) {
        mBooks.downMoney(amount);
        mainBank().paidOut(this, amount);
        return this;
    }

    @Override
    public Employable employ(Worker worker) {
        mStuffManager.employ(worker);
        return this;
    }

    @Override
    public Employable fire(Worker worker) {
        mStuffManager.fire(worker);
        return this;
    }

    @Override
    public int paySalary(Worker worker) {
        mBooks.paySalary(SALARY);
        worker.getSalary(this, SALARY);
        return SALARY;
    }

    @Override
    public int transfer(PrivateAccount target, int amount) {
        int ret = mainBank().account(this).transfer(target, amount);
        return ret;
    }

    @Override
    public Employable payIncomeTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(PrivateBankAccountTitle.DEPOSITS_RECEIVED);
        nationBooks.collectIncomeTaxes(amount);
        mBooks.payIncomeTax(amount);
        mainBank().account(this).transfer(mainBank().nationAccount(), amount);
        return this;
    }

    /**
     * 借金の申し込むを受け入れ、お金を貸します
     * @return 貸した金額
     */
    public int acceptDebt(Loan debt) {
        mLoans.add(debt);
        debt.accepted(this, mainBank().account(this));
        return debt.amount();
    }

    // テスト用集計メソッド
    public int realDeposits() {
        return mAccounts.entrySet().stream()
                .map(Map.Entry::getValue)
                .mapToInt(PrivateAccount::amount)
                .sum();
    }

}
