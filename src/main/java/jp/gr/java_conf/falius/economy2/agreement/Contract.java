package jp.gr.java_conf.falius.economy2.agreement;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.Worker;

/**
 * 請負契約を表すクラスです。
 * @author "ymiyauchi"
 * @since 1.0
 */
public class Contract {
    private final Product mProduct;
    private final Period mPeriod; // 期間
    private final LocalDate mAccrualDate; // 債権債務発生日
    private final LocalDate mDeadLine;
    private final Set<Worker> mWorkers = new HashSet<>(); // 従事者
    /** 消費した原材料 */
    private final Map<Product, Integer> mConsumedMaterials;
    private boolean isBrokenUp = false;

    /**
     *
     * @param product
     * @param workers この請負に従事する労働者
     * @since 1.0
     */
    public Contract(Product product, Set<Worker> workers) {
        mProduct = product;
        mPeriod = product.manufacturePeriod();
        mWorkers.addAll(workers);
        mAccrualDate = Market.INSTANCE.nowDate();
        mDeadLine = mAccrualDate.plus(mPeriod);
        mConsumedMaterials = product.materialSet().stream()
                .collect(
                        Collectors.toMap(Function.identity(), e -> 0, (s, t) -> s, () -> new EnumMap<>(Product.class)));
    }

    /**
     * この請負に従事している労働者を返します。
     * @return
     * @since 1.0
     */
    public Set<Worker> employers() {
        return Collections.unmodifiableSet(mWorkers);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean isComplete() {
        return isBrokenUp == true || Market.INSTANCE.nowDate().isAfter(mDeadLine);
    }

    /**
     * 解散する
     * @return 従事していた労働者のセット
     * @since 1.0
     */
    public Set<Worker> breakUp() {
        isBrokenUp = true;
        return employers();
    }

    /**
     * 不足している原材料を計算して返します。
     * @return 原材料から、その原材料の不足数へのマップ
     * @since 1.0
     */
    public Map<Product, Integer> shortageMaterials() {
        double progress = progress();
        return mProduct.materials().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> (int) (e.getValue() * progress) - mConsumedMaterials.get(e.getKey()),
                        (i1, i2) -> i1 + i2, () -> new EnumMap<>(Product.class)));
    }

    public void supplyMaterial(Product product, int num) {
        mConsumedMaterials.computeIfPresent(product, (k, v) -> v + num);
    }

    /**
     * 請負契約が開始してから終了日まで、現時点で経過している日数の割合を返します。
     * @return
     */
    private double progress() {
        if (isComplete()) {
            return 1.0;
        }
        int progressDays = (int) ChronoUnit.DAYS.between(mAccrualDate, Market.INSTANCE.nowDate());
        int allDays = (int) ChronoUnit.DAYS.between(mAccrualDate, mDeadLine);
        int ret = progressDays / allDays;
        return ret > 1.0 ? 1.0 : ret;
    }
}
