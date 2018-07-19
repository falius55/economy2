package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;

public class CentralBankAccount extends AbstractDoubleEntryAccount<CentralBankAccountTitle>
        implements BankAccount<CentralBankAccountTitle> {

    public static CentralBankAccount newInstance() {
        return new CentralBankAccount();
    }

    public CentralBankAccount() {
        super(CentralBankAccountTitle.class);
    }

    @Override
    public CentralBankAccount acceptGovernmentBond(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_BOND, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    @Override
    public CentralBankAccount redeemedGovernmentBond(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * 民間銀行からの預け入れ
     */
    @Override
    public BankAccount<CentralBankAccountTitle> keep(int amount) {
        addLeft(CentralBankAccountTitle.BANK_NOTE, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 民間銀行への払い出し
     */
    @Override
    public BankAccount<CentralBankAccountTitle> paidOut(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSIT, amount);
        addRight(CentralBankAccountTitle.BANK_NOTE, amount);
        return this;
    }

    @Override
    public BankAccount<CentralBankAccountTitle> buyGorvementBond(int amount) {
        return operateBuying(amount);
    }

    @Override
    public BankAccount<CentralBankAccountTitle> sellGovernmentBond(int amount) {
        return operateSelling(amount);
    }

    /**
     * 政府からの預け入れ
     * @param amount
     * @return
     */
    public CentralBankAccount keepByNation(int amount) {
        addLeft(CentralBankAccountTitle.BANK_NOTE, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    /**
     * 政府への払い出し
     * @param amount
     * @return
     */
    public CentralBankAccount paidOutByNation(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankAccountTitle.BANK_NOTE, amount);
        return this;
    }

    /**
     * 買いオペする
     * @param amount
     * @return
     */
    public CentralBankAccount operateBuying(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_BOND, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 売りオペする
     * @param amount
     * @return
     */
    public CentralBankAccount operateSelling(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSIT, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * 政府から民間預金への振り込み
     */
    @Override
    public CentralBankAccount transfer(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount);
        return null;
    }

    /**
     * 民間預金からの政府への支払(税金)処理
     */
    @Override
    public CentralBankAccount transfered(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSIT, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }

    @Override
    public CentralBankAccount paySalary(int amount) {
        int tax = Taxes.computeIncomeTaxFromManthly(amount);
        addLeft(CentralBankAccountTitle.SALARIES_EXPENSE, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount - tax);
        addRight(CentralBankAccountTitle.DEPOSITS_RECEIVED, tax);
        return this;
    }

    @Override
    public EmployableAccount<CentralBankAccountTitle> payIncomeTax(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSITS_RECEIVED, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }
}
