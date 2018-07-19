package jp.gr.java_conf.falius.economy2.player;

import java.time.Period;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.GovernmentAccount;
import jp.gr.java_conf.falius.economy2.account.PrivateBusinessAccount;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.helper.Taxes;
import jp.gr.java_conf.falius.economy2.loan.Loan;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;
import jp.gr.java_conf.falius.economy2.stockmanager.StockManager;

public class PrivateBusiness implements AccountOpenable, Employable, PrivateEntity, Borrowable {
    private static final List<PrivateBusiness> sOwns = new ArrayList<PrivateBusiness>();
    private static final double MARGIN = 0.2; // 原価に上乗せするマージン

    private final Industry mIndustry;
    private final Set<Product> mProducts;
    private final Map<Product, StockManager> mStockManagers; // 製品ごとに在庫管理
    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final PrivateBusinessAccount mAccount = PrivateBusinessAccount.newInstance();
    private final PrivateBank mMainBank;
    private final Set<Loan> mLoans = new HashSet<>();

    public static Stream<PrivateBusiness> stream() {
        return sOwns.stream();
    }

    public static Stream<PrivateBusiness> stream(Industry.Type type) {
        return sOwns.stream().filter(pb -> pb.mIndustry.type() == type);
    }

    public static void clear() {
        sOwns.clear();
    }

    PrivateBusiness(Worker founder, Industry industry, Set<Product> products, int initialCapital) {
        mIndustry = industry;
        mProducts = products;
        mStockManagers = products.stream()
                .collect(Collectors.toMap(Function.identity(), industry.type()::newManager, (p1, p2) -> p1,
                        () -> new EnumMap<Product, StockManager>(Product.class)));
        mMainBank = searchBank();

        mAccount.establish(initialCapital);
        mMainBank.transfered(initialCapital);
        sOwns.add(this);
        employ(founder);
    }

    /**
     * 候補が見つからなければ例外を投げる
     */
    private PrivateBank searchBank() {
        Optional<PrivateBank> opt = PrivateBank.stream().findAny();
        if (!opt.isPresent()) {
            throw new IllegalStateException("market has no banks");
        }
        return opt.get();
    }

    @Override
    public final Account<PrivateBusinessAccountTitle> accountBook() {
        return mAccount;
    }

    @Override
    public PrivateBank mainBank() {
        return mMainBank;
    }

    @Override
    public int cash() {
        return mAccount.get(PrivateBusinessAccountTitle.CASH);
    }

    @Override
    public int deposit() {
        return mAccount.get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
    }

    @Override
    public boolean isRecruit() {
        return mStuffManager.isRecruit();
    }

    @Override
    public boolean has(Worker worker) {
        return mStuffManager.has(worker);
    }

    /**
     * 業種が同じかどうかを判定します
     */
    public boolean is(Industry industry) {
        return industry == mIndustry;
    }

    /**
     * 業態が同じかどうかを判定します
     */
    public boolean is(Industry.Type type) {
        return type == mIndustry.type();
    }

    /**
     * 製品が売っているのかどうかを判断します
     * @param product 製品
     * @param lot 必要となるロット数
     */
    public boolean canSale(Product product, int require) {
        // 製品を取り扱っており、在庫があればtrue
        return mProducts.contains(product) && mStockManagers.get(product).canShipOut(require);
    }

    @Override
    public void closeEndOfMonth() {
        calcPurchase();
        int payable = mAccount.settlePayable();
        mMainBank.transfer(payable);
        int receivable = mAccount.settleReceivable();
        mMainBank.transfered(receivable);

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
     * 未計上の仕入費を計上します。
     */
    private void calcPurchase() {
        int purchase = mStockManagers.entrySet().stream()
                .map(e -> e.getValue())
                .mapToInt(sm -> sm.calcPurchaseExpense())
                .sum();
        mAccount.purchase(purchase);
    }

    @Override
    public AccountOpenable saveMoney(int amount) {
        mAccount.saveMoney(amount);
        mainBank().keep(amount);
        return this;
    }

    @Override
    public AccountOpenable downMoney(int amount) {
        mAccount.downMoney(amount);
        mainBank().paidOut(amount);
        return this;
    }

    @Override
    public boolean borrow(int amount) {
        Optional<PrivateBank> opt = PrivateBank.stream().filter(pb -> pb.canLend(amount)).findAny();
        if (!opt.isPresent()) { return false; }
        PrivateBank bank = opt.get();
        Loan dm = offerDebt(amount);
        bank.acceptDebt(dm);
        mMainBank.transfered(amount);
        return true;
    }

    /**
     * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
     * 借金が不成立の場合は想定外
     */
    private Loan offerDebt(int amount) {
        Loan debt = new Loan(mAccount, amount, Period.ofYears(1));
        mLoans.add(debt);
        return debt;
    }

    /**
     * 借金を返済します
     */
    @Override
    public void repay(int amount) {
    }

    @Override
    public Employable employ(Worker worker) {
        mStuffManager.employ(worker);
        return this;
    }

    @Override
    public Employable fire(Worker worker) {
        mStuffManager.fire(worker);
        return this;
    }

    @Override
    public int paySalary(Worker worker) {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public Employable payIncomeTax(GovernmentAccount nationAccount) {
        int amount = mAccount.get(PrivateBusinessAccountTitle.DEPOSITS_RECEIVED);
        nationAccount.collectIncomeTaxes(amount);
        mAccount.payIncomeTax(amount);
        CentralBank.INSTANCE.transfered(amount);
        return this;
    }

    public PrivateBusiness payConsumptionTax(GovernmentAccount nationAccount) {
        int amount = mAccount.get(PrivateBusinessAccountTitle.ACCRUED_CONSUMPTION_TAX);
        nationAccount.collectConsumptionTax(amount);
        mAccount.payConsumptionTax(amount);
        CentralBank.INSTANCE.transfered(amount);
        return this;
    }

    /**
     * 指定された製品を指定された数量分売ります
     * @return 売値。販売できなければ空のOptionalInt
     */
    public OptionalInt saleByCash(Product product, int require) {
        return saleBy(PrivateBusinessAccountTitle.CASH, product, require);
    }

    public OptionalInt saleByReceivable(Product product, int require) {
        return saleBy(PrivateBusinessAccountTitle.RECEIVABLE, product, require);
    }

    private OptionalInt saleBy(PrivateBusinessAccountTitle title, Product product, int require) {
        // 倉庫、工場から製品を持ってくる
        OptionalInt optCost = mStockManagers.get(product).shipOut(require);
        if (!optCost.isPresent()) {
            return OptionalInt.empty();
        }
        int cost = optCost.getAsInt();

        int price = (int) (cost * (1 + MARGIN)); // 原価にマージンを上乗せして売値を決める
        int accruedConsumptionTax = Taxes.computeAccruedConsumptionTax(price, cost);  // 未払消費税

        switch (title) {
        case CASH:
            mAccount.saleByCash(price - accruedConsumptionTax, accruedConsumptionTax);
            break;
        case RECEIVABLE:
            mAccount.saleByReceivable(price - accruedConsumptionTax, accruedConsumptionTax);
            break;
        default:
            throw new IllegalArgumentException(); // no reach
        }

        return OptionalInt.of(price);
    }

    public void update() {
        // use in Market#closeEndOfMonth
        mStockManagers.entrySet().stream()
                .map(e -> e.getValue())
                .forEach(sm -> sm.update());
    }

}
