package jp.gr.java_conf.falius.economy2.player.bank;

import jp.gr.java_conf.falius.economy2.player.Employable;

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

    /**
     * 国債を引き受けます。
     * @param amount
     * @return
     */
    public Bank acceptGovernmentBond(int amount);

    /**
     * 保有国債が償還されます。
     * @param amount
     * @return
     */
    public Bank redeemedGovernmentBond(int amount);

}
