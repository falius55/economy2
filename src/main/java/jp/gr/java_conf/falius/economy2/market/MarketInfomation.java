package jp.gr.java_conf.falius.economy2.market;

import java.time.LocalDate;

import jp.gr.java_conf.falius.economy2.player.PrivateBank;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

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
        if (mDate.getDayOfMonth() == mDate.lengthOfMonth()) {
            closeEndOfMonth();
        }
        return this;
    }

    public LocalDate nowDate() {
        return mDate;
    }

    private void closeEndOfMonth() {
        PrivateBusiness.stream().forEach(pb -> pb.update());
        PrivateBusiness.stream().forEach(pb -> pb.closeEndOfMonth());
        PrivateBank.stream().forEach(pb -> pb.closeEndOfMonth());
    }
}
