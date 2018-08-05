package jp.gr.java_conf.falius.economy2.player.bank;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.account.CentralAccount;
import jp.gr.java_conf.falius.economy2.account.NationAccount;
import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.agreement.Bond;
import jp.gr.java_conf.falius.economy2.book.CentralBankBooks;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.enumpack.CentralBankTitle;
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
public class CentralBank implements Bank {
    public static final CentralBank INSTANCE;
    private static final int SALARY = 100000;

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
        return mAccounts.get(owner);
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
    public void createAccount(PrivateBank privateBank) {
        CentralAccount account = new CentralAccount(this, privateBank);
        mAccounts.put(privateBank, account);
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
    public int paySalary(Worker worker) {
        mBooks.paySalary(SALARY);
        worker.getSalary(this, SALARY);
        return SALARY;
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
    public void clear() {
        mBooks.clearBook();
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
        PrivateBank.stream().forEach(pb -> pb.searchBonds(sells));
    }

    /**
     *
     * @param target
     * @param amount
     * @return
     * @since 1.0
     */
    public int transfer(CentralAccount target, int amount) {
        return target.increase(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public int transfer(PrivateAccount target, int amount) {
        target.bank().books().transfered(amount);
        PrivateBank targetBank = target.bank();
        CentralBank.INSTANCE.account(targetBank).increase(amount);
        return target.increase(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public int transfer(NationAccount target, int amount) {
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
