package jp.gr.java_conf.falius.economy2.loan;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import jp.gr.java_conf.falius.economy2.account.BorrowableAccount;
import jp.gr.java_conf.falius.economy2.account.LendableAccount;
import jp.gr.java_conf.falius.economy2.market.Market;

/**
 *
 * @author "ymiyauchi"
 *
 */
public class Loan {
    private final int mAmount; // 金額
    private final Period mPeriod; // 返済期間

    private LocalDate mAccrualDate; // 債権債務発生日
    private LocalDate mDeadLine; // 期限

    private LendableAccount<?> mCreditorAccount = null; // 債権者の会計
    private BorrowableAccount<?> mDebtorAccount; // 債務者の会計


    public Loan(BorrowableAccount<?> debtorAccount, int amount, Period period) {
        mDebtorAccount = debtorAccount;
        mAmount = amount;
        mPeriod = period;
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
     * 締結済かどうか
     * @return
     */
    public boolean isConcluded() {
        return Objects.nonNull(mCreditorAccount);
    }

    public boolean isOverDeadLine() {
        if (!isConcluded()) {
            throw new IllegalStateException("まだ契約が成立していません。");
        }
       return Market.INSTANCE.nowDate().isAfter(mDeadLine);
    }

    public boolean isPayOff() {
        return mAmount > 0;
    }

    /**
     * 債務が受け入れられ、債権債務関係が発生する
     */
    public Loan accepted(LendableAccount<?> creditorAccount) {
        if (isConcluded()) {
            throw new IllegalStateException("債権債務関係はすでに発生しています");
        }
        mCreditorAccount = creditorAccount;
        mAccrualDate = Market.INSTANCE.nowDate();
        mDeadLine = mAccrualDate.plus(mPeriod);
        mDebtorAccount.borrow(amount());
        creditorAccount.lend(amount());
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
