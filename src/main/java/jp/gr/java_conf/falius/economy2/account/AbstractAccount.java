package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

/**
 * 会計を表すすべてのクラスの基底クラス
 * @param T 勘定科目一覧を定義した列挙型。AccountTitleインターフェースを実装していなければならない
 */
public abstract class AbstractAccount<T extends Enum<T> & AccountTitle> implements Account<T> {

    /**
     * 引数の会計を、自分の会計に吸収併合します。結婚、合併など
     */
    @Override
    public AbstractAccount<T> merge(Account<T> another) {
        if (!(another instanceof AbstractAccount)) { throw new IllegalArgumentException(); }
        for (T item : items()) {
            increase(item, another.get(item));
        }
        return this;
    }
    // 特定科目の金額を単純に増加する
    abstract protected void increase(T item, int mount);
}
