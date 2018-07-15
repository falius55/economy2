package jp.gr.java_conf.falius.economy2.stockmanager;

import java.util.Optional;
import java.util.OptionalInt;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

/**
 * 倉庫
 */
public class Repository implements StockManager {
    private final Product mProduct; // 製造する製品
    private int mStock = 0; // 在庫
    private int mPurchaseExpense = 0; // 未計上の仕入費用総額
    private int mTotalCost = 0; // 原価総額
    private Industry.Type mProductSource; // 仕入先の業態(小売なら流通業者、流通業者ならメーカー)

    public Repository(Product product, Industry.Type productSource) {
        mProduct = product;
        mProductSource = productSource;
    }

    /**
     * 在庫があるかどうか。あるいは、在庫補充のめどが立つかどうか
     */
    @Override
    public boolean canShipOut(int require) {
        if (mStock >= require) {
            return true;
        }

        int shortfall = require - mStock;
        int sourceRequireLot = shortfall % mProduct.numOfLot() == 0 ? shortfall / mProduct.numOfLot()
                : shortfall / mProduct.numOfLot() + 1;
        int sourceRequire = sourceRequireLot * mProduct.numOfLot();
        return PrivateBusiness.stream(mProductSource)
                .anyMatch(store -> store.canSale(mProduct, sourceRequire));
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

        if (mStock < require) {
            OptionalInt optPurchaseExpence = purchase(require - mStock);
            if (optPurchaseExpence.isPresent()) {
                int purchaseExpence = optPurchaseExpence.getAsInt();
                mTotalCost += purchaseExpence;
                mPurchaseExpense += purchaseExpence;
            } else {
                return OptionalInt.empty();  // 仕入失敗による出荷不可
            }
        }

        int cost = (mTotalCost / mStock) * require;
        mStock -= require;
        mTotalCost -= cost;
        return OptionalInt.of(cost);
    }

    /**
     * 仕入費用を集計します
     * @return 仕入に要した費用
     */
    @Override
    public int calcPurchaseExpense() {
        int ret = mPurchaseExpense;
        mPurchaseExpense = 0;
        return ret;
    }

    @Override
    public int calcMerchandiseCost() {
       return mTotalCost;
    }

    /**
     * 仕入れます
     * @return 仕入に要した費用。仕入に失敗すると空
     */
    private OptionalInt purchase(int require) {
        int sourceRequireLot = require % mProduct.numOfLot() == 0 ? require / mProduct.numOfLot()
                : require / mProduct.numOfLot() + 1;
        int sourceRequire = sourceRequireLot * mProduct.numOfLot();

        // 仕入先の店を探す
        Optional<PrivateBusiness> optStore = PrivateBusiness.stream(mProductSource)
                .filter(e -> e.canSale(mProduct, sourceRequire))
                .findAny();
        if (!optStore.isPresent()) { return OptionalInt.empty(); }  // 仕入れできる店が見つからない

        // 購入する
        OptionalInt cost = optStore.get().saleByReceivable(mProduct, sourceRequire);
        if (!cost.isPresent()) { return OptionalInt.empty(); }  // 購入できなかった

        mStock += sourceRequire;
        return cost;
    }

}
