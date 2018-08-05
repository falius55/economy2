package jp.gr.java_conf.falius.economy2.agreement;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import jp.gr.java_conf.falius.economy2.book.BankBooks;
import jp.gr.java_conf.falius.economy2.book.CentralBankBooks;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.book.PrivateBankBooks;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentTitle;
import jp.gr.java_conf.falius.economy2.market.Market;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 */
public class Bond {
    private final int mAmount; // 金額
    private final Period mPeriod; // 返済期間

    private LocalDate mAccrualDate; // 債権債務発生日
    private LocalDate mDeadLine; // 期限
    private boolean mIsPayOff = false;

    private final GovernmentBooks mIssuerBooks; // 債務者の会計
    private BankBooks<?> mUnderWriterBooks = null; // 債権者の会計

    public Bond(GovernmentBooks issuerBook, int amount, Period period) {
        mIssuerBooks = issuerBook;
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
        return Objects.nonNull(mUnderWriterBooks);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean isOverDeadLine() {
       return Market.INSTANCE.nowDate().isAfter(mDeadLine);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean isPayOff() {
        return mIsPayOff;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean ofCentralBank() {
        if (!isConcluded()) {
            return false;
        }
        return mUnderWriterBooks instanceof CentralBankBooks;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean ofPrivateBank() {
        if (!isConcluded()) {
            return false;
        }
        return mUnderWriterBooks instanceof PrivateBankBooks;
    }

    /**
     * 債務が受け入れられ、債権債務関係が発生する
     * @since 1.0
     */
    public Bond accepted(BankBooks<?> underwriterBooks) {
        if (isConcluded()) {
            throw new IllegalStateException("債権債務関係はすでに発生しています");
        }
        mUnderWriterBooks = underwriterBooks;
        mAccrualDate = Market.INSTANCE.nowDate();
        mDeadLine = mAccrualDate.plus(mPeriod);
        mIssuerBooks.issueBonds(amount());
        underwriterBooks.acceptGovernmentBond(amount());
        underwriterBooks.transferable().transfer(mIssuerBooks.mainAccount(), mAmount);
        return this;
    }

    /**
     * 債権を譲渡する
     * @param transferee 新たな債権者の会計
     * @since 1.0
     */
    public Bond sellTo(BankBooks<?> transferee) {
        if (!isConcluded()) {
            throw new IllegalStateException();
        }
        mUnderWriterBooks.sellGovernmentBond(amount());
        transferee.buyGorvementBond(amount());
        transferee.transferable().transfer(mUnderWriterBooks.transferable(), mAmount);
        mUnderWriterBooks = transferee;
        return this;
    }

    /**
     * 償還する
     * @return 償還されればtrue(償還済も)
     * @since 1.0
     */
    public boolean redeemed() {
        if (!isConcluded()) {
            throw new IllegalStateException();
        }
        if (isPayOff()) {
            return true;
        }
        if (mIssuerBooks.get(GovernmentTitle.DEPOSIT) < amount()) {
            return false;
        }
        mIssuerBooks.redeemBonds(amount());
        mUnderWriterBooks.redeemedGovernmentBond(amount());
        mIssuerBooks.mainAccount().transfer(mUnderWriterBooks.transferable(), mAmount);
        mIsPayOff = true;
        return true;
    }
}
