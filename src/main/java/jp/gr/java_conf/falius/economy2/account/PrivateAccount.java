package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface PrivateAccount<T extends Enum<T> & AccountTitle> extends Account<T> {

    /**
     * 納税処理を行います
     * 公的機関ではサポートされません
     * @throws UnssuportedOperationException 公的機関の会計で実行された場合
     */
    public Account<T> payTax(int amount);

}
