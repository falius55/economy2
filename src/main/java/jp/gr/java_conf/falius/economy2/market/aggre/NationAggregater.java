package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.book.Books;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

public class NationAggregater {
    final Nation mNation = Nation.INSTANCE;
    private final Books<GovernmentAccountTitle> mBooks = Nation.INSTANCE.books();

    NationAggregater() {}

    /**
     * 政府所得(純間接税) = 間接税 - 補助金
     * @return
     */
    public int pureIncome() {
        return mBooks.get(GovernmentAccountTitle.CONSUMPTION_TAX)
                - mBooks.get(GovernmentAccountTitle.SUBSIDY);
    }

    public int salaries() {
        return mBooks.get(GovernmentAccountTitle.SALARIES_EXPENSE);
    }

    public int cashAndDeposits() {
        return mNation.cash() + mNation.deposit();
    }

    /**
     * 国債残高
     * @return
     */
    public int bonds() {
        return mBooks.get(GovernmentAccountTitle.GOVERNMENT_BOND);
    }

    public int G() {
        return mBooks.get(AccountType.EXPENSE);
    }
}
