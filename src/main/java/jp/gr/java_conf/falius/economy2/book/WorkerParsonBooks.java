package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.enumpack.TitleType;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class WorkerParsonBooks extends AbstractBooks<WorkerParsonTitle>
        implements PrivateBooks<WorkerParsonTitle>, BorrowableBooks<WorkerParsonTitle>,
        AccountOpenableBooks<WorkerParsonTitle> {
    private final PrivateAccount mAccount;

    /**
     *
     * @return
     * @since 1.0
     */
    public static WorkerParsonBooks newInstance(PrivateAccount mainAccount) {
        return new WorkerParsonBooks(mainAccount);
    }

    /**
     * @since 1.0
     */
    private WorkerParsonBooks(PrivateAccount mainAccount) {
        super(WorkerParsonTitle.class);
        mAccount = mainAccount;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateAccount mainAccount() {
        return mAccount;
    }

    /**
     * @since 1.0
     */
    @Override
    public WorkerParsonBooks saveMoney(int amount) {
        super.increase(WorkerParsonTitle.ORDINARY_DEPOSIT, amount);
        super.decrease(WorkerParsonTitle.CASH, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public WorkerParsonBooks downMoney(int amount) {
        super.increase(WorkerParsonTitle.CASH, amount);
        super.decrease(WorkerParsonTitle.ORDINARY_DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public WorkerParsonBooks borrow(int amount) {
        super.increase(WorkerParsonTitle.ORDINARY_DEPOSIT, amount);
        super.increase(WorkerParsonTitle.LOANS_PAYABLE, amount);
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
    public Books<WorkerParsonTitle> getSalary(int amount) {
        int tax = Taxes.computeIncomeTaxFromManthly(amount);
        super.increase(WorkerParsonTitle.SALARIES, amount);
        super.increase(WorkerParsonTitle.ORDINARY_DEPOSIT, amount - tax);
        super.increase(WorkerParsonTitle.TAX, tax);
        return this;
    }

    /**
     * 会社を設立した時の仕訳
     * @param initialCapital
     * @return
     * @since 1.0
     */
    public WorkerParsonBooks establish(int initialCapital) {
        super.increase(WorkerParsonTitle.ESTABLISH_EXPENSES, initialCapital);
        super.decrease(WorkerParsonTitle.ORDINARY_DEPOSIT, initialCapital);
        return this;
    }

    /**
     *
     * @param expenseTitle
     * @param amount
     * @return
     * @since 1.0
     */
    public WorkerParsonBooks buyOnCash(WorkerParsonTitle expenseTitle, int amount) {
        if (expenseTitle.type() != TitleType.EXPENSE) {
            throw new IllegalArgumentException();
        }
        super.increase(expenseTitle, amount);
        super.decrease(WorkerParsonTitle.CASH, amount);
        return this;
    }

}
