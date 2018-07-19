package jp.gr.java_conf.falius.economy2.player.gorv;

import jp.gr.java_conf.falius.economy2.account.GovernmentAccount;
import jp.gr.java_conf.falius.economy2.enumpack.Product;

/**
 * 地方自治体
 * @author "ymiyauchi"
 *
 */
public class Municipality implements Government {
    private final GovernmentAccount mAccount = GovernmentAccount.newInstance();

    @Override
    public GovernmentAccount accountBook() {
        return mAccount;
    }

    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public Government issueBonds(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Government redeemBonds(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Government collectTaxes() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Government order(Product product) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

}
