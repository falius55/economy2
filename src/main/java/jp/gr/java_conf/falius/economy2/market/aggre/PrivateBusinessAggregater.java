package jp.gr.java_conf.falius.economy2.market.aggre;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.falius.economy2.book.Books;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class PrivateBusinessAggregater {
    private final List<PrivateBusiness> mPrivateBusinesses = new ArrayList<>();

    /**
     * @since 1.0
     */
    PrivateBusinessAggregater() {
    }

    /**
     *
     * @param privateBusiness
     * @since 1.0
     */
    public void add(PrivateBusiness privateBusiness) {
        mPrivateBusinesses.add(privateBusiness);
    }

    /**
     * 付加価値総額
     * @return
     * @since 1.0
     */
    public int addedValue() {
        mPrivateBusinesses.stream()
                .forEach(PrivateBusiness::update); // 仕入れ費をすべて計上しなければ付加価値が水増しされてしまう
        int allSales = mPrivateBusinesses.stream()
                .mapToInt(pb -> pb.books().get(PrivateBusinessTitle.SALES))
                .sum();
        int allPurchase = mPrivateBusinesses.stream()
                .mapToInt(pb -> pb.books().get(PrivateBusinessTitle.PURCHESES))
                .sum();
        int stock = mPrivateBusinesses.stream()
                .mapToInt(PrivateBusiness::stockCost)
                .sum();
        allSales += stock; // 在庫品は企業が買ったとみなす
        return allSales - allPurchase;
    }

    /**
     * 固定資本減耗
     * @return
     * @since 1.0
     */
    public int depreciation() {
        return mPrivateBusinesses.stream()
                .map(PrivateBusiness::books)
                .mapToInt(book -> book.get(PrivateBusinessTitle.ACCUMULATED_DEPRECIATION))
                .sum();
    }

    /**
     * 営業余剰
     * @return
     * @since 1.0
     */
    public int benefits() {
        mPrivateBusinesses.stream()
                .forEach(PrivateBusiness::update); // 仕入れ費をすべて計上しなければ利益が水増しされてしまう
        int stock = mPrivateBusinesses.stream()
                .mapToInt(PrivateBusiness::stockCost)
                .sum();
        int benefitOfBooks = mPrivateBusinesses.stream()
                .map(PrivateBusiness::books)
                .mapToInt(Books::benefit)
                .sum();
        return benefitOfBooks + stock; // 在庫は企業が買ったとみなすので、収益に含まれる。
    }

    /**
     * 未払い消費税
     * @return
     * @since 1.0
     */
    public int accruedConsumptionTax() {
        return mPrivateBusinesses.stream()
                .map(PrivateBusiness::books)
                .mapToInt(book -> book.get(PrivateBusinessTitle.ACCRUED_CONSUMPTION_TAX))
                .sum();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int cashAndDeposits() {
        return mPrivateBusinesses.stream()
                .mapToInt(pb -> pb.cash() + pb.deposit())
                .sum();
    }

    /**
     * 在庫品増加(投資)
     * @return
     * @since 1.0
     */
    public int I() {
        return mPrivateBusinesses.stream()
                .mapToInt(PrivateBusiness::stockCost)
                .sum(); // - 初頭の在庫額
    }

    /**
     * @since 1.0
     */
    public void clear() {
        mPrivateBusinesses.clear();
    }

}
