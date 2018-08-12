package jp.gr.java_conf.falius.economy2.stockmanager;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.OptionalInt;
import java.util.Set;

import jp.gr.java_conf.falius.economy2.agreement.Deferment;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
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
    private final double mCost;

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
        double cost = (double) product.lotCost() / product.numOfLot();
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
        int cost = (int) (mCost * require);
        mStock -= require;
        return OptionalInt.of(cost);
    }

    /**
     * @since 1.0
     */
    @Override
    public Set<Deferment> purchasePayables() {
        update();
        return Collections.emptySet();
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
        return Market.dateStream(start.plus(period), today, period)
                .peek(d -> mStock += mProductionVolume)
                .reduce((f, s) -> s)
                .orElseGet(() -> start);
    }
}
