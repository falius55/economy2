package jp.gr.java_conf.falius.economy2.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;

public class PrivateBankBooks extends AbstractDoubleEntryBooks<PrivateBankAccountTitle>
        implements BankBooks<PrivateBankAccountTitle>, PrivateBooks<PrivateBankAccountTitle>,
        LendableBooks<PrivateBankAccountTitle>, AccountOpenableBooks<PrivateBankAccountTitle> {
    private final Map<AccountOpenable, Integer> mDeposits;

    public static PrivateBankBooks newInstance() {
        return new PrivateBankBooks();
    }

    private PrivateBankBooks() {
        super(PrivateBankAccountTitle.class);
        mDeposits = new HashMap<>();
    }

    @Override
    public BankBooks<PrivateBankAccountTitle> createAccount(AccountOpenable accountOpenable) {
        mDeposits.put(accountOpenable, 0);
        return this;
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
    private PrivateBankBooks buyFixedAsset(LocalDate date, int amount, int serviceLife) {
        addFixedAsset(date, amount, serviceLife);

        addLeft(PrivateBankAccountTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 間接法で減価償却する
     * @param date 減価償却日。この日が減価償却日である固定資産が減価償却される
     */
    private PrivateBankBooks depreciationByIndirect(LocalDate date) {
        int amount = recordFixedAssets(date);
        addLeft(PrivateBankAccountTitle.DEPRECIATION, amount);
        addRight(PrivateBankAccountTitle.ACCUMULATED_DEPRECIATION, amount);
        return this;
    }

    /**
     * 直接法で減価償却する
     */
    private PrivateBankBooks depreciationByDirect(LocalDate date) {
        int amount = recordFixedAssets(date);
        addLeft(PrivateBankAccountTitle.DEPRECIATION, amount);
        addRight(PrivateBankAccountTitle.TANGIBLE_ASSETS, amount);
        return this;
    }

    /**
     * 土地の購入
     */
    private PrivateBankBooks buyLand(int amount) {
        addLeft(PrivateBankAccountTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * お金を銀行に預けた時の処理を行う
     */
    @Override
    public PrivateBankBooks saveMoney(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.CASH, amount);
        return this;
    }

    /**
     * お金を下ろした時の処理を行う
     */
    @Override
    public PrivateBankBooks downMoney(int amount) {
        addLeft(PrivateBankAccountTitle.CASH, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 貸金処理を行う
     */
    @Override
    public PrivateBankBooks lend(int amount) {
        addLeft(PrivateBankAccountTitle.LOANS_RECEIVABLE, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    @Override
    public PrivateBankBooks acceptGovernmentBond(int amount) {
        addLeft(PrivateBankAccountTitle.GOVERNMENT_BOND, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    @Override
    public PrivateBankBooks redeemedGovernmentBond(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * 返済を受けた時の処理を行う
     */
    @Override
    public PrivateBankBooks repaid(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.LOANS_RECEIVABLE, amount);
        return this;
    }

    /**
     * お金を預かる
     */
    public PrivateBankBooks keep(int amount) {
        addLeft(PrivateBankAccountTitle.CASH, amount);
        addRight(PrivateBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 預金返済処理
     */
    @Override
    public PrivateBankBooks paidOut(int amount) {
        addLeft(PrivateBankAccountTitle.DEPOSIT, amount);
        addRight(PrivateBankAccountTitle.CASH, amount);
        return this;
    }

    /**
     * 振り込みます。
     * 帳簿上は、預金(負債)と日銀当座預金(資産)がともに減じます。
     */
    public PrivateBankBooks transfer(int amount) {
        addLeft(PrivateBankAccountTitle.DEPOSIT, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 振り込みを受ける
     * 帳簿上は、預金(負債)と日銀当座預金(資産)がともに増えます。
     * @param amount
     */
    public PrivateBankBooks transfered(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.DEPOSIT, amount);
        return this;
    }

    @Override
    public BankBooks<PrivateBankAccountTitle> buyGorvementBond(int amount) {
        addLeft(PrivateBankAccountTitle.GOVERNMENT_BOND, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    @Override
    public BankBooks<PrivateBankAccountTitle> sellGovernmentBond(int amount) {
        addLeft(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankAccountTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    @Override
    public PrivateBankBooks paySalary(int amount) {
        int tax = Taxes.computeIncomeTaxFromManthly(amount);
        addLeft(PrivateBankAccountTitle.SALARIES_EXPENSE, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount - tax);
        addRight(PrivateBankAccountTitle.DEPOSITS_RECEIVED, tax);
        return this;
    }

    @Override
    public PrivateBankBooks payIncomeTax(int amount) {
        addLeft(PrivateBankAccountTitle.DEPOSITS_RECEIVED, amount);
        addRight(PrivateBankAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }
}
