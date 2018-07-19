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
    public GovernmentAccount saveMoney(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.CASH, amount);
        return this;
    }

    @Override
    public GovernmentAccount downMoney(int amount) {
        addLeft(GovernmentAccountTitle.CASH, amount);
        addRight(GovernmentAccountTitle.DEPOSIT, amount);
        return this;
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

    public GovernmentAccount collectIncomeTaxes(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.INCOME_TAX, amount);
        return this;
    }

    public GovernmentAccount collectConsumptionTax(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.CONSUMPTION_TAX, amount);
        return this;
    }

}
