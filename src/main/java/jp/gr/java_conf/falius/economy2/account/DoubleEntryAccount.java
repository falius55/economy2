package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

/**
 * 複式簿記会計のインターフェース
 */
public interface DoubleEntryAccount<T extends Enum<T> & AccountTitle> extends Account<T> {

}
