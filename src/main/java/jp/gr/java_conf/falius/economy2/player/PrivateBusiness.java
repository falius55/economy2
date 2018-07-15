package jp.gr.java_conf.falius.economy2.player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public PrivateBusiness(Industry industry, Set<Product> products) {
        mIndustry = industry;
        mProducts = products;
        mStockManagers = products.stream()
                .collect(Collectors.toMap(Function.identity(), industry.type()::newManager, (p1, p2) -> p1,
                        () -> new EnumMap<Product, StockManager>(Product.class)));

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
     * 指定された製品を指定されたロット分売ります
     * @return 売値。販売できなければ空のOptionalInt
     */
    public OptionalInt sale(Product product, int require) {
        // 倉庫、工場から製品を持ってくる
        OptionalInt cost = mStockManagers.get(product).shipOut(require);
        if (!cost.isPresent()) { return OptionalInt.empty(); }

        int price = (int) (cost.getAsInt() * (1 + MARGIN)); // 原価にマージンを上乗せして売値を決める
        mAccount.saleBy(saleAccount(), price);

        return OptionalInt.of(price);
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

}
