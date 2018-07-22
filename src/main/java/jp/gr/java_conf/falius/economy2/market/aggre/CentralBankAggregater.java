package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class CentralBankAggregater {
    private CentralBank mBank = CentralBank.INSTANCE;

    /**
     * @since 1.0
     */
    CentralBankAggregater() {}

    /**
     *
     * @return
     * @since 1.0
     */
    public int salaries() {
        return mBank.books().get(CentralBankAccountTitle.SALARIES_EXPENSE);
    }

    /**
     * 中央銀行引き受けの国債残高
     * @return
     * @since 1.0
     */
    public int governmentBonds() {
        return mBank.books().get(CentralBankAccountTitle.GOVERNMENT_BOND);
    }

    /**
     * 発行銀行券
     * @return
     * @since 1.0
     */
    public int bankNotes() {
        return mBank.books().get(CentralBankAccountTitle.BANK_NOTE);
    }

}
