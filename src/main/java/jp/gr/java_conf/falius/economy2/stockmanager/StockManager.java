package jp.gr.java_conf.falius.economy2.stockmanager;

import java.time.LocalDate;
import java.util.OptionalInt;

public interface StockManager {

    /**
     * 出荷可能かどうかを返します
     */
    boolean canShipOut(int lot);
    /**
     * 出荷します
     * @return 原価。失敗すると空のOptionalInt
     */
    OptionalInt shipOut(int lot);

    /**
     * 仕入費用を集計します
     * @param date 集計日
     * @return 仕入に要した費用
     */
    int computePurchaseExpense(LocalDate date);
}
