package jp.gr.java_conf.falius.economy2.player.bank;

import jp.gr.java_conf.falius.economy2.account.CentralBankAccount;
import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.player.Worker;

public class CentralBank implements Bank {
    public static final CentralBank INSTANCE;
    private static final int SALARY = 100000;

    private final CentralBankAccount mAccount = CentralBankAccount.newInstance();
    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);

    static {
        INSTANCE = new CentralBank();
    }

    private CentralBank() {}

    @Override
    public CentralBankAccount accountBook() {
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

    /**
     * 国債を引き受けます。
     * @param amount
     * @return
     */
    public Bank acceptGovernmentBond(int amount) {
        mAccount.acceptGovernmentBond(amount);
        return this;
    }

    /**
     * 保有国債が償還されます。
     * @param amount
     * @return
     */
    public Bank redeemedGovernmentBond(int amount) {
        mAccount.redeemedGovernmentBond(amount);
        return this;
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

}
