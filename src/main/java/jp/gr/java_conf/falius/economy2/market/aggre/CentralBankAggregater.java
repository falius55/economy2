package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankTitle;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
class CentralBankAggregater {
    private CentralBank mBank;

    /**
     * @since 1.0
     */
    CentralBankAggregater(CentralBank bank) {
        mBank = bank;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    int salaries() {
        return mBank.books().get(CentralBankTitle.SALARIES_EXPENSE);
    }

    /**
     * 中央銀行引き受けの国債残高
     * @return
     * @since 1.0
     */
    int governmentBonds() {
        return mBank.books().get(CentralBankTitle.GOVERNMENT_BOND);
    }

    /**
     * 発行銀行券
     * @return
     * @since 1.0
     */
    int bankNotes() {
        return mBank.books().get(CentralBankTitle.BANK_NOTE);
    }

}
