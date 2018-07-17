package jp.gr.java_conf.falius.economy2.account;

import java.time.LocalDate;

import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;

public class PrivateBankAccount extends AbstractDoubleEntryAccount<PrivateBankAccountTitle>
        implements BankAccount<PrivateBankAccountTitle>, PrivateAccount<PrivateBankAccountTitle> {

    public static PrivateBankAccount newInstance() {
        return new PrivateBankAccount();
    }

    private PrivateBankAccount() {
        super(PrivateBankAccountTitle.class);
    }

    /*
     * 以下、ほぼ仕分け処理が続く
     */

    /**
     * 固定資産の購入
     * @param date 購入日
     * @param amount 金額
     * @param serviceLife 耐用年数
     */
    private PrivateBankAccount buyFixedAsset(LocalDate date, int amount, int serviceLife) {
        addFixedAsset(date, amount, serviceLife);

        addLeft(PrivateBankAccountTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 間接法で減価償却する
     * @param date 減価償却日。この日が減価償却日である固定資産が減価償却される
     */
    private PrivateBankAccount depreciationByIndirect(LocalDate date) {
        int amount = recordFixedAssets(date);
        addLeft(PrivateBankAccountTitle.DEPRECIATION, amount);
        addRight(PrivateBankAccountTitle.ACCUMULATED_DEPRECIATION, amount);
        return this;
    }

    /**
     * 直接法で減価償却する
     */
    private PrivateBankAccount depreciationByDirect(LocalDate date) {
        int amount = recordFixedAssets(date);
        addLeft(PrivateBankAccountTitle.DEPRECIATION, amount);
        addRight(PrivateBankAccountTitle.TANGIBLE_ASSETS, amount);
        return this;
    }

    /**
     * 土地の購入
     */
    private PrivateBankAccount buyLand(int amount) {
        addLeft(PrivateBankAccountTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * お金を銀行に預けた時の処理を行う
     */
    @Override
    public PrivateBankAccount saveMoney(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.CASH, amount);
        return this;
    }

    /**
     * お金を下ろした時の処理を行う
     */
    @Override
    public PrivateBankAccount downMoney(int amount) {
        addLeft(PrivateBankAccountTitle.CASH, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 貸金処理を行う
     */
    @Override
    public PrivateBankAccount lend(int amount) {
        addLeft(PrivateBankAccountTitle.LOANS_RECEIVABLE, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    @Override
    public PrivateBankAccount borrow(int amount) {
        return this;
    }

    @Override
    public PrivateBankAccount acceptGovernmentBond(int amount) {
        addLeft(PrivateBankAccountTitle.GOVERNMENT_BOND, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    @Override
    public PrivateBankAccount redeemedGovernmentBond(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * 預金返済処理を行う
     */
    @Override
    public PrivateBankAccount repay(int amount) {
        return this;
    }

    /**
     * 返済を受けた時の処理を行う
     */
    @Override
    public PrivateBankAccount repaid(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.LOANS_RECEIVABLE, amount);
        return this;
    }

    @Override
    public PrivateBankAccount payTax(int amount) {
        return this;
    }

    /**
     * お金を預かる
     */
    public PrivateBankAccount keep(int amount) {
        addLeft(PrivateBankAccountTitle.CASH, amount);
        addRight(PrivateBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 預金返済処理
     */
    @Override
    public PrivateBankAccount paidOut(int amount) {
        addLeft(PrivateBankAccountTitle.DEPOSIT, amount);
        addRight(PrivateBankAccountTitle.CASH, amount);
        return this;
    }

    /**
     * 民間預金への送金処理
     */
    @Override
    public PrivateBankAccount transfer(int amount) {
        addLeft(PrivateBankAccountTitle.DEPOSIT, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 振り込みを受ける
     * @param amount
     */
    @Override
    public PrivateBankAccount transfered(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    @Override
    public PrivateBankAccount paySalary(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }
}
