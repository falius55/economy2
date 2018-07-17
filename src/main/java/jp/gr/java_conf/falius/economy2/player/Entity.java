package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.account.Account;

/**
 * 経済主体を表すインターフェースです。
 * @author "ymiyauchi"
 *
 */
public interface Entity {

    public Account<? extends Enum<?>> accountBook();

    /**
     * 借金を返済する
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
