package jp.gr.java_conf.falius.economy2.player.gorv;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.GovernmentAccount;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.player.PrivateEntity;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

public class Nation implements Government {
    public static final Nation INSTANCE;

    private final GovernmentAccount mAccount = GovernmentAccount.newInstance();

    static {
        INSTANCE = new Nation();
    }

    private Nation() {}

    @Override
    public Account<? extends Enum<?>> accountBook() {
        return mAccount;
    }

    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public Government issueBonds(int amount) {
        CentralBank.INSTANCE.acceptGovernmentBond(amount);
        mAccount.issueBonds(amount);
        return this;
    }

    @Override
    public Government redeemBonds(int amount) {
        CentralBank.INSTANCE.redeemedGovernmentBond(amount);
        mAccount.redeemBonds(amount);
        return this;
    }

    @Override
    public Government collectTaxes(PrivateEntity entity) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Government order(Product product) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

}
