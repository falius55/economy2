package jp.gr.java_conf.falius.economy2.market;

import java.time.LocalDate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.market.aggre.MarketAggregater;
import jp.gr.java_conf.falius.economy2.player.Employable;
import jp.gr.java_conf.falius.economy2.player.Entity;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class Market {

    public static final Market INSTANCE;
    private LocalDate mDate;

    private MarketAggregater mAggregater = new MarketAggregater();

    static {
        INSTANCE = new Market(LocalDate.now());
    }

    /**
     *
     * @param date
     * @since 1.0
     */
    private Market(LocalDate date) {
        mDate = date;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public MarketAggregater aggregater() {
        return mAggregater;
    }

    /**
     * @return
     * @since 1.0
     */
    public Stream<Entity> entities() {
        return mAggregater.entities().stream();
    }

    /**
     * @param clazz
     * @return
     * @since 1.0
     */
    public <T extends Entity> Stream<T> entities(Class<T> clazz) {
        return mAggregater.entities().stream().filter(clazz::isInstance).map(clazz::cast);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public LocalDate nowDate() {
        return mDate;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public Stream<Employable> employables() {
        return entities(Employable.class);
    }

    /**
     *
     * @param n
     * @return
     * @since 1.0
     */
    public Market nextDay(int n) {
        IntStream.range(0, n).forEach(i -> nextDay());
        return this;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public Market nextDay() {
        mDate = mDate.plusDays(1);
        entities().forEach(e -> e.closeEndOfDay(mDate));
        if (mDate.getDayOfMonth() == mDate.lengthOfMonth()) {
            entities().forEach(Entity::closeEndOfMonth);
        }
        return this;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public Market nextEndOfMonth() {
        if (nowDate().lengthOfMonth() == nowDate().getDayOfMonth()) {
            nextDay();
        }
        int countToEndOfMonth = nowDate().lengthOfMonth() - nowDate().getDayOfMonth();
        nextDay(countToEndOfMonth);
        return this;
    }

    /**
     * @since 1.0
     */
    public void clear() {
        mDate = LocalDate.now();
        CentralBank.INSTANCE.clear();
        Nation.INSTANCE.clear();
        mAggregater.clear();
    }
}
