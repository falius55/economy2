package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface PrivateBooks<T extends Enum<T> & AccountTitle> extends Books<T> {

}
