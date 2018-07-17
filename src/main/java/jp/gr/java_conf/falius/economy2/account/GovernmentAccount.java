package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;

public class GovernmentAccount extends AbstractDoubleEntryAccount<GovernmentAccountTitle> {

    public static GovernmentAccount newInstance() {
        return new GovernmentAccount();
    }

    private GovernmentAccount() {
        super(GovernmentAccountTitle.class);
    }

    @Override
    public Account<GovernmentAccountTitle> saveMoney(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<GovernmentAccountTitle> downMoney(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<GovernmentAccountTitle> lend(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<GovernmentAccountTitle> borrow(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<GovernmentAccountTitle> repay(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<GovernmentAccountTitle> repaid(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    public GovernmentAccount issueBonds(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    public GovernmentAccount redeemBonds(int amount) {
        addLeft(GovernmentAccountTitle.GOVERNMENT_BOND, amount);
        addRight(GovernmentAccountTitle.DEPOSIT, amount);
        return this;
    }

}
