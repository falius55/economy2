package jp.gr.java_conf.falius.economy2.loan;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import jp.gr.java_conf.falius.economy2.account.BankAccount;
import jp.gr.java_conf.falius.economy2.account.GovernmentAccount;
import jp.gr.java_conf.falius.economy2.market.Market;

public class Bond {
    private final int mAmount; // 金額
    private final Period mPeriod; // 返済期間

    private LocalDate mAccrualDate; // 債権債務発生日
    private LocalDate mDeadLine; // 期限
    private boolean mIsPayOff = false;

    private BankAccount<?> mUnderWriterAccount = null; // 債権者の会計
    private GovernmentAccount mIssuerAccount; // 債務者の会計

    public Bond(GovernmentAccount issuerAccount, int amount, Period period) {
        mIssuerAccount = issuerAccount;
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
        return Objects.nonNull(mUnderWriterAccount);
    }

    public boolean isOverDeadLine() {
       return Market.INSTANCE.nowDate().isAfter(mDeadLine);
    }

    public boolean isPayOff() {
        return mIsPayOff;
    }

    /**
     * 債務が受け入れられ、債権債務関係が発生する
     */
    public Bond accepted(BankAccount<?> underwriterAccount) {
        if (isConcluded()) {
            throw new IllegalStateException("債権債務関係はすでに発生しています");
        }
        mUnderWriterAccount = underwriterAccount;
        mAccrualDate = Market.INSTANCE.nowDate();
        mDeadLine = mAccrualDate.plus(mPeriod);
        mIssuerAccount.issueBonds(amount());
        underwriterAccount.acceptGovernmentBond(amount());
        return this;
    }

    /**
     * 債権を譲渡する
     * @param underwriterAccount 新たな債権者の会計
     */
    public Bond transfer(BankAccount<?> underwriterAccount) {
        if (!isConcluded()) {
            throw new IllegalStateException();
        }
        mUnderWriterAccount = underwriterAccount;
        return this;
    }

    /**
     * 借金を減らす
     * @return 償還されればtrue(償還済も)
     */
    public boolean redeemed(int amount) {
        if (!isConcluded()) {
            throw new IllegalStateException();
        }
        if (isPayOff()) {
            return true;
        }
        if (amount < mAmount) {
            return false;
        }
        mIssuerAccount.redeemBonds(amount);
        mUnderWriterAccount.redeemedGovernmentBond(amount);
        mIsPayOff = true;
        return true;
    }
}
