package jp.gr.java_conf.falius.economy2.player;

import java.util.Optional;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.CentralBankAccount;

public class CentralBank extends AbstractEntity implements Bank {
    public static final CentralBank INSTANCE;

    private final CentralBankAccount mAccount = CentralBankAccount.newInstance();
    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);

    static {
        INSTANCE = new CentralBank();
    }

    private CentralBank() {
    }

    @Override
    public Entity saveMoney(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Entity downMoney(int amount) {
        throw new UnsupportedOperationException();
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

    /**
     * 民間からの預け入れ
     */
    @Override
    public void keep(int amount) {
        mAccount.keep(amount);
    }

    @Override
    public void paidOut(int amount) {
        mAccount.paidOut(amount);
    }

    @Override
    protected Account<? extends Enum<?>> account() {
        return mAccount;
    }

    @Override
    protected Optional<Bank> searchBank() {
        return Optional.empty();
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
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ
    }

}
