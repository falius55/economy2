package jp.gr.java_conf.falius.economy2.player;

import java.time.LocalDate;

import jp.gr.java_conf.falius.economy2.book.Books;

/**
 * 経済主体を表すインターフェースです。
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Entity {

    /**
     *
     * @return
     * @since 1.0
     */
    public Books<? extends Enum<?>> books();

    /**
     * 日課処理を行います。
     * @since 1.0
     */
    public void closeEndOfDay(LocalDate date);

    /**
     * 月末処理を行います。
     * @since 1.0
     */
    public void closeEndOfMonth();
}
