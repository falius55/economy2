package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;

/**
 *
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface LendableBooks<T extends Enum<T> & Title>  extends Books<T>  {

    /**
     * 貸金処理を行う
     * @since 1.0
     */
    public LendableBooks<T> lend(int amount);

    /**
     * 返済を受けた時の処理を行います
     * @since 1.0
     */
    public LendableBooks<T> repaid(int amount);

}
