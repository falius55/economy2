package jp.gr.java_conf.falius.economy2.stockmanager;

import java.time.LocalDate;
import java.time.Period;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.market.MarketInfomation;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

public class Factory implements StockManager {
    /** 製造する製品 */
    private final Product mProduct;
    /** 在庫 */
    private int mStock = 0;
    /** 未計上の仕入費用総額 */
    private int mPurchaseExpense = 0;
    /**
    *  原価総額
    *  あくまで現在の在庫にかかった費用であり、出荷することで平均から算出された分だけ減少する
    */
    private int mTotalCost = 0;
    /** 最終製造日 */
    private LocalDate mLastManufacture;
    /** 製造期間 */
    private final Period mManufacturePeriod;
    /** 一度の製造数 */
    private final int mProductionVolume;
    /** 保有している原材料 */
    private final Map<Product, Integer> mMaterials;

    public Factory(Product product) {
        mProduct = product;
        mManufacturePeriod = product.manufacturePeriod();
        mProductionVolume = product.productionVolume();
        mMaterials = product.materialSet().stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(), e -> 0, (s, t) -> s, () -> new EnumMap<>(Product.class)));
        mLastManufacture = MarketInfomation.INSTANCE.nowDate();
    }

    /**
     * 在庫があるかどうか
     */
    @Override
    public boolean canShipOut(int require) {
        update();
        return mStock > require;
    }

    /**
     * 在庫や仕入れ情報を更新します。
     */
    public void update() {
        LocalDate today = MarketInfomation.INSTANCE.nowDate();
        int count = mLastManufacture.until(today).getDays() / mManufacturePeriod.getDays(); // 製造日が何回きたか
        IntStream.range(0, count)
                .forEach(n -> manufacture());
    }

    /**
     * 出荷します
     * @return 原価。出荷に失敗すると空のOptionalInt
     */
    @Override
    public OptionalInt shipOut(int require) {
        if (!canShipOut(require)) {
            return OptionalInt.empty();
        }

        // 原価計算(単純に総費用を在庫で割って平均を求める)
        int cost = (mTotalCost / mStock) * require;
        mTotalCost -= cost;
        // 在庫の減少
        mStock -= require;
        return OptionalInt.of(cost);
    }

    /**
     * 仕入費用を集計します
     * このメソッドを実行しなければ在庫が補充されませんので注意してください
     * @return 仕入に要した費用
     */
    @Override
    public int calcPurchaseExpense() {
        update();
        int ret = mPurchaseExpense;
        mPurchaseExpense = 0;
        return ret;
    }

    @Override
    public int calcMerchandiseCost() {
        update();
        return mTotalCost;
    }

    /**
     * 一度製造します。
     * @return 仕入費用の増加分(計上済)
     */
    private OptionalInt manufacture() {
        mLastManufacture = mLastManufacture.plus(mManufacturePeriod);

        OptionalInt dCost = restock();
        if (!dCost.isPresent()) {
            return OptionalInt.empty();
        }

        mProduct.materials()
                .forEach((material, require) -> {
                    mMaterials.compute(material, (m, stock) -> stock - require * mProductionVolume);
                });
        mStock += mProductionVolume;

        return dCost;
    }

    /**
     * １回の製造に必要な原材料をすべてそろえます
     * @return 仕入に要した費用
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
     */
    private int computeRequireLot(Product material, int require) {
        int stock = mMaterials.get(material);
        if (stock >= require) {
            return 0;
        }
        int shortfall = require - stock;
        int ret = (int) Math.ceil((double) shortfall / material.numOfLot());
        return ret >= 0 ? ret : 0;
    }

    /**
     * 仕入れます
     * @return 仕入に要した費用。仕入に失敗すると空
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

        OptionalInt optAmount = store.saleByReceivable(product, require);
        if (!optAmount.isPresent()) {
            return OptionalInt.empty();
        }
        int amount = optAmount.getAsInt();

        mTotalCost += amount;
        mPurchaseExpense += amount;
        mMaterials.compute(product, (k, v) -> v + require);
        return OptionalInt.of(amount);
    }

}
