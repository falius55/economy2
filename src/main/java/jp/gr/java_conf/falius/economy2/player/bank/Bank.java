package jp.gr.java_conf.falius.economy2.player.bank;

import java.util.Set;

import jp.gr.java_conf.falius.economy2.account.NationAccount;
import jp.gr.java_conf.falius.economy2.book.BankBooks;
import jp.gr.java_conf.falius.economy2.loan.Bond;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.Employable;

/**
 * 銀行を表すインターフェースです。
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Bank extends Employable {

    public BankBooks<? extends Enum<?>> books();

    /**
     * お金を預かる
     * @since 1.0
     */
    public void keep(AccountOpenable accountOpenable, int amount);

    /**
     * お金を払い出す
     * @since 1.0
     */
    public void paidOut(AccountOpenable accountOpenable, int amount);

    /**
     * 債券市場を物色します。
     * @param bondMarket
     * @return 成約した債権
     * @since 1.0
     */
    public Set<Bond> searchBonds(Set<Bond> bondMarket);

    /**
     * 国の口座に振り込みます。
     * @param target
     * @param amount
     * @return
     * @since 1.0
     */
    public int transfer(NationAccount target, int amount);

}
