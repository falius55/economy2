package jp.gr.java_conf.falius.economy2.book;

import java.util.HashMap;
import java.util.Map;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class CentralBankBooks extends AbstractDoubleEntryBooks<CentralBankAccountTitle>
        implements BankBooks<CentralBankAccountTitle> {
    private final Map<AccountOpenable, Integer> mDeposits;

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
        super(CentralBankAccountTitle.class);
        mDeposits = new HashMap<>();
    }

    /**
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankAccountTitle> createAccount(AccountOpenable accountOpenable) {
        mDeposits.put(accountOpenable, 0);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBankBooks acceptGovernmentBond(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_BOND, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBankBooks redeemedGovernmentBond(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * 民間銀行からの預け入れ
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankAccountTitle> keep(int amount) {
        addLeft(CentralBankAccountTitle.BANK_NOTE, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 民間銀行への払い出し
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankAccountTitle> paidOut(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSIT, amount);
        addRight(CentralBankAccountTitle.BANK_NOTE, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankAccountTitle> buyGorvementBond(int amount) {
        return operateBuying(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public BankBooks<CentralBankAccountTitle> sellGovernmentBond(int amount) {
        return operateSelling(amount);
    }

    /**
     * 政府からの預け入れ
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks keepByNation(int amount) {
        addLeft(CentralBankAccountTitle.BANK_NOTE, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    /**
     * 政府への払い出し
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks paidOutByNation(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankAccountTitle.BANK_NOTE, amount);
        return this;
    }

    /**
     * 買いオペする
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks operateBuying(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_BOND, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 売りオペする
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks operateSelling(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSIT, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks transferToPrivateBankFromNation(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public CentralBankBooks transferToNationFromPrivateBank(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSIT, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    @Override
    public CentralBankBooks paySalary(int amount) {
        int tax = Taxes.computeIncomeTaxFromManthly(amount);
        addLeft(CentralBankAccountTitle.SALARIES_EXPENSE, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount - tax);
        addRight(CentralBankAccountTitle.DEPOSITS_RECEIVED, tax);
        return this;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    @Override
    public EmployableBooks<CentralBankAccountTitle> payIncomeTax(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSITS_RECEIVED, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }
}
