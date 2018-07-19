package jp.gr.java_conf.falius.economy2.player.bank;

import java.util.Set;

import jp.gr.java_conf.falius.economy2.loan.Bond;
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
     * 債券市場を物色します。
     * @param bondMarket
     * @return 成約した債権
     */
    public Set<Bond> searchBonds(Set<Bond> bondMarket);

}
