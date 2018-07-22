package jp.gr.java_conf.falius.economy2.stockmanager;

import java.util.OptionalInt;
import java.util.Set;

import jp.gr.java_conf.falius.economy2.loan.Deferment;

public interface StockManager {

    /**
     * 出荷可能かどうかを返します
     */
    public boolean canShipOut(int num);

    /**
     * 出荷します
     * @return 原価。失敗すると空のOptionalInt
     */
    public OptionalInt shipOut(int num);

    /**
     * 仕入費用を集計します
     * @return 仕入に要した費用
     */
    public Set<Deferment> purchasePayable();

    /**
     * 現在の商品原価総額を返します。
     * @return
     */
    public int stockCost();

    public void update();
}
