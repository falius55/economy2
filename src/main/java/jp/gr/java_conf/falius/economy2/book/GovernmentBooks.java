package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;

public class GovernmentBooks extends AbstractDoubleEntryBooks<GovernmentAccountTitle>
        implements AccountOpenableBooks<GovernmentAccountTitle> {

    public static GovernmentBooks newInstance() {
        return new GovernmentBooks();
    }

    private GovernmentBooks() {
        super(GovernmentAccountTitle.class);
    }

    @Override
    public GovernmentBooks saveMoney(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.CASH, amount);
        return this;
    }

    @Override
    public GovernmentBooks downMoney(int amount) {
        addLeft(GovernmentAccountTitle.CASH, amount);
        addRight(GovernmentAccountTitle.DEPOSIT, amount);
        return this;
    }

    public GovernmentBooks issueBonds(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    public GovernmentBooks redeemBonds(int amount) {
        addLeft(GovernmentAccountTitle.GOVERNMENT_BOND, amount);
        addRight(GovernmentAccountTitle.DEPOSIT, amount);
        return this;
    }

    public GovernmentBooks collectIncomeTaxes(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.INCOME_TAX, amount);
        return this;
    }

    public GovernmentBooks collectConsumptionTax(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.CONSUMPTION_TAX, amount);
        return this;
    }

}
