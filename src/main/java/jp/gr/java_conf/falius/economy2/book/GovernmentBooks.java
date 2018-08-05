package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentTitle;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class GovernmentBooks extends AbstractDoubleEntryBooks<GovernmentTitle>
        implements AccountOpenableBooks<GovernmentTitle> {
    private final Account mAccount;

    /**
     *
     * @return
     * @since 1.0
     */
    public static GovernmentBooks newInstance(Account mainAccount) {
        return new GovernmentBooks(mainAccount);
    }

    /**
     * @since 1.0
     */
    private GovernmentBooks(Account mainAccount) {
        super(GovernmentTitle.class);
        mAccount = mainAccount;
    }

    /**
     * @since 1.0
     */
    @Override
    public Account mainAccount() {
        return mAccount;
    }

    /**
     * @since 1.0
     */
    @Override
    public GovernmentBooks saveMoney(int amount) {
        addLeft(GovernmentTitle.DEPOSIT, amount);
        addRight(GovernmentTitle.CASH, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public GovernmentBooks downMoney(int amount) {
        addLeft(GovernmentTitle.CASH, amount);
        addRight(GovernmentTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public GovernmentBooks issueBonds(int amount) {
        addLeft(GovernmentTitle.DEPOSIT, amount);
        addRight(GovernmentTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public GovernmentBooks redeemBonds(int amount) {
        addLeft(GovernmentTitle.GOVERNMENT_BOND, amount);
        addRight(GovernmentTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public GovernmentBooks collectIncomeTaxes(int amount) {
        addLeft(GovernmentTitle.DEPOSIT, amount);
        addRight(GovernmentTitle.INCOME_TAX, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public GovernmentBooks collectConsumptionTax(int amount) {
        addLeft(GovernmentTitle.DEPOSIT, amount);
        addRight(GovernmentTitle.CONSUMPTION_TAX, amount);
        return this;
    }

}
