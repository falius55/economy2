package jp.gr.java_conf.falius.economy2.book;

import java.time.LocalDate;

import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.enumpack.TitleType;
import jp.gr.java_conf.falius.economy2.helper.Taxes;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class PrivateBusinessBooks extends AbstractDoubleEntryBooks<PrivateBusinessTitle>
        implements EmployableBooks<PrivateBusinessTitle>, PrivateBooks<PrivateBusinessTitle>,
        BorrowableBooks<PrivateBusinessTitle>, AccountOpenableBooks<PrivateBusinessTitle>,
        InstallmentReceivableBooks<PrivateBusinessTitle> {
    private final PrivateAccount mAccount;

    /**
     *
     * @return
     * @since 1.0
     */
    public static PrivateBusinessBooks newInstance(PrivateAccount mainAccount) {
        return new PrivateBusinessBooks(mainAccount);
    }

    /**
     * @since 1.0
     */
    private PrivateBusinessBooks(PrivateAccount mainAccount) {
        super(PrivateBusinessTitle.class, false);
        mAccount = mainAccount;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateAccount mainAccount() {
        return mAccount;
    }

    /*
     * 以下、ほぼ仕分け処理が続く
     */

    /**
     * 創業処理
     * @param initialExpenses 創業費
     * @return
     * @since 1.0
     */
    public PrivateBusinessBooks establish(int initialExpenses) {
        addLeft(PrivateBusinessTitle.CHECKING_ACCOUNTS, initialExpenses);
        addRight(PrivateBusinessTitle.CAPITAL_STOCK, initialExpenses);
        return this;
    }

    /**
     * 売り上げます
     * @param receiveItem 受取科目
     * @since 1.0
     */
    public PrivateBusinessBooks saleBy(PrivateBusinessTitle receiveItem, int amount) {
        if (receiveItem.type() != TitleType.ASSETS) {
            throw new IllegalArgumentException();
        }
        addLeft(receiveItem, amount);
        addRight(PrivateBusinessTitle.SALES, amount);
        return this;
    }

    /**
     * 現金受取での売上
     * @param amount
     * @return
     * @since 1.0
     */
    public PrivateBusinessBooks saleByCash(int amount) {
        return saleBy(PrivateBusinessTitle.CASH, amount);
    }

    /**
     * 売掛金での売上
     * @param amount
     * @return
     * @since 1.0
     */
    public PrivateBusinessBooks saleByReceivable(int amount) {
        return saleBy(PrivateBusinessTitle.RECEIVABLE, amount);
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public PrivateBusinessBooks settleConsumptionTax(int amount) {
        addLeft(PrivateBusinessTitle.TAX, amount);
        addRight(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX, amount);
        return this;
    }

    /**
     * 仕入れる(買掛金)
     * @since 1.0
     */
    public PrivateBusinessBooks stock(int amount) {
        addLeft(PrivateBusinessTitle.PURCHESES, amount);
        addRight(PrivateBusinessTitle.PAYABLE, amount);
        return this;
    }

    /**
     * 固定資産の購入
     * @param date 購入日
     * @param amount 金額
     * @param serviceLife 耐用年数
     * @since 1.0
     */
    private PrivateBusinessBooks buyFixedAsset(LocalDate date, int amount, int serviceLife) {
        addFixedAsset(date, amount, serviceLife);

        addLeft(PrivateBusinessTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 間接法で減価償却する
     * @param date 減価償却日。この日が減価償却日である固定資産が減価償却される
     * @since 1.0
     */
    @Override
    protected void depreciationByIndirect(int amount) {
        addLeft(PrivateBusinessTitle.DEPRECIATION, amount);
        addRight(PrivateBusinessTitle.ACCUMULATED_DEPRECIATION, amount);
    }

    /**
     * 直接法で減価償却する
     * @since 1.0
     */
    @Override
    protected void depreciationByDirect(int amount) {
        addLeft(PrivateBusinessTitle.DEPRECIATION, amount);
        addRight(PrivateBusinessTitle.TANGIBLE_ASSETS, amount);
    }

    /**
     * 土地の購入
     * @since 1.0
     */
    private PrivateBusinessBooks buyLand(int amount) {
        addLeft(PrivateBusinessTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * お金を銀行に預けた時の処理を行う
     * @since 1.0
     */
    @Override
    public PrivateBusinessBooks saveMoney(int amount) {
        addLeft(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBusinessTitle.CASH, amount);
        return this;
    }

    /**
     * お金を下ろした時の処理を行う
     * @since 1.0
     */
    @Override
    public PrivateBusinessBooks downMoney(int amount) {
        addLeft(PrivateBusinessTitle.CASH, amount);
        addRight(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 借金処理
     * @since 1.0
     */
    @Override
    public PrivateBusinessBooks borrow(int amount) {
        addLeft(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBusinessTitle.LOANS_PAYABLE, amount);
        return this;
    }

    /**
     * 返済処理を行う
     * @since 1.0
     */
    @Override
    public PrivateBusinessBooks repay(int amount) {
        addLeft(PrivateBusinessTitle.LOANS_PAYABLE, amount);
        addRight(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     *
     * @param amount
     * @return
     * @since 1.0
     */
    public PrivateBusinessBooks purchase(int amount) {
        addLeft(PrivateBusinessTitle.PURCHESES, amount);
        addRight(PrivateBusinessTitle.PAYABLE, amount);
        return this;
    }

    /**
     * 買掛金を精算します。
     * @return
     * @since 1.0
     */
    public int settlePayable(int amount) {
        addLeft(PrivateBusinessTitle.PAYABLE, amount);
        addRight(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        return amount;
    }

    /**
     * 売掛金を精算します。
     * @return
     * @since 1.0
     */
    public int settleReceivable(int amount) {
        addLeft(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBusinessTitle.RECEIVABLE, amount);
        return amount;
    }

    /**
     * @since 1.0
     */
    @Override
    public int paySalary(int amount) {
        int tax = Taxes.computeIncomeTax(amount * 12) / 12;
        int takeHome = amount - tax;
        addLeft(PrivateBusinessTitle.SALARIES_EXPENSE, amount);
        addRight(PrivateBusinessTitle.CHECKING_ACCOUNTS, takeHome);
        addRight(PrivateBusinessTitle.DEPOSITS_RECEIVED, tax);
        return takeHome;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBusinessBooks payIncomeTax(int amount) {
        addLeft(PrivateBusinessTitle.DEPOSITS_RECEIVED, amount);
        addRight(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBusinessBooks receiveInstallment(int amount) {
        addLeft(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBusinessTitle.SALES, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public PrivateBusinessBooks payConsumptionTax(int amount) {
        addLeft(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX, amount);
        addRight(PrivateBusinessTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

}
