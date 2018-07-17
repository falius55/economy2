package jp.gr.java_conf.falius.economy2.market;

import java.time.LocalDate;
import java.util.stream.Stream;

import jp.gr.java_conf.falius.economy2.player.Employable;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class Market {

public static final Market INSTANCE;
    private LocalDate mDate;

    static {
        INSTANCE = new Market(LocalDate.now());
    }

    private Market(LocalDate date) {
        mDate = date;
    }

    public LocalDate nowDate() {
        return mDate;
    }

    public Stream<Employable> employables() {
        Stream.Builder<Employable> builder = Stream.builder();
        PrivateBusiness.stream().forEach(pb -> builder.add(pb));
        PrivateBank.stream().forEach(pb -> builder.add(pb));
        builder.add(CentralBank.INSTANCE);
        return builder.build();
    }

    public Market nextDay() {
        mDate = mDate.plusDays(1);
        if (mDate.getDayOfMonth() == mDate.lengthOfMonth()) {
            closeEndOfMonth();
        }
        return this;
    }

    private void closeEndOfMonth() {
        PrivateBusiness.stream().forEach(pb -> pb.update());
        PrivateBusiness.stream().forEach(pb -> pb.closeEndOfMonth());
        PrivateBank.stream().forEach(pb -> pb.closeEndOfMonth());
    }

    public void clear() {
        mDate = LocalDate.now();
        PrivateBusiness.clear();
        PrivateBank.clear();
        CentralBank.INSTANCE.clear();
    }
}
