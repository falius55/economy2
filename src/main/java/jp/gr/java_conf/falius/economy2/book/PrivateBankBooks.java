package jp.gr.java_conf.falius.economy2.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class PrivateBankBooks extends AbstractDoubleEntryBooks<PrivateBankTitle>
        implements BankBooks<PrivateBankTitle>, PrivateBooks<PrivateBankTitle>,
        LendableBooks<PrivateBankTitle>, AccountOpenableBooks<PrivateBankTitle> {
    private final Map<AccountOpenable, Integer> mDeposits;

    /**
     *
     * @return
     * @since 1.0
     */
    public static PrivateBankBooks newInstance() {
        return new PrivateBankBooks();
    }

    /**
     * @since 1.0
     */
    private PrivateBankBooks() {
        super(PrivateBankTitle.class);
        mDeposits = new HashMap<>();
    }

    /**
     * @since 1.0
     */
    @Override
    public BankBooks<PrivateBankTitle> createAccount(AccountOpenable accountOpenable) {
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
     * @since 1.0
     */
    private PrivateBankBooks buyFixedAsset(LocalDate date, int amount, int serviceLife) {
        addFixedAsset(date, amount, serviceLife);

        addLeft(PrivateBankTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 間接法で減価償却する
     * @param date 減価償却日。この日が減価償却日である固定資産が減価償却される
     * @since 1.0
     */
    private PrivateBankBooks depreciationByIndirect(LocalDate date) {
        int amount = recordFixedAssets(date);
        addLeft(PrivateBankTitle.DEPRECIATION, amount);
        addRight(PrivateBankTitle.ACCUMULATED_DEPRECIATION, amount);
        return this;
    }

    /**
     * 直接法で減価償却する
     * @since 1.0
     */
    private PrivateBankBooks depreciationByDirect(LocalDate date) {
        int amount = recordFixedAssets(date);
        addLeft(PrivateBankTitle.DEPRECIATION, amount);
        addRight(PrivateBankTitle.TANGIBLE_ASSETS, amount);
        return this;
    }

    /**
     * 土地の購入
     * @since 1.0
     */
    private PrivateBankBooks buyLand(int amount) {
        addLeft(PrivateBankTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * お金を銀行に預けた時の処理を行う
     * @since 1.0
     */
    @Override
    public PrivateBankBooks saveMoney(int amount) {
        addLeft(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankTitle.CASH, amount);
        return this;
    }

    /**
     * お金を下ろした時の処理を行う
     * @since 1.0
     */
    @Override
    public PrivateBankBooks downMoney(int amount) {
        addLeft(PrivateBankTitle.CASH, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 貸金処理を行う
     * @since 1.0
     */
    @Override
    public PrivateBankBooks lend(int amount) {
        addLeft(PrivateBankTitle.LOANS_RECEIVABLE, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBankBooks acceptGovernmentBond(int amount) {
        addLeft(PrivateBankTitle.GOVERNMENT_BOND, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBankBooks redeemedGovernmentBond(int amount) {
        addLeft(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * 返済を受けた時の処理を行う
     * @since 1.0
     */
    @Override
    public PrivateBankBooks repaid(int amount) {
        addLeft(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankTitle.LOANS_RECEIVABLE, amount);
        return this;
    }

    /**
     * お金を預かる
     * @since 1.0
     */
    public PrivateBankBooks keep(int amount) {
        addLeft(PrivateBankTitle.CASH, amount);
        addRight(PrivateBankTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * 預金返済処理
     * @since 1.0
     */
    @Override
    public PrivateBankBooks paidOut(int amount) {
        addLeft(PrivateBankTitle.DEPOSIT, amount);
        addRight(PrivateBankTitle.CASH, amount);
        return this;
    }

    /**
     * 振り込みます。
     * 帳簿上は、預金(負債)と日銀当座預金(資産)がともに減じます。
     * @since 1.0
     */
    public PrivateBankBooks transfer(int amount) {
        addLeft(PrivateBankTitle.DEPOSIT, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 振り込みを受ける
     * 帳簿上は、預金(負債)と日銀当座預金(資産)がともに増えます。
     * @param amount
     * @since 1.0
     */
    public PrivateBankBooks transfered(int amount) {
        addLeft(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankTitle.DEPOSIT, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public BankBooks<PrivateBankTitle> buyGorvementBond(int amount) {
        addLeft(PrivateBankTitle.GOVERNMENT_BOND, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public BankBooks<PrivateBankTitle> sellGovernmentBond(int amount) {
        addLeft(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBankTitle.GOVERNMENT_BOND, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBankBooks paySalary(int amount) {
        int tax = Taxes.computeIncomeTaxFromManthly(amount);
        addLeft(PrivateBankTitle.SALARIES_EXPENSE, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount - tax);
        addRight(PrivateBankTitle.DEPOSITS_RECEIVED, tax);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBankBooks payIncomeTax(int amount) {
        addLeft(PrivateBankTitle.DEPOSITS_RECEIVED, amount);
        addRight(PrivateBankTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }
}
