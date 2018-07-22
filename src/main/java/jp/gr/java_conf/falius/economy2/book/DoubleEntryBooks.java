package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

/**
 * 複式簿記会計のインターフェース
 * @since 1.0
 */
public interface DoubleEntryBooks<T extends Enum<T> & AccountTitle> extends Books<T> {

    /**
     * 借方に記帳します。
     * @param item
     * @param amount
     * @since 1.0
     */
    public void addLeft(T item, int amount);

    /**
     * 貸方に記帳します。
     * @param item
     * @param amount
     * @since 1.0
     */
    public void addRight(T item, int amount);

}
