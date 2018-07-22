package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.book.Books;

/**
 * 経済主体を表すインターフェースです。
 * @author "ymiyauchi"
 *
 */
public interface Entity {

    public Books<? extends Enum<?>> books();

    /**
     * 月末処理を行います。
     */
    public void closeEndOfMonth();
}
