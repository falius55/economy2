package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

public class CentralBankAggregater {
    private CentralBank mBank = CentralBank.INSTANCE;

    CentralBankAggregater() {}

    public int salaries() {
        return mBank.accountBook().get(CentralBankAccountTitle.SALARIES_EXPENSE);
    }

    /**
     * 中央銀行引き受けの国債残高
     * @return
     */
    public int governmentBonds() {
        return mBank.accountBook().get(CentralBankAccountTitle.GOVERNMENT_BOND);
    }

    /**
     * 発行銀行券
     * @return
     */
    public int bankNotes() {
        return mBank.accountBook().get(CentralBankAccountTitle.BANK_NOTE);
    }

}
