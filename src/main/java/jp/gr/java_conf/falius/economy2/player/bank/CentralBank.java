package jp.gr.java_conf.falius.economy2.player.bank;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.account.CentralAccount;
import jp.gr.java_conf.falius.economy2.account.NationAccount;
import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.account.Transferable;
import jp.gr.java_conf.falius.economy2.agreement.Bond;
import jp.gr.java_conf.falius.economy2.book.CentralBankBooks;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.enumpack.CentralBankTitle;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.Employable;
import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.player.Worker;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class CentralBank implements Bank, Transferable {
    public static final CentralBank INSTANCE;

    private final CentralBankBooks mBooks = CentralBankBooks.newInstance();
    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final Map<AccountOpenable, CentralAccount> mAccounts = new HashMap<>();
    private final NationAccount mNationAccount = new NationAccount(this);

    static {
        INSTANCE = new CentralBank();
    }

    /**
     * @since 1.0
     */
    private CentralBank() {
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBankBooks books() {
        return mBooks;
    }

    /**
     *
     * @param owner
     * @return
     * @since 1.0
     */
    public CentralAccount account(PrivateBank owner) {
        return Objects.requireNonNull(mAccounts.get(owner),
                () -> String.format("%s has no account of %s", this.toString(), owner.toString()));
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public NationAccount nationAccount() {
        return mNationAccount;
    }

    /**
     *
     * @param privateBank
     * @since 1.0
     */
    public CentralAccount createAccount(PrivateBank privateBank) {
        CentralAccount account = new CentralAccount(this, privateBank);
        mAccounts.put(privateBank, account);
        return account;
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
     * 市中銀行からの預け入れ
     * @since 1.0
     */
    @Override
    public void keep(AccountOpenable openable, int amount) {
        mBooks.keep(amount);
        mAccounts.get(openable).increase(amount);
    }

    /**
     * 市中銀行への払い出し
     * @since 1.0
     */
    @Override
    public void paidOut(AccountOpenable openable, int amount) {
        mBooks.paidOut(amount);
        mAccounts.get(openable).decrease(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public Set<Bond> searchBonds(Set<Bond> bondMarket) {
        Set<Bond> successed = new HashSet<>();
        bondMarket.stream()
                .forEach(bond -> successed.add(bond.accepted(mBooks)));
        return successed;
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBank employ(Worker worker) {
        mStuffManager.employ(worker);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBank fire(Worker worker) {
        mStuffManager.fire(worker);
        return this;
    }

    /**
     * @return 給与の額面金額
     * @since 1.0
     */
    @Override
    public int paySalary(Worker worker, int salary) {
        int takeHome = mBooks.paySalary(salary);
        worker.books().getSalary(salary);
        PrivateAccount workerAccount = worker.books().mainAccount();
        transfer(workerAccount, takeHome);
        return salary;
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable payIncomeTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(CentralBankTitle.DEPOSITS_RECEIVED);
        nationBooks.collectIncomeTaxes(amount);
        mBooks.payIncomeTax(amount);
        transfer(mNationAccount, amount);
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
     * @since 1.0
     */
    public void clear() {
        mBooks.clear();
        mStuffManager.clear();
        mAccounts.clear();
        mNationAccount.clear();
    }

    /**
     *
     * @param amount
     * @since 1.0
     */
    public void keepByNation(int amount) {
        mBooks.keepByNation(amount);
        mNationAccount.increase(amount);
    }

    /**
     *
     * @param amount
     * @since 1.0
     */
    public void paidOutByNation(int amount) {
        mBooks.paidOutByNation(amount);
        mNationAccount.decrease(amount);
    }

    /**
     *
     * @param maxBudget
     * @since 1.0
     */
    public void operateBuying(int maxBudget) {
        Nation.INSTANCE.bonds().stream()
                .filter(bond -> !bond.isPayOff())
                .filter(Bond::ofPrivateBank)
                .forEach(new Consumer<Bond>() {
                    private int mBudget = maxBudget;

                    @Override
                    public void accept(Bond bond) {
                        if (bond.amount() > mBudget) {
                            return;
                        }
                        bond.sellTo(mBooks);
                        mBudget -= bond.amount();
                    }
                });
    }

    /**
     *
     * @param maxAmount
     * @since 1.0
     */
    public void operateSelling(int maxAmount) {
        int amount = Math.min(mBooks.get(CentralBankTitle.GOVERNMENT_BOND), maxAmount);
        Set<Bond> sells = Nation.INSTANCE.bonds().stream()
                .filter(bond -> !bond.isPayOff())
                .filter(Bond::ofCentralBank)
                .filter(new Predicate<Bond>() {
                    private int mAmount = amount;

                    @Override
                    public boolean test(Bond bond) {
                        if (bond.amount() > mAmount) {
                            return false;
                        }
                        mAmount -= bond.amount();
                        return true;
                    }
                }).collect(Collectors.toSet());
        Market.INSTANCE.entities(PrivateBank.class).forEach(pb -> pb.searchBonds(sells));
    }

    /**
     *
     * @param target
     * @param amount
     * @return
     * @since 1.0
     */
    @Override
    public int transfer(Transferable target, int amount) {
        if (target instanceof NationAccount) {
            return transfer((NationAccount) target, amount);
        } else if (target instanceof PrivateAccount) {
            return transfer((PrivateAccount) target, amount);
        } else if (target instanceof CentralAccount) {
            return transfer((CentralAccount) target, amount);
        }
        throw new IllegalArgumentException();
    }

    /**
     *
     * @param target
     * @param amount
     * @return
     * @since 1.0
     */
    private int transfer(CentralAccount target, int amount) {
        return target.increase(amount);
    }

    /**
     * @since 1.0
     */
    private int transfer(PrivateAccount target, int amount) {
        target.bank().books().transfered(amount);
        PrivateBank targetBank = target.bank();
        this.account(targetBank).increase(amount);
        return target.increase(amount);
    }

    /**
     * @since 1.0
     */
    private int transfer(NationAccount target, int amount) {
        return target.increase(amount);
    }

    // テスト用集計メソッド
    /**
     *
     * @return
     * @since 1.0
     */
    public int realDeposits() {
        return mAccounts.values().stream()
                .mapToInt(CentralAccount::amount)
                .sum();
    }

}
