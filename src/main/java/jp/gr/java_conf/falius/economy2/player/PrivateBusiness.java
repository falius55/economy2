package jp.gr.java_conf.falius.economy2.player;

import java.time.LocalDate;
import java.time.Period;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.agreement.Deferment;
import jp.gr.java_conf.falius.economy2.agreement.Loan;
import jp.gr.java_conf.falius.economy2.agreement.PaymentByInstallments;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.book.PrivateBusinessBooks;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;
import jp.gr.java_conf.falius.economy2.stockmanager.StockManager;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class PrivateBusiness
        implements AccountOpenable, Employable, PrivateEntity, Borrowable, Deferrable, InstallmentReceivable {
    private static final double MARGIN = 0.2; // 原価に上乗せするマージン

    private final Industry mIndustry;
    private final Set<Product> mProducts;
    private final Map<Product, StockManager> mStockManagers; // 製品ごとに在庫管理
    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final PrivateBank mMainBank;
    private final PrivateBusinessBooks mBooks;
    private final Set<Loan> mLoans = new HashSet<>();
    private final Set<Deferment> mPayables = new HashSet<>();

    /**
     *
     * @return
     * @since 1.0
     */
    public static Stream<PrivateBusiness> stream() {
        return Market.INSTANCE.entities(PrivateBusiness.class);
    }

    /**
     *
     * @param type
     * @return
     * @since 1.0
     */
    public static Stream<PrivateBusiness> stream(Industry.Type type) {
        return stream().filter(pb -> pb.mIndustry.type() == type);
    }

    /**
     *
     * @param founder
     * @param industry
     * @param products
     * @param initialCapital
     * @since 1.0
     */
    PrivateBusiness(WorkerParson founder, Industry industry, Set<Product> products, int initialCapital) {
        mIndustry = industry;
        mProducts = products;
        mStockManagers = products.stream()
                .collect(Collectors.toMap(Function.identity(),
                        (product) -> industry.type().newManager(product, mStuffManager),
                        (p1, p2) -> p1, () -> new EnumMap<Product, StockManager>(Product.class)));
        mMainBank = searchBank();
        PrivateAccount account = mMainBank.createAccount(this);
        mBooks = PrivateBusinessBooks.newInstance(account);
        mBooks.establish(initialCapital);
        Market.INSTANCE.aggregater().add(this);
        employ(founder);
    }

    /**
     * 候補が見つからなければ例外を投げる
     * @since 1.0
     */
    private PrivateBank searchBank() {
        Optional<PrivateBank> opt = PrivateBank.stream().findAny();
        if (!opt.isPresent()) {
            throw new IllegalStateException("market has no banks");
        }
        return opt.get();
    }

    /**
     * @since 1.0
     */
    @Override
    public final PrivateBusinessBooks books() {
        return mBooks;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBank mainBank() {
        return mMainBank;
    }

    /**
     * @since 1.0
     */
    @Override
    public int cash() {
        return mBooks.get(PrivateBusinessTitle.CASH);
    }

    /**
     * @since 1.0
     */
    @Override
    public int deposit() {
        return mBooks.get(PrivateBusinessTitle.CHECKING_ACCOUNTS);
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean isRecruit() {
        return mStuffManager.isRecruit();
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean has(Worker worker) {
        return mStuffManager.has(worker);
    }

    /**
     * 業種が同じかどうかを判定します
     * @since 1.0
     */
    public boolean is(Industry industry) {
        return industry == mIndustry;
    }

    /**
     * 業態が同じかどうかを判定します
     * @since 1.0
     */
    public boolean is(Industry.Type type) {
        return type == mIndustry.type();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int stockCost() {
        return mStockManagers.values().stream()
                .mapToInt(StockManager::stockCost)
                .sum();
    }

    /**
     * 製品が売っているのかどうかを判断します
     * @param product 製品
     * @param require 必要量
     * @since 1.0
     */
    public boolean canSale(Product product, int require) {
        // 製品を取り扱っており、在庫があればtrue
        return mProducts.contains(product) && mStockManagers.get(product).canShipOut(require);
    }

    /**
     * 日課処理を行います。
     * @since 1.0
     */
    public void closeEndOfDay(LocalDate date) {
        recodePurchase();
    }

    /**
     * @since 1.0
     */
    @Override
    public void closeEndOfMonth() {
        mPayables.stream()
                .forEach(def -> def.settle(this));
        mPayables.clear();

        int cash = cash();
        if (cash < 0) {
            downMoney(-cash);
        }
        int deposit = deposit();
        if (deposit < 0) {
            borrow(-deposit);
        }
    }

    /**
     * @since 1.0
     */
    @Override
    public AccountOpenable saveMoney(int amount) {
        mBooks.saveMoney(amount);
        mainBank().keep(this, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public AccountOpenable downMoney(int amount) {
        mBooks.downMoney(amount);
        mainBank().paidOut(this, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean borrow(int amount) {
        Optional<PrivateBank> opt = PrivateBank.stream().filter(pb -> pb.canLend(amount)).findAny();
        if (!opt.isPresent()) {
            return false;
        }
        PrivateBank bank = opt.get();
        Loan dm = offerDebt(amount);
        bank.acceptDebt(dm);
        return true;
    }

    /**
     * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
     * 借金が不成立の場合は想定外
     * @since 1.0
     */
    private Loan offerDebt(int amount) {
        Loan debt = new Loan(this, amount, Period.ofYears(1));
        mLoans.add(debt);
        return debt;
    }

    /**
     * 借金を返済します
     * @since 1.0
     */
    @Override
    public void repay(int amount) {
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable employ(Worker worker) {
        mStuffManager.employ(worker);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable fire(Worker worker) {
        mStuffManager.fire(worker);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public int paySalary(Worker worker, int salary) {
        int takeHome = mBooks.paySalary(salary);
        worker.books().getSalary(salary);
        PrivateAccount workerAccount = worker.books().mainAccount();
        mainBank().account(this).transfer(workerAccount, takeHome);
        return salary;
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable payIncomeTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(PrivateBusinessTitle.DEPOSITS_RECEIVED);
        nationBooks.collectIncomeTaxes(amount);
        mBooks.payIncomeTax(amount);
        mainBank().account(this).transfer(CentralBank.INSTANCE.nationAccount(), amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public Set<Worker> employers() {
        return mStuffManager.employers();
    }

    /**
     * @since 1.0
     */
    public PrivateBusiness payConsumptionTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX);
        nationBooks.collectConsumptionTax(amount);
        mBooks.payConsumptionTax(amount);
        mainBank().account(this).transfer(CentralBank.INSTANCE.nationAccount(), amount);
        return this;
    }

    /**
     * 指定された製品を指定された数量分売ります
     * @return 売値。販売できなければ空のOptionalInt
     * @since 1.0
     */
    public OptionalInt saleByCash(Product product, int require) {
        OptionalInt optPrice = sale(product, require);
        if (!optPrice.isPresent()) {
            return OptionalInt.empty();
        }
        mBooks.saleByCash(optPrice.getAsInt());
        return optPrice;
    }

    /**
     * 売り掛けで販売します。
     * @since 1.0
     */
    @Override
    public Optional<Deferment> saleByReceivable(Product product, int require) {
        OptionalInt optPrice = sale(product, require);
        if (!optPrice.isPresent()) {
            return Optional.empty();
        }
        int price = optPrice.getAsInt();
        mBooks.saleByReceivable(price);
        Deferment ret = new Deferment(this, price);
        return Optional.of(ret);
    }

    /**
     *
     * @param product
     * @param require
     * @return
     * @since 1.0
     */
    public Optional<PaymentByInstallments<PrivateBusinessTitle>> saleByInstallments(
            Product product, int require) {
        OptionalInt optPrice = sale(product, require);
        if (!optPrice.isPresent()) {
            return Optional.empty();
        }
        int price = optPrice.getAsInt();
        PaymentByInstallments<PrivateBusinessTitle> ret = PaymentByInstallments.newInstanceByCount(product, price, 12,
                mBooks);
        return Optional.of(ret);
    }

    /**
     * @since 1.0
     */
    private OptionalInt sale(Product product, int require) {
        // 倉庫、工場から製品を持ってくる
        OptionalInt optCost = mStockManagers.get(product).shipOut(require);
        if (!optCost.isPresent()) {
            return OptionalInt.empty();
        }
        int cost = optCost.getAsInt();

        int price = (int) (cost * (1 + MARGIN)); // 原価にマージンを上乗せして売値を決める
        int accruedConsumptionTax = Taxes.computeConsumptionTax(price) - Taxes.computeConsumptionTax(cost);
        mBooks.settleConsumptionTax(accruedConsumptionTax);
        return OptionalInt.of(price);
    }

    /**
     * 未計上の仕入費を計上します。
     * @since 1.0
     */
    public void recodePurchase() {
        Set<Deferment> payables = mStockManagers.values().stream()
                .map(StockManager::purchasePayables)
                .flatMap(Set::stream) // Set<Deferment>のstreamからDefermentのstreamにする
                .collect(Collectors.toSet());
        mPayables.addAll(payables);

        int purchase = payables.stream()
                .mapToInt(Deferment::amount)
                .sum();
        mBooks.purchase(purchase);
    }

    // テスト用
    public boolean check() {
        int loan = mLoans.stream().mapToInt(Loan::amount).sum();
        if (loan != mBooks.get(PrivateBusinessTitle.LOANS_PAYABLE)) {
            return false;
        }
        int payable = mPayables.stream().mapToInt(Deferment::amount).sum();
        if (payable != mBooks.get(PrivateBusinessTitle.PAYABLE)) {
            return false;
        }
        return true;
    }

}
