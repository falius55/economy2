package jp.gr.java_conf.falius.economy2.loan;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.Borrowable;
import jp.gr.java_conf.falius.economy2.player.Lendable;

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

    private Lendable mCreditor = null; // 債権者
    private final Borrowable mDebtor; // 債務者
    private Account mCreditorAccount = null;
    private final Account mDebtorAccount;


    public Loan(Borrowable debtor, Account account, int amount, Period period) {
        mDebtor = debtor;
        mDebtorAccount = account;
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
        return Objects.nonNull(mCreditor);
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
    public Loan accepted(Lendable creditor, Account account) {
        if (isConcluded()) {
            throw new IllegalStateException("債権債務関係はすでに発生しています");
        }
        mCreditor = creditor;
        mCreditorAccount = account;
        mAccrualDate = Market.INSTANCE.nowDate();
        mDeadLine = mAccrualDate.plus(mPeriod);
        mDebtor.books().borrow(amount());
        creditor.books().lend(amount());
        mCreditorAccount.transfer(mDebtorAccount, mAmount);
        return this;
    }

    /**
     * 借金を減らす
     * @return 借金が完済されればtrue
     */
    public boolean repay(int amount) {
        amount = amount <= mAmount ? amount : mAmount;
        mDebtor.books().repay(amount);
        mCreditor.books().repaid(amount);
        mDebtorAccount.transfer(mCreditorAccount, amount);
        return mAmount == 0;
    }
}
