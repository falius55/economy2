package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.Title;
import jp.gr.java_conf.falius.economy2.enumpack.TitleType;

/**
 * 会計帳簿を表すインターフェース
 * @param T 科目一覧の列挙型
 * @since 1.0
 */
public interface Books<T extends Enum<T> & Title> {

    /**
     * 指定した科目種別の総額を集計します
     * @param type 科目種別
     * @return 集計結果
     * @since 1.0
     */
    public int get(TitleType type);

    /**
     * 指定した勘定科目の金額を返します
     * @param item 勘定科目
     * @return 指定した勘定科目の金額
     * @since 1.0
     */
    public int get(T item);

    /**
     * 剰余金を計算する
     * @return
     * @since 1.0
     */
    public int benefit();

}
