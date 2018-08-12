package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;

/**
 *
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public interface PrivateBooks<T extends Enum<T> & Title> extends Books<T> {

}
