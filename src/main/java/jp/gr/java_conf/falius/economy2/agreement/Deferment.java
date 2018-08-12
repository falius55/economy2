package jp.gr.java_conf.falius.economy2.agreement;

import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

/**
 * 売掛金、買掛金でのやりとりを管理するクラス
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class Deferment {
    private final PrivateBusiness mReceiver;
    private final int mAmount;

    /**
     *
     * @param receiver 売掛金を計上した企業
     * @param amount 売掛金の金額
     * @since 1.0
     */
    public Deferment(PrivateBusiness receiver, int amount) {
        mReceiver = receiver;
        mAmount = amount;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int amount() {
        return mAmount;
    }

    /**
     * 売掛金、買掛金を精算します。
     * @param payer 買掛金を計上している企業
     * @since 1.0
     */
    public void settle(PrivateBusiness payer) {
        payer.books().settlePayable(mAmount);
        mReceiver.books().settleReceivable(mAmount);
        PrivateAccount payerAccount = payer.mainBank().account(payer);
        PrivateAccount receiveAccount = mReceiver.mainBank().account(mReceiver);
        payerAccount.transfer(receiveAccount, mAmount);
    }

}
