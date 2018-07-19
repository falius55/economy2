package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.enumpack.CentralBankAccountTitle;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

public class CentralBankAggregater {
    private CentralBank mBank = CentralBank.INSTANCE;

    CentralBankAggregater() {}

    public int salaries() {
        return mBank.accountBook().get(CentralBankAccountTitle.SALARIES_EXPENSE);
    }

}
