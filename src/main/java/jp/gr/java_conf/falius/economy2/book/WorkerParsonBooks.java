package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class WorkerParsonBooks extends AbstractBooks<WorkerParsonAccountTitle>
        implements PrivateBooks<WorkerParsonAccountTitle>, BorrowableBooks<WorkerParsonAccountTitle>,
        AccountOpenableBooks<WorkerParsonAccountTitle> {

    /**
     *
     * @return
     * @since 1.0
     */
    public static WorkerParsonBooks newInstance() {
        return new WorkerParsonBooks();
    }

    /**
     * @since 1.0
     */
    private WorkerParsonBooks() {
        super(WorkerParsonAccountTitle.class);
    }

    /**
     * @since 1.0
     */
    @Override
    public WorkerParsonBooks saveMoney(int amount) {
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        super.decrease(WorkerParsonAccountTitle.CASH, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public WorkerParsonBooks downMoney(int amount) {
        super.increase(WorkerParsonAccountTitle.CASH, amount);
        super.decrease(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public WorkerParsonBooks borrow(int amount) {
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        super.increase(WorkerParsonAccountTitle.LOANS_PAYABLE, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public WorkerParsonBooks repay(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    /**
     * 給与を銀行振り込みで受け取る時の仕訳
     * @param amount
     * @return
     * @since 1.0
     */
    public Books<WorkerParsonAccountTitle> getSalary(int amount) {
        int tax = Taxes.computeIncomeTaxFromManthly(amount);
        super.increase(WorkerParsonAccountTitle.SALARIES, amount);
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount - tax);
        super.increase(WorkerParsonAccountTitle.TAX, tax);
        return this;
    }

    /**
     * 会社を設立した時の仕訳
     * @param initialCapital
     * @return
     * @since 1.0
     */
    public WorkerParsonBooks establish(int initialCapital) {
        super.increase(WorkerParsonAccountTitle.ESTABLISH_EXPENSES, initialCapital);
        super.decrease(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, initialCapital);
        return this;
    }

    /**
     *
     * @param expenseTitle
     * @param amount
     * @return
     * @since 1.0
     */
    public WorkerParsonBooks buyOnCash(WorkerParsonAccountTitle expenseTitle, int amount) {
        if (expenseTitle.type() != AccountType.EXPENSE) {
            throw new IllegalArgumentException();
        }
        super.increase(expenseTitle, amount);
        super.decrease(WorkerParsonAccountTitle.CASH, amount);
        return this;
    }

}
