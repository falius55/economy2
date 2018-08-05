package jp.gr.java_conf.falius.economy2.stockmanager;

import java.util.OptionalInt;
import java.util.Set;

import jp.gr.java_conf.falius.economy2.agreement.Deferment;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface StockManager {

    /**
     * 出荷可能かどうかを返します
     * @since 1.0
     */
    public boolean canShipOut(int num);

    /**
     * 出荷します
     * @return 原価。失敗すると空のOptionalInt
     * @since 1.0
     */
    public OptionalInt shipOut(int num);

    /**
     * 仕入費用を集計します
     * @return 仕入に要した費用
     * @since 1.0
     */
    public Set<Deferment> purchasePayable();

    /**
     * 現在の商品原価総額を返します。
     * @return
     * @since 1.0
     */
    public int stockCost();

    /**
     * @since 1.0
     */
    public void update();
}
