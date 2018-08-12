package jp.gr.java_conf.falius.economy2.market;

import java.time.LocalDate;
import java.time.Period;
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
     * @param start 含む
     * @param end 含まない
     * @return
     */
    public static Stream<LocalDate> dateStream(LocalDate start, LocalDate end) {
        return dateStream(start, end, Period.ofDays(1));
    }

    /**
     * startからend(含まない)までの日付の順次ストリームを返します。
     * startとendが同じ日付の場合、またはstartがendより後の日付の場合は空のストリームが返ります。
     * @param start 含む
     * @param end 含まない
     * @param period
     * @return
     * @since 1.0
     */
    public static Stream<LocalDate> dateStream(LocalDate start, LocalDate end, Period period) {
        Stream.Builder<LocalDate> builder = Stream.builder();
        for (LocalDate next = start; next.isBefore(end); next = next.plus(period)) {
            builder.add(next);
        }
        return builder.build();
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
