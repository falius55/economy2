package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.agreement.PaymentByInstallments;
import jp.gr.java_conf.falius.economy2.enumpack.Title;

/**
 * 分割払いで支払える帳簿のインターフェースです。
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface InstallmentPayableBooks<T extends Enum<T> & Title> extends AccountOpenableBooks<T> {

    /**
     *
     * @param amount
     * @return
     * @since1.0
     */
    public InstallmentPayableBooks<T> payInstallment(int amount);

    /**
     * 分割払いの対象製品を取得する。
     * @param product
     * @return
     * @since 1.0
     */
    public InstallmentPayableBooks<T> redeem(PaymentByInstallments<?> installments);
}
