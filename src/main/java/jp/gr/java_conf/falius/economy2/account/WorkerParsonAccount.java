package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;

public class WorkerParsonAccount extends AbstractAccount<WorkerParsonAccountTitle> {

    public static WorkerParsonAccount newInstance() {
        return new WorkerParsonAccount();
    }

    private WorkerParsonAccount() {
        super(WorkerParsonAccountTitle.class);
    }

    @Override
    protected WorkerParsonAccountTitle[] items() {
        return WorkerParsonAccountTitle.values();
    }

    @Override
    public Account<WorkerParsonAccountTitle> saveMoney(int amount) {
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        super.decrease(WorkerParsonAccountTitle.CASH, amount);
        return this;
    }

    @Override
    public Account<WorkerParsonAccountTitle> downMoney(int amount) {
        super.increase(WorkerParsonAccountTitle.CASH, amount);
        super.decrease(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        return this;
    }

    @Override
    public Account<WorkerParsonAccountTitle> borrow(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<WorkerParsonAccountTitle> repay(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<WorkerParsonAccountTitle> lend(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<WorkerParsonAccountTitle> repaid(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<WorkerParsonAccountTitle> payTax(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    /**
     * 給与を銀行振り込みで受け取ります。
     * @param amount
     * @return
     */
    public Account<WorkerParsonAccountTitle> getSalary(int amount) {
        super.increase(WorkerParsonAccountTitle.SALARIES, amount);
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        return this;
    }

    public WorkerParsonAccount establish(int initialCapital) {
        super.increase(WorkerParsonAccountTitle.ESTABLISH_EXPENSES, initialCapital);
        super.decrease(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, initialCapital);
        return this;
    }

    public WorkerParsonAccount buyOnCash(WorkerParsonAccountTitle expenseTitle, int amount) {
        if (expenseTitle.type() != AccountType.EXPENSE) {
            throw new IllegalArgumentException();
        }
        super.increase(expenseTitle, amount);
        super.decrease(WorkerParsonAccountTitle.CASH, amount);
        return this;
    }

}
