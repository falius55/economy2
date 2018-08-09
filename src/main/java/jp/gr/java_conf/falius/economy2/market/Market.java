package jp.gr.java_conf.falius.economy2.market;

import java.time.LocalDate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.market.aggre.MarketAggregater;
import jp.gr.java_conf.falius.economy2.player.Employable;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;
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
        Stream.Builder<Employable> builder = Stream.builder();
        PrivateBusiness.stream().forEach(pb -> builder.add(pb));
        PrivateBank.stream().forEach(pb -> builder.add(pb));
        builder.add(CentralBank.INSTANCE);
        return builder.build();
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
        if (mDate.getDayOfMonth() == mDate.lengthOfMonth()) {
            closeEndOfMonth();
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
    private void closeEndOfMonth() {
        PrivateBusiness.stream().forEach(PrivateBusiness::update);
        PrivateBusiness.stream().forEach(PrivateBusiness::closeEndOfMonth);
        PrivateBank.stream().forEach(PrivateBank::closeEndOfMonth);
        Nation.INSTANCE.closeEndOfMonth();
    }

    /**
     * @since 1.0
     */
    public void clear() {
        mDate = LocalDate.now();
        WorkerParson.clear();
        PrivateBusiness.clear();
        PrivateBank.clear();
        CentralBank.INSTANCE.clear();
        Nation.INSTANCE.clear();
        mAggregater.clear();
    }
}
