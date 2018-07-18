package jp.gr.java_conf.falius.economy2.player.bank;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.CentralBankAccount;
import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.loan.Bond;
import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.player.Worker;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

public class CentralBank implements Bank {
    public static final CentralBank INSTANCE;
    private static final int SALARY = 100000;

    private final CentralBankAccount mAccount = CentralBankAccount.newInstance();
    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);

    static {
        INSTANCE = new CentralBank();
    }

    private CentralBank() {
    }

    @Override
    public Account<CentralBankAccountTitle> accountBook() {
        return mAccount;
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
    public void keep(int amount) {
        mAccount.keep(amount);
    }

    /**
     * 市中銀行への払い出し
     */
    @Override
    public void paidOut(int amount) {
        mAccount.paidOut(amount);
    }

    @Override
    public void transfer(int amount) {
        mAccount.transfer(amount);
    }

    @Override
    public void transfered(int amount) {
        mAccount.transfered(amount);
    }

    @Override
    public Set<Bond> searchBonds(Set<Bond> bondMarket) {
        Set<Bond> successed = new HashSet<>();
        bondMarket.stream()
                .forEach(bond -> successed.add(bond.accepted(mAccount)));
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

    @Override
    public int paySalary(Worker worker) {
        mAccount.paySalary(SALARY);
        worker.getSalary(SALARY);
        return SALARY;
    }

    public void clear() {
        mAccount.clearBook();
    }

    public void keepByNation(int amount) {
        mAccount.keepByNation(amount);
    }

    public void paidOutByNation(int amount) {
        mAccount.paidOutByNation(amount);
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
                        bond.sellTo(mAccount);
                        mBudget -= bond.amount();
                    }
                });
    }

    public void operateSelling(int maxAmount) {
        int amount = Math.min(mAccount.get(CentralBankAccountTitle.GOVERNMENT_BOND), maxAmount);
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

}
