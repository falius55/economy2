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

    private BankAccount<?> mAcceptorAccount = null; // 債権者の会計
    private GovernmentAccount mIssuerAccount; // 債務者の会計

    public Bond(GovernmentAccount debtorAccount, int amount, Period period) {
        mIssuerAccount = debtorAccount;
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

    public boolean isConcluded() {
        return Objects.nonNull(mAcceptorAccount);
    }

    public boolean isOverDeadLine() {
       return Market.INSTANCE.nowDate().isAfter(mDeadLine);
    }

    public boolean isPayOff() {
        return mAmount <= 0;
    }

    /**
     * 債務が受け入れられ、債権債務関係が発生する
     */
    public Bond accepted(BankAccount<?> acceptorAccount) {
        if (isConcluded()) {
            throw new IllegalStateException("債権債務関係はすでに発生しています");
        }
        mAcceptorAccount = acceptorAccount;
        mAccrualDate = Market.INSTANCE.nowDate();
        mDeadLine = mAccrualDate.plus(mPeriod);
        mIssuerAccount.issueBonds(amount());
        acceptorAccount.acceptGovernmentBond(amount());
        return this;
    }

    /**
     * 債権を譲渡する
     * @param acceptorAccount 新たな債権者の会計
     */
    public Bond transfer(BankAccount<?> acceptorAccount) {
        if (!isConcluded()) {
            throw new IllegalStateException();
        }
        mAcceptorAccount = acceptorAccount;
        return this;
    }

    /**
     * 借金を減らす
     * @return 借金が完済されればtrue
     */
    public boolean repay(int amount) {
        if (!isConcluded()) {
            throw new IllegalStateException();
        }
        amount = Math.min(amount, mAmount);
        mIssuerAccount.repay(amount);
        mAcceptorAccount.repaid(amount);
        return mAmount == 0;
    }
}
