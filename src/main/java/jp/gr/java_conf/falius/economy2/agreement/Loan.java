package jp.gr.java_conf.falius.economy2.agreement;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.Transferable;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.Borrowable;
import jp.gr.java_conf.falius.economy2.player.Lendable;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class Loan {
    private final int mAmount; // 金額
    private final Period mPeriod; // 返済期間

    private LocalDate mAccrualDate; // 債権債務発生日
    private LocalDate mDeadLine; // 期限

    private Lendable mCreditor = null; // 債権者
    private final Borrowable mDebtor; // 債務者


    /**
     *
     * @param debtor
     * @param amount
     * @param period
     * @since 1.0
     */
    public Loan(Borrowable debtor, int amount, Period period) {
        mDebtor = debtor;
        mAmount = amount;
        mPeriod = period;
    }

    /**
     * 残高
     * @since 1.0
     */
    public int amount() {
        return mAmount;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public LocalDate deadLine() {
        return mDeadLine;
    }

    /**
     * 締結済かどうか
     * @return
     * @since 1.0
     */
    public boolean isConcluded() {
        return Objects.nonNull(mCreditor);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean isOverDeadLine() {
        if (!isConcluded()) {
            throw new IllegalStateException("まだ契約が成立していません。");
        }
       return Market.INSTANCE.nowDate().isAfter(mDeadLine);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean isPayOff() {
        return mAmount > 0;
    }

    /**
     * 債務が受け入れられ、債権債務関係が発生する
     * @since 1.0
     */
    public Loan accepted(Lendable creditor) {
        if (isConcluded()) {
            throw new IllegalStateException("債権債務関係はすでに発生しています");
        }
        mCreditor = creditor;
        mAccrualDate = Market.INSTANCE.nowDate();
        mDeadLine = mAccrualDate.plus(mPeriod);
        mDebtor.books().borrow(amount());
        creditor.books().lend(amount());
        Account debtorAccount = mDebtor.books().mainAccount();
        Transferable creditorTransferable = creditor.books().transferable();
        creditorTransferable.transfer(debtorAccount, mAmount);
        return this;
    }

    /**
     * 借金を減らす
     * @return 借金が完済されればtrue
     * @since 1.0
     */
    public boolean repay(int amount) {
        amount = amount <= mAmount ? amount : mAmount;
        mDebtor.books().repay(amount);
        mCreditor.books().repaid(amount);
        Account debtorAccount = mDebtor.books().mainAccount();
        Transferable creditorTransferable = mCreditor.books().transferable();
        debtorAccount.transfer(creditorTransferable, amount);
        return mAmount == 0;
    }
}
