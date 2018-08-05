package jp.gr.java_conf.falius.economy2.agreement;

import java.time.LocalDate;
import java.time.Period;

import jp.gr.java_conf.falius.economy2.book.InstallmentPayableBooks;
import jp.gr.java_conf.falius.economy2.enumpack.Title;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.InstallmentReceivable;

/**
 * 分割払いを管理するクラス
 * @author "ymiyauchi"
 * @since 1.0
 */
public class PaymentByInstallments<T extends Enum<T> & Title> {
    private final int mAmount;
    private final int mInstallment; // 分割払いの一回分
    private final int mCount;
    private final Period mPeriod;
    private final InstallmentReceivable mReceiver;
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
            int amount, int installment, InstallmentReceivable receiver) {
        return newInstanceByInstallment(amount, installment, Period.ofMonths(1), receiver);
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
            int amount, int installment, Period period, InstallmentReceivable receiver) {
        int count = amount % installment == 0 ? amount / installment : amount / installment + 1;
        return new PaymentByInstallments<>(amount, installment, count, period, receiver);
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
            int amount, int count, InstallmentReceivable receiver) {
        return newInstanceByCount(amount, count, Period.ofMonths(1), receiver);
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
            int amount, int count, Period period, InstallmentReceivable receiver) {
        int installment = amount / count;
        return new PaymentByInstallments<>(amount, installment, count, period, receiver);
    }

    private PaymentByInstallments(int amount, int installment, int count, Period period,
            InstallmentReceivable receiver) {
        mAmount = amount;
        mInstallment = installment;
        mCount = count;
        mPeriod = period;
        mReceiver = receiver;
        mRemain = amount;
        mLastPayment = Market.INSTANCE.nowDate();
    }

    public int allAmount() {
        return mAmount;
    }

    public int installment() {
        return mInstallment;
    }

    public int count() {
        return mCount;
    }

    public <U extends Enum<U> & Title> void update(InstallmentPayableBooks<U> payerBooks) {
        mLastPayment = settle(payerBooks);
    }

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

    private <U extends Enum<U> & Title> void settleOnce(InstallmentPayableBooks<U> payerBooks) {
            int installment = mRemain >= mInstallment ? mInstallment : mRemain;
            mReceiver.books().receiveInstallment(installment);
            payerBooks.payInstallment(installment);
            mRemain -= installment;
    }
}
