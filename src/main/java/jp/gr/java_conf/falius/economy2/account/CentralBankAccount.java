package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;

public class CentralBankAccount extends AbstractDoubleEntryAccount<CentralBankAccountTitle>
        implements BankAccount<CentralBankAccountTitle> {

    public static CentralBankAccount newInstance() {
        return new CentralBankAccount();
    }

    public CentralBankAccount() {
        super(CentralBankAccountTitle.class);
    }

    @Override
    protected CentralBankAccountTitle[] items() {
        return CentralBankAccountTitle.values();
    }

    @Override
    public CentralBankAccountTitle defaultItem() {
        return CentralBankAccountTitle.CASH;
    }

    @Override
    public Account<CentralBankAccountTitle> saveMoney(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Account<CentralBankAccountTitle> downMoney(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Account<CentralBankAccountTitle> borrow(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Account<CentralBankAccountTitle> repay(int amount) {
        throw new UnsupportedOperationException();
    }

    /**
     * 民間銀行への貸付
     */
    @Override
    public Account<CentralBankAccountTitle> lend(int amount) {
        addLeft(CentralBankAccountTitle.LOANS_RECEIVABLE, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 民間銀行からの返済
     */
    @Override
    public Account<CentralBankAccountTitle> repaid(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSIT, amount);
        addRight(CentralBankAccountTitle.LOANS_RECEIVABLE, amount);
        return this;
    }

    @Override
    public Account<CentralBankAccountTitle> payTax(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
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
    public BankAccount<CentralBankAccountTitle> transfer(int amount) {
        addLeft(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        addRight(CentralBankAccountTitle.DEPOSIT, amount);
        return null;
    }

    /**
     * 民間預金からの政府への支払(税金)処理
     */
    @Override
    public BankAccount<CentralBankAccountTitle> transfered(int amount) {
        addLeft(CentralBankAccountTitle.DEPOSIT, amount);
        addRight(CentralBankAccountTitle.GOVERNMENT_DEPOSIT, amount);
        return this;
    }
}
