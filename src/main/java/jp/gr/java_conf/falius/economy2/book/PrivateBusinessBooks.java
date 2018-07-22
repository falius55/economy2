package jp.gr.java_conf.falius.economy2.book;

import java.time.LocalDate;

import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.helper.Taxes;

public class PrivateBusinessBooks extends AbstractDoubleEntryBooks<PrivateBusinessAccountTitle>
        implements EmployableBooks<PrivateBusinessAccountTitle>, PrivateBooks<PrivateBusinessAccountTitle>,
        BorrowableBooks<PrivateBusinessAccountTitle>, AccountOpenableBooks<PrivateBusinessAccountTitle> {

    private PrivateBusinessBooks() {
        super(PrivateBusinessAccountTitle.class);
    }

    public static PrivateBusinessBooks newInstance() {
        return new PrivateBusinessBooks();
    }

    /*
     * 以下、ほぼ仕分け処理が続く
     */

    /**
     * 創業処理
     * @param initialExpenses 創業費
     * @return
     */
    public PrivateBusinessBooks establish(int initialExpenses) {
        addLeft(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, initialExpenses);
        addRight(PrivateBusinessAccountTitle.CAPITAL_STOCK, initialExpenses);
        return this;
    }

    /**
     * 売り上げます
     * @param receiveItem 受取科目
     */
    public PrivateBusinessBooks saleBy(PrivateBusinessAccountTitle receiveItem, int amount) {
        if (receiveItem.type() != AccountType.ASSETS) {
            throw new IllegalArgumentException();
        }
        addLeft(receiveItem, amount);
        addRight(PrivateBusinessAccountTitle.SALES, amount);
        return this;
    }

    // 現金受け取り
    public PrivateBusinessBooks saleByCash(int amount) {
        return saleBy(PrivateBusinessAccountTitle.CASH, amount);
    }

    // 売掛金
    public PrivateBusinessBooks saleByReceivable(int amount) {
        return saleBy(PrivateBusinessAccountTitle.RECEIVABLE, amount);
    }

    public PrivateBusinessBooks settleConsumptionTax(int amount) {
        addLeft(PrivateBusinessAccountTitle.TAX, amount);
        addRight(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX, amount);
        return this;
    }

    /**
     * 仕入れる(買掛金)
     */
    public PrivateBusinessBooks stock(int amount) {
        addLeft(PrivateBusinessAccountTitle.PURCHESES, amount);
        addRight(PrivateBusinessAccountTitle.PAYABLE, amount);
        return this;
    }

    /**
     * 固定資産の購入
     * @param date 購入日
     * @param amount 金額
     * @param serviceLife 耐用年数
     */
    private PrivateBusinessBooks buyFixedAsset(LocalDate date, int amount, int serviceLife) {
        addFixedAsset(date, amount, serviceLife);

        addLeft(PrivateBusinessAccountTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 間接法で減価償却する
     * @param date 減価償却日。この日が減価償却日である固定資産が減価償却される
     */
    private PrivateBusinessBooks depreciationByIndirect(LocalDate date) {
        int amount = recordFixedAssets(date);
        addLeft(PrivateBusinessAccountTitle.DEPRECIATION, amount);
        addRight(PrivateBusinessAccountTitle.ACCUMULATED_DEPRECIATION, amount);
        return this;
    }

    /**
     * 直接法で減価償却する
     */
    private PrivateBusinessBooks depreciationByDirect(LocalDate date) {
        int amount = recordFixedAssets(date);
        addLeft(PrivateBusinessAccountTitle.DEPRECIATION, amount);
        addRight(PrivateBusinessAccountTitle.TANGIBLE_ASSETS, amount);
        return this;
    }

    /**
     * 土地の購入
     */
    private PrivateBusinessBooks buyLand(int amount) {
        addLeft(PrivateBusinessAccountTitle.TANGIBLE_ASSETS, amount);
        addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * お金を銀行に預けた時の処理を行う
     */
    @Override
    public PrivateBusinessBooks saveMoney(int amount) {
        addLeft(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBusinessAccountTitle.CASH, amount);
        return this;
    }

    /**
     * お金を下ろした時の処理を行う
     */
    @Override
    public PrivateBusinessBooks downMoney(int amount) {
        addLeft(PrivateBusinessAccountTitle.CASH, amount);
        addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    /**
     * 借金処理
     */
    @Override
    public PrivateBusinessBooks borrow(int amount) {
        addLeft(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBusinessAccountTitle.LOANS_PAYABLE, amount);
        return this;
    }

    /**
     * 返済処理を行う
     */
    @Override
    public PrivateBusinessBooks repay(int amount) {
        addLeft(PrivateBusinessAccountTitle.LOANS_PAYABLE, amount);
        addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    public PrivateBusinessBooks purchase(int amount) {
        addLeft(PrivateBusinessAccountTitle.PURCHESES, amount);
        addRight(PrivateBusinessAccountTitle.PAYABLE, amount);
        return this;
    }

    /**
     * 買掛金を精算します。
     * @return
     */
    public int settlePayable(int amount) {
        addLeft(PrivateBusinessAccountTitle.PAYABLE, amount);
        addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        return amount;
    }

    /**
     * 売掛金を精算します。
     * @return
     */
    public int settleReceivable(int amount) {
        addLeft(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        addRight(PrivateBusinessAccountTitle.RECEIVABLE, amount);
        return amount;
    }

    @Override
    public PrivateBusinessBooks paySalary(int amount) {
        int tax = Taxes.computeIncomeTax(amount * 12) / 12;
        addLeft(PrivateBusinessAccountTitle.SALARIES_EXPENSE, amount);
        addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount - tax);
        addRight(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED, tax);
        return this;
    }

    @Override
    public PrivateBusinessBooks payIncomeTax(int amount) {
        addLeft(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED, amount);
        addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

    public PrivateBusinessBooks payConsumptionTax(int amount) {
        addLeft(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX, amount);
        addRight(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS, amount);
        return this;
    }

}
