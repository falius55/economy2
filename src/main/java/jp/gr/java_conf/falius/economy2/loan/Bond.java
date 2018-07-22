package jp.gr.java_conf.falius.economy2.loan;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import jp.gr.java_conf.falius.economy2.book.BankBooks;
import jp.gr.java_conf.falius.economy2.book.CentralBankBooks;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.book.PrivateBankBooks;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;
import jp.gr.java_conf.falius.economy2.market.Market;

public class Bond {
    private final int mAmount; // 金額
    private final Period mPeriod; // 返済期間

    private LocalDate mAccrualDate; // 債権債務発生日
    private LocalDate mDeadLine; // 期限
    private boolean mIsPayOff = false;

    private BankBooks<?> mUnderWriterBooks = null; // 債権者の会計
    private GovernmentBooks mIssuerBooks; // 債務者の会計

    public Bond(GovernmentBooks issuerBook, int amount, Period period) {
        mIssuerBooks = issuerBook;
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
        return Objects.nonNull(mUnderWriterBooks);
    }

    public boolean isOverDeadLine() {
       return Market.INSTANCE.nowDate().isAfter(mDeadLine);
    }

    public boolean isPayOff() {
        return mIsPayOff;
    }

    public boolean ofCentralBank() {
        if (!isConcluded()) {
            return false;
        }
        return mUnderWriterBooks instanceof CentralBankBooks;
    }

    public boolean ofPrivateBank() {
        if (!isConcluded()) {
            return false;
        }
        return mUnderWriterBooks instanceof PrivateBankBooks;
    }

    /**
     * 債務が受け入れられ、債権債務関係が発生する
     */
    public Bond accepted(BankBooks<?> underwriterBook) {
        if (isConcluded()) {
            throw new IllegalStateException("債権債務関係はすでに発生しています");
        }
        mUnderWriterBooks = underwriterBook;
        mAccrualDate = Market.INSTANCE.nowDate();
        mDeadLine = mAccrualDate.plus(mPeriod);
        mIssuerBooks.issueBonds(amount());
        underwriterBook.acceptGovernmentBond(amount());
        return this;
    }

    /**
     * 債権を譲渡する
     * @param transferee 新たな債権者の会計
     */
    public Bond sellTo(BankBooks<?> transferee) {
        if (!isConcluded()) {
            throw new IllegalStateException();
        }
        mUnderWriterBooks.sellGovernmentBond(amount());
        transferee.buyGorvementBond(amount());
        mUnderWriterBooks = transferee;
        return this;
    }

    /**
     * 償還する
     * @return 償還されればtrue(償還済も)
     */
    public boolean redeemed() {
        if (!isConcluded()) {
            throw new IllegalStateException();
        }
        if (isPayOff()) {
            return true;
        }
        if (mIssuerBooks.get(GovernmentAccountTitle.DEPOSIT) < amount()) {
            return false;
        }
        mIssuerBooks.redeemBonds(amount());
        mUnderWriterBooks.redeemedGovernmentBond(amount());
        mIsPayOff = true;
        return true;
    }
}
