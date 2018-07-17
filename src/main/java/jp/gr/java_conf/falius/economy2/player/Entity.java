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
     * 月末処理を行います。
     */
    public void closeEndOfMonth();
}
