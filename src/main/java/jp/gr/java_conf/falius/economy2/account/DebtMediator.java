package jp.gr.java_conf.falius.economy2.account;

import java.time.LocalDate;

/**
 *
 * {@code
 *
 *  A.offer(B);
 *
 *      class A {
 *          boolean offer(Bank another) {
 *              DebtMediator dm = new DebtMediator(this, 10000);
 *              return another.review(dm);
 *          }
 *      }
 *
 *      class B {
 *          boolean review(content) {
 *              content.accepted(this);
 *          }
 *      }
 * }
 * @author "ymiyauchi"
 *
 */
public class DebtMediator {
    private LocalDate mAccrualDate; // 債権債務発生日
    private LocalDate mDeadLine; // 期限
    private Account<?> mCreditorAccount = null; // 債権者の会計
    private Account<?> mDebtorAccount; // 債務者の会計
    private int mAmount = 0; // 金額

    public DebtMediator(Account<?> debtorAccount, int amount) {
        mDebtorAccount = debtorAccount;
        mAmount = amount;
    }
    /**
     * 債務が受け入れられ、債権債務関係が発生する
     */
    public DebtMediator accepted(Account<?> creditorAccount, LocalDate date) {
        if (mCreditorAccount != null) throw new IllegalStateException("債権債務関係はすでに発生しています");
        mCreditorAccount = creditorAccount;
        mAccrualDate = date;
        mDebtorAccount.borrow(amount());
        creditorAccount.lend(amount());
        return this;
    }

    /**
     * 債権を譲渡する
     * @param creditorAccount 新たな債権者の会計
     */
    public DebtMediator transfer(Account<?> creditorAccount) {
        mCreditorAccount = creditorAccount;
        return this;
    }

    /**
     * 残高
     */
    public int amount() {
        return mAmount;
    }

    public LocalDate deadLine() {
        return mDeadLine;
    }

    /**
     * 借金を減らす
     * @return 借金が完済されればtrue
     */
    public boolean repay(int amount) {
        amount = amount <= mAmount ? amount : mAmount;
        mDebtorAccount.repay(amount);
        mCreditorAccount.repaid(amount);
        return mAmount == 0;
    }
}
