package jp.gr.java_conf.falius.economy2.player;

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
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.book.PrivateBusinessBooks;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.loan.Deferment;
import jp.gr.java_conf.falius.economy2.loan.Loan;
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
public class PrivateBusiness implements AccountOpenable, Employable, PrivateEntity, Borrowable, Deferrable {
    private static final Set<PrivateBusiness> sOwns = new HashSet<PrivateBusiness>();
    private static final double MARGIN = 0.2; // 原価に上乗せするマージン
    private static final int SALARY = 50000;

    private final Industry mIndustry;
    private final Set<Product> mProducts;
    private final Map<Product, StockManager> mStockManagers; // 製品ごとに在庫管理
    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final PrivateBusinessBooks mBooks = PrivateBusinessBooks.newInstance();
    private final PrivateBank mMainBank;
    private final Set<Loan> mLoans = new HashSet<>();
    private final Set<Deferment> mPayables = new HashSet<>();

    /**
     *
     * @return
     * @since 1.0
     */
    public static Stream<PrivateBusiness> stream() {
        return sOwns.stream();
    }

    /**
     *
     * @param type
     * @return
     * @since 1.0
     */
    public static Stream<PrivateBusiness> stream(Industry.Type type) {
        return sOwns.stream().filter(pb -> pb.mIndustry.type() == type);
    }

    /**
     * @since 1.0
     */
    public static void clear() {
        sOwns.clear();
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
                .collect(Collectors.toMap(Function.identity(), industry.type()::newManager, (p1, p2) -> p1,
                        () -> new EnumMap<Product, StockManager>(Product.class)));
        mMainBank = searchBank();
        mMainBank.createAccount(this);
        mBooks.establish(initialCapital);
        sOwns.add(this);
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
        return mBooks.get(PrivateBusinessAccountTitle.CASH);
    }

    /**
     * @since 1.0
     */
    @Override
    public int deposit() {
        return mBooks.get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
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
     * @since 1.0
     */
    @Override
    public void closeEndOfMonth() {
        update();
        mPayables.stream()
                .forEach(def -> def.settle(this));

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
        Loan debt = new Loan(this, mainBank().account(this), amount, Period.ofYears(1));
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
    public int paySalary(Worker worker) {
        mBooks.paySalary(SALARY);
        worker.getSalary(this, SALARY);
        return SALARY;
    }

    /**
     * @since 1.0
     */
    @Override
    public int transfer(PrivateAccount target, int amount) {
        return mainBank().account(this).transfer(target, amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public Employable payIncomeTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED);
        nationBooks.collectIncomeTaxes(amount);
        mBooks.payIncomeTax(amount);
        mainBank().account(this).transfer(CentralBank.INSTANCE.nationAccount(), amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public PrivateBusiness payConsumptionTax(GovernmentBooks nationBooks) {
        int amount = mBooks.get(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX);
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
        return saleBy(PrivateBusinessAccountTitle.CASH, product, require);
    }

    /**
     * @since 1.0
     */
    @Override
    public Optional<Deferment> saleByReceivable(Product product, int require) {
        OptionalInt optPrice = saleBy(PrivateBusinessAccountTitle.RECEIVABLE, product, require);
        if (!optPrice.isPresent()) {
            return Optional.empty();
        }
        int price = optPrice.getAsInt();
        Deferment ret = new Deferment(this, price);
        return Optional.of(ret);
    }

    /**
     * @since 1.0
     */
    private OptionalInt saleBy(PrivateBusinessAccountTitle title, Product product, int require) {
        // 倉庫、工場から製品を持ってくる
        OptionalInt optCost = mStockManagers.get(product).shipOut(require);
        if (!optCost.isPresent()) {
            return OptionalInt.empty();
        }
        int cost = optCost.getAsInt();

        int price = (int) (cost * (1 + MARGIN)); // 原価にマージンを上乗せして売値を決める
        switch (title) {
        case CASH:
            mBooks.saleByCash(price);
            break;
        case RECEIVABLE:
            mBooks.saleByReceivable(price);
            break;
        default:
            throw new IllegalArgumentException(); // no reach
        }

        int accruedConsumptionTax = Taxes.computeConsumptionTax(price) - Taxes.computeConsumptionTax(cost);
        mBooks.settleConsumptionTax(accruedConsumptionTax);
        return OptionalInt.of(price);
    }

    /**
     * @since 1.0
     */
    public void update() {
        // use in Market#closeEndOfMonth
        mStockManagers.values().stream()
                .forEach(StockManager::update);
        recodePurchase();
    }

    /**
     * 未計上の仕入費を計上します。
     * @since 1.0
     */
    private void recodePurchase() {
        Set<Deferment> payables = mStockManagers.values().stream()
                .map(StockManager::purchasePayable)
                .reduce(new HashSet<>(), (collect, payable) -> {collect.addAll(payable); return collect;});
        mPayables.addAll(payables);

        int purchase = payables.stream()
                .mapToInt(Deferment::amount)
                .sum();
        mBooks.purchase(purchase);
    }

}
