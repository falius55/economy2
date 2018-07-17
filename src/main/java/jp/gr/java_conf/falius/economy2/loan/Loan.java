package jp.gr.java_conf.falius.economy2.loan;

import java.time.LocalDate;
import java.util.Objects;

import jp.gr.java_conf.falius.economy2.account.BorrowableAccount;
import jp.gr.java_conf.falius.economy2.account.LendableAccount;
import jp.gr.java_conf.falius.economy2.market.Market;

/**
 *
 * {@code
 *
 *  A.offer(B);
 *
 *      class A {
 *          boolean offer(Bank another) {
 *              Loan dm = new Loan(this, 10000);
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
public class Loan {
    private LocalDate mAccrualDate; // 債権債務発生日
    private LocalDate mDeadLine; // 期限

    private LendableAccount<?> mCreditorAccount = null; // 債権者の会計
    private BorrowableAccount<?> mDebtorAccount; // 債務者の会計

    private final int mAmount; // 金額

    public Loan(BorrowableAccount<?> debtorAccount, int amount) {
        mDebtorAccount = debtorAccount;
        mAmount = amount;
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

    public Loan accepted(LendableAccount<?> creditorAccount) {
        return accepted(creditorAccount, Market.INSTANCE.nowDate());
    }

    /**
     * 債務が受け入れられ、債権債務関係が発生する
     */
    public Loan accepted(LendableAccount<?> creditorAccount, LocalDate date) {
        if (Objects.nonNull(mCreditorAccount)) {
            throw new IllegalStateException("債権債務関係はすでに発生しています");
        }
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
    public Loan transfer(LendableAccount<?> creditorAccount) {
        mCreditorAccount = creditorAccount;
        return this;
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
