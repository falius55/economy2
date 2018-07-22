package jp.gr.java_conf.falius.economy2.loan;

import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

public class Deferment {
    private final PrivateBusiness mReceiver;
    private final int mAmount;

    public Deferment(PrivateBusiness receiver, int amount) {
        mReceiver = receiver;
        mAmount = amount;
    }

    public int amount() {
        return mAmount;
    }

    public void settle(PrivateBusiness payer) {
        payer.books().settlePayable(mAmount);
        mReceiver.books().settleReceivable(mAmount);
        PrivateAccount payerAccount = payer.mainBank().account(payer);
        PrivateAccount receiveAccount = mReceiver.mainBank().account(mReceiver);
        payerAccount.transfer(receiveAccount, mAmount);
    }

}
