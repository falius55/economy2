package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;

/**
 *
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface MutableBooks<T extends Enum<T> & Title>  extends Books<T> {

    /**
     * 引数の会計を、自分の会計に吸収併合する。結婚、合併など
     * @since 1.0
     */
    public Books<T> merge(Books<T> another);

}
