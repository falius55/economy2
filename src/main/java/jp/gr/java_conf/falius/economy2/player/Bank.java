package jp.gr.java_conf.falius.economy2.player;

/**
 * 銀行を表すインターフェースです。
 * @author "ymiyauchi"
 *
 */
public interface Bank extends Organization {

    /**
     * お金を預かる
     */
    void keep(int money);

    /**
     * お金を払い出す
     */
    void paidOut(int money);

}
