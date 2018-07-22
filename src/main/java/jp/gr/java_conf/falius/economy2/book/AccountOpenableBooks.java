package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

/**
 *
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface AccountOpenableBooks<T extends Enum<T> & AccountTitle>  extends Books<T>  {

    /**
     * お金を口座に預けたときの処理
     * @param amount
     * @return
     * @since 1.0
     */
    public AccountOpenableBooks<T> saveMoney(int amount);

    /**
     * お金を口座から下ろしたときの処理
     * @param amount
     * @return
     * @since 1.0
     */
    public AccountOpenableBooks<T> downMoney(int amount);
}
