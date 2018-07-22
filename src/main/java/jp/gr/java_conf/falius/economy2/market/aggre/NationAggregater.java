package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.book.Books;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class NationAggregater {
    final Nation mNation = Nation.INSTANCE;
    private final Books<GovernmentAccountTitle> mBooks = Nation.INSTANCE.books();

    /**
     * @since 1.0
     */
    NationAggregater() {}

    /**
     * 政府所得(純間接税) = 間接税 - 補助金
     * @return
     * @since 1.0
     */
    public int pureIncome() {
        return mBooks.get(GovernmentAccountTitle.CONSUMPTION_TAX)
                - mBooks.get(GovernmentAccountTitle.SUBSIDY);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int salaries() {
        return mBooks.get(GovernmentAccountTitle.SALARIES_EXPENSE);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int cashAndDeposits() {
        return mNation.cash() + mNation.deposit();
    }

    /**
     * 国債残高
     * @return
     * @since 1.0
     */
    public int bonds() {
        return mBooks.get(GovernmentAccountTitle.GOVERNMENT_BOND);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int G() {
        return mBooks.get(AccountType.EXPENSE);
    }
}
