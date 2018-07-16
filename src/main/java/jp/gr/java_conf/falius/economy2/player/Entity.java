package jp.gr.java_conf.falius.economy2.player;

/**
 * 経済主体を表すインターフェースです。
 * @author "ymiyauchi"
 *
 */
public interface Entity {

    /**
     * 貯金する
     * 対象はメインバンク
     * 銀行が実行すると中央銀行に預ける
     * 中央銀行ではサポートされません
     */
    public Entity saveMoney(int amount);

    /**
     * お金をおろす
     * 対象はメインバンク
     * 銀行が実行すると中央銀行からおろす
     * 中央銀行ではサポートされません
     */
    public Entity downMoney(int amount);

    /**
     * 借金を返済する
     * 中央銀行が実行すると、お金が市場から消える
     */
    public void repay(int amount);

    /**
     * 返済を受ける
     */
    public void repaid(int amount);

    /**
     * 納税します
     * 公的機関ではサポートされません
     */
    public void payTax(int amount);

    /**
     * 月末処理を行います。
     */
    public void closeEndOfMonth();
}
