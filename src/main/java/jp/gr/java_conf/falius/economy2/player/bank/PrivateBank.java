package jp.gr.java_conf.falius.economy2.player.bank;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import jp.gr.java_conf.falius.economy2.account.CentralAccount;
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
public class PrivateBank implements Bank, PrivateEntity, Lendable {
    /**
     * 債券購入に充てられる最高割合(保有している現預金のうち何割までなら債券購入に使っていいか)
     */
    public static final double BOND_RATIO = 0.3d;

    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final Set<Loan> mLoans = new HashSet<>();
    private final Map<AccountOpenable, PrivateAccount> mAccounts = new HashMap<>();
    private final PrivateBankBooks mBooks;

    /**
     * @since 1.0
     */
    public PrivateBank() {
        Market.INSTANCE.aggregater().add(this);
        CentralAccount account = mainBank().createAccount(this);
        mBooks = PrivateBankBooks.newInstance(account);
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
        return Objects.requireNonNull(mAccounts.get(accountOpenable),
                () -> String.format("%s has no account of %s", this.toString(), accountOpenable.toString()));
    }

    /**
     *
     * @param accountOpenable
     * @since 1.0
     */
    public PrivateAccount createAccount(AccountOpenable accountOpenable) {
        PrivateAccount account = new PrivateAccount(this, accountOpenable);
        mAccounts.put(accountOpenable, account);
        return account;
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
     * 日課処理を行います。
     * @since 1.0
     */
    public void closeEndOfDay(LocalDate date) {
        if (date.getDayOfMonth() == 21) {
            paySalaries();
        }
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
    public int paySalary(Worker worker, int salary) {
        int takeHome = mBooks.paySalary(salary);
        worker.books().getSalary(salary);
        PrivateAccount workerAccount = worker.books().mainAccount();
        mainBank().account(this).transfer(workerAccount, takeHome);
        return salary;
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable payIncomeTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(PrivateBankTitle.DEPOSITS_RECEIVED);
        nationBooks.collectIncomeTaxes(amount);
        mBooks.payIncomeTax(amount);
        mainBank().account(this).transfer(nationBooks.mainAccount(), amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public Set<Worker> employers() {
        return mStuffManager.employers();
    }

    /**
     * 借金の申し込むを受け入れ、お金を貸します
     * @return 貸した金額
     * @since 1.0
     */
    @Override
    public int acceptDebt(Loan debt) {
        mLoans.add(debt);
        debt.accepted(this);
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

    boolean check() {
        int loan = mLoans.stream().mapToInt(Loan::amount).sum();
        if (loan != mBooks.get(PrivateBankTitle.LOANS_RECEIVABLE)) {
            return false;
        }
        int accountDeposit = mAccounts.values().stream()
                .mapToInt(PrivateAccount::amount)
                .sum();
        if (accountDeposit != mBooks.get(PrivateBankTitle.DEPOSIT)) {
            return false;
        }
        int centralAccountDeposit = mainBank().account(this).amount();
        if (centralAccountDeposit != deposit()) {
            return false;
        }
        return true;
    }

}
