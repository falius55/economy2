package jp.gr.java_conf.falius.economy2.stockmanager;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.IntStream;

import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.loan.Deferment;
import jp.gr.java_conf.falius.economy2.market.Market;

/**
 * 田畑
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class Farm implements StockManager {
    /** 製造する製品 */
    private final Product mProduct;
    /** 在庫 */
    private int mStock = 0;
    /** 最終製造日 */
    private LocalDate mLastManufacture;
    /** 製造期間 */
    private final Period mManufacturePeriod;
    /** 一度の製造数 */
    private final int mProductionVolume;
    /** 単位あたり原価 */
    private final int mCost;

    /**
     *
     * @param product
     * @since 1.0
     */
    public Farm(Product product) {
        mProduct = product;
        mManufacturePeriod = product.manufacturePeriod();
        mProductionVolume = product.productionVolume();
        mLastManufacture = Market.INSTANCE.nowDate();
        int cost = product.price() / product.numOfLot();
        mCost = cost > 0 ? cost : 1;

    }

    /**
     * @since 1.0
     */
    @Override
    public boolean canShipOut(int repuire) {
        update();
        return mStock >= repuire;
    }

    /**
     * @since 1.0
     */
    @Override
    public OptionalInt shipOut(int require) {
        if (!canShipOut(require)) {
            return OptionalInt.empty();
        }
        int cost = mCost * require;
        mStock -= require;
        return OptionalInt.of(cost);
    }

    /**
     * @since 1.0
     */
    @Override
    public Set<Deferment> purchasePayable() {
        return new HashSet<>();
    }

    /**
     * @since 1.0
     */
    @Override
    public int stockCost() {
        return 0;
    }

    /**
     * 在庫や仕入れ情報を更新します。
     * @since 1.0
     */
    @Override
    public void update() {
        LocalDate today = Market.INSTANCE.nowDate();
        int count = (int) mLastManufacture.until(today, ChronoUnit.DAYS) / mManufacturePeriod.getDays(); // 製造日が何回きたか
        IntStream.range(0, count)
                .forEach(n -> manufacture());
    }

    /**
     * 一度製造します。
     * @return 仕入費用の増加分(計上済)
     * @since 1.0
     */
    private void manufacture() {
        mLastManufacture = mLastManufacture.plus(mManufacturePeriod);
        mStock += mProductionVolume;
    }

}
