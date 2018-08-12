package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.account.Transferable;
import jp.gr.java_conf.falius.economy2.enumpack.CentralBankTitle;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.util.Taxes;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class CentralBankBooks extends AbstractDoubleEntryBooks<CentralBankTitle>
        implements BankBooks<CentralBankTitle> {

    /**
     *
     * @return
     * @since 1.0
     */
    public static CentralBankBooks newInstance() {
        return new CentralBankBooks();
    }

    /**
     * @since 1.0
     */
    public CentralBankBooks() {
        super(CentralBankTitle.class, false);
    }

    @Override
    public Transferable transferable() {
        return CentralBank.INSTANCE;
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBankBooks acceptGovernmentBond(int amount) {
        addLeft(CentralBankTitle.GOVERNMENT_BOND, amount);
        addRight(CentralBankTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBankBooks redeemedGovernmentBond(int amount) {
        addLeft(CentralBankTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * 民間銀行からの預け入れ
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankTitle> keep(int amount) {
        addLeft(CentralBankTitle.BANK_NOTE, amount);
        addRight(CentralBankTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 民間銀行への払い出し
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankTitle> paidOut(int amount) {
        addLeft(CentralBankTitle.DEPOSIT, amount);
        addRight(CentralBankTitle.BANK_NOTE, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankTitle> buyGorvementBond(int amount) {
        return operateBuying(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankTitle> sellGovernmentBond(int amount) {
        return operateSelling(amount);
    }

    /**
     * 政府からの預け入れ
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks keepByNation(int amount) {
        addLeft(CentralBankTitle.BANK_NOTE, amount);
        addRight(CentralBankTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    /**
     * 政府への払い出し
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks paidOutByNation(int amount) {
        addLeft(CentralBankTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankTitle.BANK_NOTE, amount);
        return this;
    }

    /**
     * 買いオペする
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks operateBuying(int amount) {
        addLeft(CentralBankTitle.GOVERNMENT_BOND, amount);
        addRight(CentralBankTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 売りオペする
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks operateSelling(int amount) {
        addLeft(CentralBankTitle.DEPOSIT, amount);
        addRight(CentralBankTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks transferToPrivateBankFromNation(int amount) {
        addLeft(CentralBankTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankTitle.DEPOSIT, amount);
        return this;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks transferToNationFromPrivateBank(int amount) {
        addLeft(CentralBankTitle.DEPOSIT, amount);
        addRight(CentralBankTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    @Override
    public int paySalary(int amount) {
        int tax = Taxes.computeIncomeTaxFromManthly(amount);
        int takeHome = amount - tax;
        addLeft(CentralBankTitle.SALARIES_EXPENSE, amount);
        addRight(CentralBankTitle.DEPOSIT, takeHome);
        addRight(CentralBankTitle.DEPOSITS_RECEIVED, tax);
        return takeHome;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    @Override
    public EmployableBooks<CentralBankTitle> payIncomeTax(int amount) {
        addLeft(CentralBankTitle.DEPOSITS_RECEIVED, amount);
        addRight(CentralBankTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    @Override
    protected void depreciationByIndirect(int amount) {
        addLeft(CentralBankTitle.DEPRECIATION, amount);
        addRight(CentralBankTitle.ACCUMULATED_DEPRECIATION, amount);
    }

    @Override
    protected void depreciationByDirect(int amount) {
        addLeft(CentralBankTitle.DEPRECIATION, amount);
        addRight(CentralBankTitle.TANGIBLE_ASSETS, amount);
    }
}
