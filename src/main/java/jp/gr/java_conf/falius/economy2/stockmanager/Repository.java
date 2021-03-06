package jp.gr.java_conf.falius.economy2.stockmanager;

import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import jp.gr.java_conf.falius.economy2.agreement.Deferment;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

/**
 * 倉庫
 * @since 1.0
 */
public class Repository implements StockManager {
    private final Product mProduct; // 製造する製品
    private int mStock = 0; // 在庫
    /**
     * 未計上の仕入れ買掛金
     */
    private final Set<Deferment> mPurchasePayable = new HashSet<>();
    private int mStockCost = 0; // 原価総額
    private Industry.Type mProductSource; // 仕入先の業態(小売なら流通業者、流通業者ならメーカー)

    public Repository(Product product, Industry.Type productSource) {
        mProduct = product;
        mProductSource = productSource;
    }

    /**
     * 在庫があるかどうか。あるいは、在庫補充のめどが立つかどうか
     * @since 1.0
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
     * @since 1.0
     */
    @Override
    public OptionalInt shipOut(int require) {
        if (!canShipOut(require)) {
            return OptionalInt.empty();
        }

        if (mStock < require) {
            if (!purchase(require - mStock)) {
                return OptionalInt.empty();
            }
        }

        int cost = (mStockCost / mStock) * require;
        mStock -= require;
        mStockCost -= cost;
        return OptionalInt.of(cost);
    }

    /**
     * 仕入れます
     * @return 仕入に要した費用。仕入に失敗すると空
     * @since 1.0
     */
    private boolean purchase(int require) {
        int sourceRequireLot = require % mProduct.numOfLot() == 0 ? require / mProduct.numOfLot()
                : require / mProduct.numOfLot() + 1;
        int sourceRequire = sourceRequireLot * mProduct.numOfLot();

        // 仕入先の店を探す
        Optional<PrivateBusiness> optStore = PrivateBusiness.stream(mProductSource)
                .filter(e -> e.canSale(mProduct, sourceRequire))
                .findAny();
        if (!optStore.isPresent()) {
            return false; // 仕入れできる店が見つからない
        }
        PrivateBusiness store = optStore.get();

        // 購入する
        Optional<Deferment> optPayable = store.saleByReceivable(mProduct, sourceRequire);
        if (!optPayable.isPresent()) {
            return false; // 購入できなかった
        }
        Deferment payable = optPayable.get();
        int cost = payable.amount();

        mPurchasePayable.add(payable);
        mStock += sourceRequire;
        mStockCost += cost;
        return true;
    }

    /**
     * 仕入費用を集計します
     * @return 仕入に要した費用
     * @since 1.0
     */
    @Override
    public Set<Deferment> purchasePayables() {
        Set<Deferment> ret = new HashSet<>(mPurchasePayable);
        mPurchasePayable.clear();
        return ret;
    }

    /**
     * @since 1.0
     */
    @Override
    public int stockCost() {
        return mStockCost;
    }

}
