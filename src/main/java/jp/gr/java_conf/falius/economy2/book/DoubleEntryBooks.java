package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

/**
 * 複式簿記会計のインターフェース
 */
public interface DoubleEntryBooks<T extends Enum<T> & AccountTitle> extends Books<T> {

    public void addLeft(T item, int amount);

    public void addRight(T item, int amount);

}
