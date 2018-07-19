package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface PrivateAccount<T extends Enum<T> & AccountTitle> extends Account<T> {

}
