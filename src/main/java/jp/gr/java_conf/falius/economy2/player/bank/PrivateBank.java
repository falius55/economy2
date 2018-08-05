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
import jp.gr.java_conf.falius.economy2.agreement.Bond;
import jp.gr.java_conf.falius.economy2.agreement.Loan;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.book.PrivateBankBooks;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankTitle;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.Employable;
import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.player.Lendable;
import jp.gr.java_conf.falius.economy2.player.PrivateEntity;
import jp.gr.java_conf.falius.economy2.player.Worker;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
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

    /**
     *
     * @return
     * @since 1.0
     */
    public static Stream<PrivateBank> stream() {
        return sOwns.stream();
    }

    /**
     * @since 1.0
     */
    public static void clear() {
        sOwns.clear();
    }

    /**
     * @since 1.0
     */
    public PrivateBank() {
        sOwns.add(this);
        Market.INSTANCE.aggregater().add(this);
        mainBank().createAccount(this);
    }

    /**
     * @since 1.0i
     */
    @Override
    public PrivateBankBooks books() {
        return mBooks;
    }

    /**
     * @since 1.0i
     */
    @Override
    public CentralBank mainBank() {
        return CentralBank.INSTANCE;
    }

    /**
     *
     * @param accountOpenable
     * @return
     * @since 1.0
     */
    public PrivateAccount account(AccountOpenable accountOpenable) {
        return mAccounts.get(accountOpenable);
    }

    /**
     *
     * @param accountOpenable
     * @since 1.0
     */
    public void createAccount(AccountOpenable accountOpenable) {
        PrivateAccount account = new PrivateAccount(this, accountOpenable);
        mAccounts.put(accountOpenable, account);
    }

    /**
     * @since 1.0
     */
    @Override
    public int cash() {
        return mBooks.get(PrivateBankTitle.CASH);
    }

    /**
     * @since 1.0
     */
    @Override
    public int deposit() {
        return mBooks.get(PrivateBankTitle.CHECKING_ACCOUNTS);
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean isRecruit() {
        return mStuffManager.isRecruit();
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean has(Worker worker) {
        return mStuffManager.has(worker);
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean canLend(int amount) {
        int cash = mBooks.get(PrivateBankTitle.CASH);
        int checking = mBooks.get(PrivateBankTitle.CHECKING_ACCOUNTS);
        return cash + checking >= amount;
    }

    /**
     * @since 1.0
     */
    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    /**
     * @since 1.0
     */
    @Override
    public void keep(AccountOpenable openable, int amount) {
        mBooks.keep(amount);
        mAccounts.get(openable).increase(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public void paidOut(AccountOpenable openable, int amount) {
        int cash = mBooks.get(PrivateBankTitle.CASH);
        if (cash < amount) {
            downMoney(amount - cash);
        }
        mBooks.paidOut(amount);
        mAccounts.get(openable).decrease(amount);
    }

    /**
     * @since 1.0
     */
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

    /**
     * @since 1.0
     */
    @Override
    public int transfer(NationAccount target, int amount) {
        return mainBank().account(this).transfer(target, amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public AccountOpenable saveMoney(int amount) {
        mBooks.saveMoney(amount);
        mainBank().keep(this, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public AccountOpenable downMoney(int amount) {
        mBooks.downMoney(amount);
        mainBank().paidOut(this, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable employ(Worker worker) {
        mStuffManager.employ(worker);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable fire(Worker worker) {
        mStuffManager.fire(worker);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public int paySalary(Worker worker) {
        mBooks.paySalary(SALARY);
        worker.getSalary(this, SALARY);
        return SALARY;
    }

    /**
     * @since 1.0
     */
    @Override
    public int transfer(PrivateAccount target, int amount) {
        int ret = mainBank().account(this).transfer(target, amount);
        return ret;
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable payIncomeTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(PrivateBankTitle.DEPOSITS_RECEIVED);
        nationBooks.collectIncomeTaxes(amount);
        mBooks.payIncomeTax(amount);
        mainBank().account(this).transfer(mainBank().nationAccount(), amount);
        return this;
    }

    /**
     * 借金の申し込むを受け入れ、お金を貸します
     * @return 貸した金額
     * @since 1.0
     */
    @Override
    public int acceptDebt(Loan debt) {
        mLoans.add(debt);
        debt.accepted(this, mainBank().account(this));
        return debt.amount();
    }

    // テスト用集計メソッド
    /**
     * @since 1.0
     */
    public int realDeposits() {
        return mAccounts.values().stream()
                .mapToInt(PrivateAccount::amount)
                .sum();
    }

}
