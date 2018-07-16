package jp.gr.java_conf.falius.economy2.player;

/**
 * 銀行を表すインターフェースです。
 * @author "ymiyauchi"
 *
 */
public interface Bank extends Employable {

    /**
     * お金を預かる
     */
    public void keep(int amount);

    /**
     * お金を払い出す
     */
    public void paidOut(int amount);

    public void transfer(int amount);

    public void transfered(int amount);

}
