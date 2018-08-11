package jp.gr.java_conf.falius.economy2.stockmanager;

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

import jp.gr.java_conf.falius.economy2.agreement.Deferment;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class Factory implements StockManager {
    /** 製造する製品 */
    private final Product mProduct;
    /** 製造期間 */
    private final Period mManufacturePeriod;
    /** 一度の製造数 */
    private final int mProductionVolume;

    /** 保有している原材料 */
    private final Map<Product, Integer> mMaterials;
    /**
     * 未計上の仕入れの買掛金
     */
    private final Set<Deferment> mPurchasePayable = new HashSet<>();

    /** 最終製造日 */
    private LocalDate mLastManufacture;
    /** 在庫 */
    private int mStock = 0;
    /**
    *  原価総額(在庫評価額)
    *  あくまで現在の在庫にかかった費用であり、出荷することで平均から算出された分だけ減少する
    */
    private int mStockCost = 0;

    /**
     *
     * @param product
     * @since 1.0
     */
    public Factory(Product product) {
        mProduct = product;
        mManufacturePeriod = product.manufacturePeriod();
        mProductionVolume = product.productionVolume();
        mMaterials = product.materialSet().stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(), e -> 0, (s, t) -> s, () -> new EnumMap<>(Product.class)));
        mLastManufacture = Market.INSTANCE.nowDate();
    }

    /**
     * 在庫があるかどうか
     * @since 1.0
     */
    @Override
    public boolean canShipOut(int require) {
        update();
        return mStock > require;
    }

    /**
     * 在庫や仕入れ情報を更新します。
     * @since 1.0
     */
    private void update() {
        mLastManufacture = updateManufacture(mLastManufacture, mManufacturePeriod);
    }

    /**
     *
     * @param start
     * @param period
     * @return 最後に生産した日
     */
    private LocalDate updateManufacture(LocalDate start, Period period) {
        LocalDate today = Market.INSTANCE.nowDate();
        LocalDate ret = start;
        for (LocalDate next = start.plus(period); next.isBefore(today); ret = next, next = ret.plus(period)) {
            manufacture();
        }
        return ret;
    }

    /**
     * 出荷します
     * @return 原価。出荷に失敗すると空のOptionalInt
     * @since 1.0
     */
    @Override
    public OptionalInt shipOut(int require) {
        if (!canShipOut(require)) {
            return OptionalInt.empty();
        }

        // 原価計算(単純に総費用を在庫で割って平均を求める)
        int cost = (mStockCost / mStock) * require;
        mStockCost -= cost;
        // 在庫の減少
        mStock -= require;
        return OptionalInt.of(cost);
    }

    /**
     * 仕入費用を集計します
     * @return 仕入に要した費用
     * @since 1.0
     */
    @Override
    public Set<Deferment> purchasePayables() {
        update();
        Set<Deferment> ret = new HashSet<>(mPurchasePayable);
        mPurchasePayable.clear();
        return ret;
    }

    /**
     * @since 1.0
     */
    @Override
    public int stockCost() {
        update();
        return mStockCost;
    }

    /**
     * 一度製造します。
     * @return 仕入費用の増加分(計上済)
     * @since 1.0
     */
    private void manufacture() {
        OptionalInt dCost = restock();
        if (!dCost.isPresent()) {
            return;
        }

        mProduct.materials()
                .forEach((material, require) -> {
                    mMaterials.compute(material, (m, stock) -> stock - require * mProductionVolume);
                });
        mStock += mProductionVolume;
    }

    /**
     * １回の製造に必要な原材料をすべてそろえます
     * @return 仕入に要した費用
     * @since 1.0
     */
    private OptionalInt restock() {
        int cost = 0;
        for (Map.Entry<Product, Integer> entry : mProduct.materials().entrySet()) {
            Product material = entry.getKey();
            int require = entry.getValue().intValue() * mProductionVolume;
            int stock = mMaterials.get(material).intValue();

            if (stock >= require) {
                continue;
            }

            int lot = computeRequireLot(material, require);
            OptionalInt optAmount = purchase(material, lot * material.numOfLot());
            if (!optAmount.isPresent()) {
                return OptionalInt.empty();
            }
            int amount = optAmount.getAsInt();
            cost += amount;
        }
        return OptionalInt.of(cost);
    }

    /**
     * 指定された原材料の保有量がrequireになるのに必要なロット数を計算します
     * @return 新たに必要なロット数。必要なければ０
     * @since 1.0
     */
    private int computeRequireLot(Product material, int require) {
        int stock = mMaterials.get(material);
        if (stock >= require) {
            return 0;
        }
        int shortage = require - stock;
        int ret = (int) Math.ceil((double) shortage / material.numOfLot());
        return ret >= 0 ? ret : 0;
    }

    /**
     * 仕入れます
     * @return 仕入に要した費用。仕入に失敗すると空
     * @since 1.0
     */
    private OptionalInt purchase(Product product, int require) {
        // 特定の原材料を指定された分の補充
        Optional<PrivateBusiness> optStore = PrivateBusiness.stream(Industry.Type.FARMER)
                .filter(e -> e.canSale(product, require))
                .findAny();
        if (!optStore.isPresent()) {
            return OptionalInt.empty();
        }
        PrivateBusiness store = optStore.get();

        Optional<Deferment> optDeferment = store.saleByReceivable(product, require);
        if (!optDeferment.isPresent()) {
            return OptionalInt.empty();
        }
        Deferment deferment = optDeferment.get();
        mPurchasePayable.add(deferment);
        int cost = deferment.amount();

        mStockCost += cost;
        mMaterials.compute(product, (k, v) -> v + require);
        return OptionalInt.of(cost);
    }

}
