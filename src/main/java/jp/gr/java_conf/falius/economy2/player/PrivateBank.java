package jp.gr.java_conf.falius.economy2.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.account.PrivateBankAccount;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;

public class PrivateBank extends AbstractEntity implements Bank {
    private static final List<PrivateBank> sOwns = new ArrayList<PrivateBank>();

    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final PrivateBankAccount mAccount = PrivateBankAccount.newInstance();

    public static Stream<PrivateBank> stream() {
        return sOwns.stream();
    }

    public static void clear() {
        sOwns.clear();
    }

    public PrivateBank() {
        sOwns.add(this);
    }

    public boolean canLend(int amount) {
        int cash = mAccount.get(PrivateBankAccountTitle.CASH);
        int checking = mAccount.get(PrivateBankAccountTitle.CHECKING_ACCOUNTS);
        return cash + checking >= amount;
    }

    @Override
    public boolean isRecruit() {
        return mStuffManager.isRecruit();
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
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public boolean has(Worker worker) {
        return mStuffManager.has(worker);
    }

    @Override
    public void keep(int amount) {
        mAccount.keep(amount);
    }

    @Override
    public void paidOut(int amount) {
        int cash = mAccount.get(PrivateBankAccountTitle.CASH);
        if (cash < amount) {
            downMoney(amount - cash);
        }
        mAccount.paidOut(amount);
    }

    @Override
    protected PrivateBankAccount account() {
        return mAccount;
    }

    @Override
    protected Optional<Bank> searchBank() {
        return Optional.of(CentralBank.INSTANCE);
    }

    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void transfer(int amount) {
        mAccount.transfer(amount);
    }

    @Override
    public void transfered(int amount) {
        mAccount.transfered(amount);
    }

}
