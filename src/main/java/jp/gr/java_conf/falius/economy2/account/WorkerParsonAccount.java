package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;

public class WorkerParsonAccount extends AbstractAccount<WorkerParsonAccountTitle>
        implements PrivateAccount<WorkerParsonAccountTitle>, BorrowableAccount<WorkerParsonAccountTitle> {

    public static WorkerParsonAccount newInstance() {
        return new WorkerParsonAccount();
    }

    private WorkerParsonAccount() {
        super(WorkerParsonAccountTitle.class);
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
    public WorkerParsonAccount borrow(int amount) {
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        super.increase(WorkerParsonAccountTitle.LOANS_PAYABLE, amount);
        return this;
    }

    @Override
    public WorkerParsonAccount repay(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    /**
     * 給与を銀行振り込みで受け取ります。
     * @param amount
     * @return
     */
    public Account<WorkerParsonAccountTitle> getSalary(int amount) {
        int tax = Taxes.computeIncomeTaxFromManthly(amount);
        super.increase(WorkerParsonAccountTitle.SALARIES, amount);
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount - tax);
        super.increase(WorkerParsonAccountTitle.TAX, tax);
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
