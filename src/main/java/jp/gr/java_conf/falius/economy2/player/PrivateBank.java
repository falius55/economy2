package jp.gr.java_conf.falius.economy2.player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.account.PrivateBankAccount;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;

public class PrivateBank extends AbstractEntity implements Bank, AccountOpenable {
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

    @Override
    public PrivateBankAccount accountBook() {
        return mAccount;
    }

    @Override
    public Bank mainBank() {
        return CentralBank.INSTANCE;
    }

    @Override
    public int cash() {
        return mAccount.get(PrivateBankAccountTitle.CASH);
    }

    @Override
    public int deposit() {
        return mAccount.get(PrivateBankAccountTitle.CHECKING_ACCOUNTS);
    }

    @Override
    public boolean isRecruit() {
        return mStuffManager.isRecruit();
    }

    @Override
    public boolean has(Worker worker) {
        return mStuffManager.has(worker);
    }

    public boolean canLend(int amount) {
        int cash = mAccount.get(PrivateBankAccountTitle.CASH);
        int checking = mAccount.get(PrivateBankAccountTitle.CHECKING_ACCOUNTS);
        return cash + checking >= amount;
    }

    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

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

    /**
     * 民間預金への送金処理
     */
    @Override
    public void transfer(int amount) {
        mAccount.transfer(amount);
    }

    /**
     * 振り込みを受ける
     */
    @Override
    public void transfered(int amount) {
        mAccount.transfered(amount);
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

}
