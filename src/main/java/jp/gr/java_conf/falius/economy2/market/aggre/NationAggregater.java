package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

public class NationAggregater {
    final Nation mNation = Nation.INSTANCE;
    private final Account<GovernmentAccountTitle> mAccount = Nation.INSTANCE.accountBook();

    NationAggregater() {}

    /**
     * 政府所得(純間接税) = 間接税 - 補助金
     * @return
     */
    public int pureIncome() {
        return mAccount.get(GovernmentAccountTitle.CONSUMPTION_TAX)
                - mAccount.get(GovernmentAccountTitle.SUBSIDY);
    }

    public int salaries() {
        return mAccount.get(GovernmentAccountTitle.SALARIES_EXPENSE);
    }

    public int cashAndDeposits() {
        return mNation.cash() + mNation.deposit();
    }

    /**
     * 国債残高
     * @return
     */
    public int bonds() {
        return mAccount.get(GovernmentAccountTitle.GOVERNMENT_BOND);
    }

    public int G() {
        return mAccount.get(AccountType.EXPENSE);
    }
}
