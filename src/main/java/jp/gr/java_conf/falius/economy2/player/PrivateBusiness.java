package jp.gr.java_conf.falius.economy2.player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.account.DebtMediator;
import jp.gr.java_conf.falius.economy2.account.PrivateBusinessAccount;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.stockmanager.StockManager;

public class PrivateBusiness extends AbstractEntity implements Organization {
    private static final List<PrivateBusiness> sOwns = new ArrayList<PrivateBusiness>();
    private static final double MARGIN = 0.2; // 原価に上乗せするマージン

    private final Set<Product> mProducts;
    private final Map<Product, StockManager> mStockManagers; // 製品ごとに在庫管理
    private final Industry mIndustry;
    private final HumanResourcesDepartment mStuffManager = new HumanResourcesDepartment(5);
    private final PrivateBusinessAccount mAccount = PrivateBusinessAccount.newInstance();

    public static Stream<PrivateBusiness> stream() {
        return sOwns.stream();
    }

    public static Stream<PrivateBusiness> stream(Industry.Type type) {
        return sOwns.stream().filter(pb -> pb.mIndustry.type() == type);
    }

    public static void clear() {
        sOwns.clear();
    }

    public PrivateBusiness(Industry industry, Set<Product> products, int initialExpenses) {
        mIndustry = industry;
        mProducts = products;
        mStockManagers = products.stream()
                .collect(Collectors.toMap(Function.identity(), industry.type()::newManager, (p1, p2) -> p1,
                        () -> new EnumMap<Product, StockManager>(Product.class)));
        mAccount.establish(initialExpenses);
        super.credited(initialExpenses);

        sOwns.add(this);
    }

    @Override
    protected final PrivateBusinessAccount account() {
        return mAccount;
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
        switch (title) {
        case CASH:
            mAccount.saleByCash(price);
            break;
        case RECEIVABLE:
            mAccount.saleByReceivable(price);
            break;
        default:
            throw new IllegalArgumentException(); // no reach
        }

        return OptionalInt.of(price);
    }

    /**
     * 未計上の仕入費を計上します。
     */
    public void calcPurchase() {
        int purchase = mStockManagers.entrySet().stream()
                .map(e -> e.getValue())
                .mapToInt(sm -> sm.calcPurchaseExpense())
                .sum();

        mAccount.purchase(purchase);
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
     * 売り上げた時、通常使用する勘定科目を返します
     * 当座預金でない場合は継承してオーバーライドしてください
     */
    PrivateBusinessAccountTitle saleAccount() {
        return PrivateBusinessAccountTitle.CHECKING_ACCOUNTS;
    }

    @Override
    public boolean isRecruit() {
        return mStuffManager.isRecruit();
    }

    @Override
    public Organization employ(Worker worker) {
        mStuffManager.employ(worker);
        return this;
    }

    @Override
    public Organization fire(Worker worker) {
        mStuffManager.fire(worker);
        return this;
    }

    @Override
    public int paySalary(Worker worker) {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public boolean has(Worker worker) {
        return mStuffManager.has(worker);
    }

    @Override
    protected Bank searchBank() {
        return PrivateBank.stream().findAny().get();
    }

    public void borrow(int amount) {
        Optional<PrivateBank> opt = PrivateBank.stream().filter(pb -> pb.canLend(amount)).findAny();
        PrivateBank bank = opt.get();
        DebtMediator dm = super.offerDebt(amount);
        bank.acceptDebt(dm);
        super.credited(amount);
    }

    public void update() {
        mStockManagers.entrySet().stream()
                .map(e -> e.getValue())
                .forEach(sm -> sm.update());
    }

    @Override
    public void closeEndOfMonth() {
        calcPurchase();
        int payable = mAccount.settlePayable();
        super.credited(-payable);
        int receivable = mAccount.settleReceivable();
        super.credited(receivable);

        int cash = mAccount.get(PrivateBusinessAccountTitle.CASH);
        if (cash < 0) {
            downMoney(-cash);
        }
        int deposit = mAccount.get(PrivateBusinessAccountTitle.CHECKING_ACCOUNTS);
        if (deposit < 0) {
            borrow(-deposit);
        }

    }

}
