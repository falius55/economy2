package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.agreement.PaymentByInstallments;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentTitle;
import jp.gr.java_conf.falius.economy2.market.Market;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class GovernmentBooks extends AbstractDoubleEntryBooks<GovernmentTitle>
        implements AccountOpenableBooks<GovernmentTitle>, InstallmentPayableBooks<GovernmentTitle> {
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

    @Override
    public InstallmentPayableBooks<GovernmentTitle> payInstallment(int amount) {
        addLeft(GovernmentTitle.FIXEDASSET_SUSPENSE_ACCOUNT, amount);
        addRight(GovernmentTitle.DEPOSIT, amount);
        return this;
    }

    @Override
    public InstallmentPayableBooks<GovernmentTitle> redeem(PaymentByInstallments<?> installments) {
        int amount = installments.allAmount();
        switch (installments.product()) {
        case BUILDINGS:
            addLeft(GovernmentTitle.BUILDINGS, amount);
            break;
        case SYSTEM:
            addLeft(GovernmentTitle.SYSTEM, amount);
            break;
        default:
            throw new IllegalArgumentException();
        }

        addRight(GovernmentTitle.FIXEDASSET_SUSPENSE_ACCOUNT, amount);
        super.addFixedAsset(Market.INSTANCE.nowDate(), amount, installments.product().serviceLife());
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

    @Override
    protected void depreciationByIndirect(int amount) {
        addLeft(GovernmentTitle.DEPRECIATION, amount);
        addRight(GovernmentTitle.ACCUMULATED_DEPRECIATION, amount);
    }

    @Override
    protected void depreciationByDirect(int amount) {
        addLeft(GovernmentTitle.DEPRECIATION, amount);
        addRight(GovernmentTitle.TANGIBLE_ASSETS, amount);
    }

}
