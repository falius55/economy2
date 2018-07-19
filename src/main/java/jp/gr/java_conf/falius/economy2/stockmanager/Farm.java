package jp.gr.java_conf.falius.economy2.stockmanager;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.market.Market;

/**
 * 田畑
 * @author "ymiyauchi"
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

    public Farm(Product product) {
        mProduct = product;
        mManufacturePeriod = product.manufacturePeriod();
        mProductionVolume = product.productionVolume();
        mLastManufacture = Market.INSTANCE.nowDate();
        int cost = product.price() / product.numOfLot();
        mCost = cost > 0 ? cost : 1;

    }

    @Override
    public boolean canShipOut(int repuire) {
        update();
        return mStock >= repuire;
    }

    @Override
    public OptionalInt shipOut(int require) {
        if (!canShipOut(require)) {
            return OptionalInt.empty();
        }
        int cost = mCost * require;
        mStock -= require;
        return OptionalInt.of(cost);
    }

    @Override
    public int calcPurchaseExpense() {
        return 0;
    }

    @Override
    public int stockCost() {
        return 0;
    }

    /**
     * 在庫や仕入れ情報を更新します。
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
     */
    private void manufacture() {
        mLastManufacture = mLastManufacture.plus(mManufacturePeriod);
        mStock += mProductionVolume;
    }

}
