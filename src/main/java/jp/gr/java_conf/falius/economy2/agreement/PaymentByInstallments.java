package jp.gr.java_conf.falius.economy2.agreement;

import java.time.LocalDate;
import java.time.Period;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.book.InstallmentPayableBooks;
import jp.gr.java_conf.falius.economy2.book.InstallmentReceivableBooks;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.Title;
import jp.gr.java_conf.falius.economy2.market.Market;

/**
 * 分割払いを管理するクラス
 * @author "ymiyauchi"
 * @since 1.0
 * @param <T> 受け取り科目の種類
 */
public class PaymentByInstallments<T extends Enum<T> & Title> {
    private final Product mProduct;
    private final int mAmount;
    private final int mInstallment; // 分割払いの一回分
    private final int mCount;
    private final Period mPeriod;
    private final InstallmentReceivableBooks<T> mReceiverBooks;
    private int mRemain;
    private LocalDate mLastPayment;

    /**
     *
     * @param amount
     * @param installment
     * @param receiverBooks
     * @return
     * @since 1.0
     */
    public static <T extends Enum<T> & Title> PaymentByInstallments<T> newInstanceByInstallment(
            Product product, int amount, int installment, InstallmentReceivableBooks<T> receiverBooks) {
        return newInstanceByInstallment(product, amount, installment, Period.ofMonths(1), receiverBooks);
    }

    /**
     *
     * @param amount
     * @param installment 一回分の支払額
     * @param period
     * @param receiverBooks
     * @return
     * @since 1.0
     */
    public static <T extends Enum<T> & Title> PaymentByInstallments<T> newInstanceByInstallment(
            Product product, int amount, int installment, Period period, InstallmentReceivableBooks<T> receiverBooks) {
        int count = amount % installment == 0 ? amount / installment : amount / installment + 1;
        return new PaymentByInstallments<>(product, amount, installment, count, period, receiverBooks);
    }

    /**
     *
     * @param amount
     * @param count
     * @param receiverBooks
     * @return
     * @since 1.0
     */
    public static <T extends Enum<T> & Title> PaymentByInstallments<T> newInstanceByCount(
            Product product, int amount, int count, InstallmentReceivableBooks<T> receiverBooks) {
        return newInstanceByCount(product, amount, count, Period.ofMonths(1), receiverBooks);
    }


    /**
     *
     * @param amount
     * @param count 分割の回数
     * @param period 分割の間の期間
     * @param receiverBooks 受け取りの帳簿
     * @return
     * @since 1.0
     */
    public static <T extends Enum<T> & Title> PaymentByInstallments<T> newInstanceByCount(
            Product product, int amount, int count, Period period, InstallmentReceivableBooks<T> receiverBooks) {
        int installment = amount / count;
        return new PaymentByInstallments<>(product, amount, installment, count, period, receiverBooks);
    }

    /**
     *
     * @param product
     * @param amount
     * @param installment
     * @param count
     * @param period
     * @param receiverBooks
     * @since 1.0
     */
    private PaymentByInstallments(Product product, int amount, int installment, int count, Period period,
            InstallmentReceivableBooks<T> receiverBooks) {
        mProduct = product;
        mAmount = amount;
        mInstallment = installment;
        mCount = count;
        mPeriod = period;
        mReceiverBooks = receiverBooks;
        mRemain = amount;
        mLastPayment = Market.INSTANCE.nowDate();
    }

    /**
     * @return
     * @since 1.0
     */
    public int allAmount() {
        return mAmount;
    }

    /**
     * @return
     * @since 1.0
     */
    public int installment() {
        return mInstallment;
    }

    /**
     * @return
     * @since 1.0
     */
    public int remain() {
        return mRemain;
    }

    /**
     * @return
     * @since 1.0
     */
    public int count() {
        return mCount;
    }

    /**
     * @return
     * @since 1.0
     */
    public Product product() {
        return mProduct;
    }

    /**
     * @return
     * @since 1.0
     */
    public boolean isComplete() {
        return mRemain <= 0;
    }

    /**
     * @param payerBooks
     * @since 1.0
     */
    public <U extends Enum<U> & Title> void update(InstallmentPayableBooks<U> payerBooks) {
        mLastPayment = settle(payerBooks);
    }

    /**
     * @param payerBooks
     * @return
     * @since 1.0
     */
    private <U extends Enum<U> & Title> LocalDate settle(InstallmentPayableBooks<U> payerBooks) {
        LocalDate today = Market.INSTANCE.nowDate();
        LocalDate ret = mLastPayment;
        for (LocalDate next = ret.plus(mPeriod); next.isBefore(today); ret = next, next = ret.plus(mPeriod)) {
            if (mRemain <= 0) {
                return ret;
            }
            settleOnce(payerBooks);
        }
        return ret;
    }

    /**
     * @param payerBooks
     * @return
     * @since 1.0
     */
    private <U extends Enum<U> & Title> void settleOnce(InstallmentPayableBooks<U> payerBooks) {
            int installment = mRemain >= mInstallment ? mInstallment : mRemain;
            mReceiverBooks.receiveInstallment(installment);
            payerBooks.payInstallment(installment);
            Account receiverAccount = mReceiverBooks.mainAccount();
            Account payerAccount = payerBooks.mainAccount();
            payerAccount.transfer(receiverAccount, installment);
            mRemain -= installment;
    }
}
