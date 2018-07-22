package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class GovernmentBooks extends AbstractDoubleEntryBooks<GovernmentAccountTitle>
        implements AccountOpenableBooks<GovernmentAccountTitle> {

    /**
     *
     * @return
     * @since 1.0
     */
    public static GovernmentBooks newInstance() {
        return new GovernmentBooks();
    }

    /**
     * @since 1.0
     */
    private GovernmentBooks() {
        super(GovernmentAccountTitle.class);
    }

    /**
     * @since 1.0
     */
    @Override
    public GovernmentBooks saveMoney(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.CASH, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public GovernmentBooks downMoney(int amount) {
        addLeft(GovernmentAccountTitle.CASH, amount);
        addRight(GovernmentAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public GovernmentBooks issueBonds(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public GovernmentBooks redeemBonds(int amount) {
        addLeft(GovernmentAccountTitle.GOVERNMENT_BOND, amount);
        addRight(GovernmentAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public GovernmentBooks collectIncomeTaxes(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.INCOME_TAX, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public GovernmentBooks collectConsumptionTax(int amount) {
        addLeft(GovernmentAccountTitle.DEPOSIT, amount);
        addRight(GovernmentAccountTitle.CONSUMPTION_TAX, amount);
        return this;
    }

}
