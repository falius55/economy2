package jp.gr.java_conf.falius.economy2.market;

import java.time.LocalDate;

public class MarketInfomation {

public static MarketInfomation INSTANCE;
    private LocalDate mDate;

    static {
        INSTANCE = new MarketInfomation(LocalDate.now());
    }

    private MarketInfomation(LocalDate date) {
        mDate = date;
    }

    public MarketInfomation nextDay() {
        mDate = mDate.plusDays(1);
        return this;
    }

    public LocalDate nowDate() {
        return mDate;
    }
}
