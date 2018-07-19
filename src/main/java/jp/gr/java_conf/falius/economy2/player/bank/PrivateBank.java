package jp.gr.java_conf.falius.economy2.player.bank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.GovernmentAccount;
import jp.gr.java_conf.falius.economy2.account.PrivateBankAccount;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;
import jp.gr.java_conf.falius.economy2.loan.Bond;
import jp.gr.java_conf.falius.economy2.loan.Loan;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.Employable;
import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.player.PrivateEntity;
import jp.gr.java_conf.falius.economy2.player.Worker;

public class PrivateBank implements Bank, AccountOpenable, PrivateEntity {
    /**
     * 債券購入に充てられる最高割合(保有している現預金のうち何割までなら債券購入に使っていいか)
     */
    public static final double BOND_RATIO = 0.3d;

    private static final int SALARY = 50000;
    private static final List<PrivateBank> sOwns = new ArrayList<PrivateBank>();

    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final PrivateBankAccount mAccount = PrivateBankAccount.newInstance();
    private final Set<Loan> mLoans = new HashSet<>();

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
    public Account<PrivateBankAccountTitle> accountBook() {
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
                            bond.sellTo(mAccount);
                        }
                        if (!bond.isConcluded()) {
                            bond.accepted(mAccount);
                        }
                        mBudget -= bond.amount();
                        successed.add(bond);
                    }

                });
        return successed;
    }

    @Override
    public AccountOpenable saveMoney(int amount) {
        mAccount.saveMoney(amount);
        mainBank().keep(amount);
        return this;
    }

    @Override
    public AccountOpenable downMoney(int amount) {
        mAccount.downMoney(amount);
        mainBank().paidOut(amount);
        return this;
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
        mAccount.paySalary(SALARY);
        worker.getSalary(SALARY);
        return SALARY;
    }

    @Override
    public Employable payIncomeTax(GovernmentAccount nationAccount) {
        int amount = mAccount.get(PrivateBankAccountTitle.DEPOSITS_RECEIVED);
        nationAccount.collectIncomeTaxes(amount);
        mAccount.payIncomeTax(amount);
        CentralBank.INSTANCE.transfered(amount);
        return this;
    }

    /**
     * 借金の申し込むを受け入れ、お金を貸します
     * @return 貸した金額
     */
    public int acceptDebt(Loan debt) {
        mLoans.add(debt);
        debt.accepted(mAccount);
        return debt.amount();
    }

}
