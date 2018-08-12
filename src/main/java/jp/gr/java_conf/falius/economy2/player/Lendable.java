package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.agreement.Loan;
import jp.gr.java_conf.falius.economy2.book.LendableBooks;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Lendable extends Entity {

    /**
     * @since 1.0
     */
    public LendableBooks<?> books();

    /**
     * @since 1.0
     */
    public boolean canLend(int amount);

    /**
     * @since 1.0
     */
    public int acceptDebt(Loan debt);
}
