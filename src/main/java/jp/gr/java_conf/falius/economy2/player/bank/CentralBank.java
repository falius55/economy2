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
import jp.gr.java_conf.falius.economy2.book.CentralBankBooks;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.loan.Bond;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.Employable;
import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.player.Worker;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

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

    private CentralBank() {
    }

    @Override
    public CentralBankBooks books() {
        return mBooks;
    }

    public CentralAccount account(PrivateBank owner) {
        return mAccounts.get(owner);
    }

    public NationAccount nationAccount() {
        return mNationAccount;
    }

    public void createAccount(PrivateBank privateBank) {
        CentralAccount account = new CentralAccount(this, privateBank);
        mAccounts.put(privateBank, account);
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
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ
    }

    /**
     * 市中銀行からの預け入れ
     */
    @Override
    public void keep(AccountOpenable openable, int amount) {
        mBooks.keep(amount);
        mAccounts.get(openable).increase(amount);
    }

    /**
     * 市中銀行への払い出し
     */
    @Override
    public void paidOut(AccountOpenable openable, int amount) {
        mBooks.paidOut(amount);
        mAccounts.get(openable).decrease(amount);
    }

    @Override
    public Set<Bond> searchBonds(Set<Bond> bondMarket) {
        Set<Bond> successed = new HashSet<>();
        bondMarket.stream()
                .forEach(bond -> successed.add(bond.accepted(mBooks)));
        return successed;
    }

    @Override
    public CentralBank employ(Worker worker) {
        mStuffManager.employ(worker);
        return this;
    }

    @Override
    public CentralBank fire(Worker worker) {
        mStuffManager.fire(worker);
        return this;
    }

    /**
     * @return 給与の額面金額
     */
    @Override
    public int paySalary(Worker worker) {
        mBooks.paySalary(SALARY);
        worker.getSalary(this, SALARY);
        return SALARY;
    }

    @Override
    public Employable payIncomeTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(CentralBankAccountTitle.DEPOSITS_RECEIVED);
        nationBooks.collectIncomeTaxes(amount);
        mBooks.payIncomeTax(amount);
        transfer(mNationAccount, amount);
        return this;
    }

    public void clear() {
        mBooks.clearBook();
        mAccounts.clear();
        mNationAccount.clear();
    }

    public void keepByNation(int amount) {
        mBooks.keepByNation(amount);
        mNationAccount.increase(amount);
    }

    public void paidOutByNation(int amount) {
        mBooks.paidOutByNation(amount);
        mNationAccount.decrease(amount);
    }

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

    public void operateSelling(int maxAmount) {
        int amount = Math.min(mBooks.get(CentralBankAccountTitle.GOVERNMENT_BOND), maxAmount);
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

    public int transfer(CentralAccount target, int amount) {
        return target.increase(amount);
    }

    @Override
    public int transfer(PrivateAccount target, int amount) {
        target.bank().books().transfered(amount);
        PrivateBank targetBank = target.bank();
        CentralBank.INSTANCE.account(targetBank).increase(amount);
        return target.increase(amount);
    }

    @Override
    public int transfer(NationAccount target, int amount) {
        return target.increase(amount);
    }

    // テスト用集計メソッド
    public int realDeposits() {
        return mAccounts.values().stream()
        .mapToInt(CentralAccount::amount)
        .sum();
    }

}
