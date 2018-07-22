package jp.gr.java_conf.falius.economy2.loan;

import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class Deferment {
    private final PrivateBusiness mReceiver;
    private final int mAmount;

    /**
     *
     * @param receiver
     * @param amount
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
     *
     * @param payer
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
