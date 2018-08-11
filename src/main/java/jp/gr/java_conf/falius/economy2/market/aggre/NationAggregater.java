package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentTitle;
import jp.gr.java_conf.falius.economy2.enumpack.TitleType;
import jp.gr.java_conf.falius.economy2.player.gorv.Nation;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
class NationAggregater {
    private final Nation mNation;
    private final GovernmentBooks mBooks;

    /**
     * @since 1.0
     */
    NationAggregater(Nation nation) {
        mNation = nation;
        mBooks = nation.books();
    }

    /**
     * 政府所得(純間接税) = 間接税 - 補助金
     * @return
     * @since 1.0
     */
    int pureIncome() {
        return mBooks.get(GovernmentTitle.CONSUMPTION_TAX)
                - mBooks.get(GovernmentTitle.SUBSIDY);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    int salaries() {
        return mBooks.get(GovernmentTitle.SALARIES_EXPENSE);
    }

    int depreciation() {
        return mBooks.get(GovernmentTitle.DEPRECIATION);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    int cashAndDeposits() {
        return mNation.cash() + mNation.deposit();
    }

    /**
     * 国債残高
     * @return
     * @since 1.0
     */
    int bonds() {
        return mBooks.get(GovernmentTitle.GOVERNMENT_BOND);
    }

    /**
     *
     * @return
     * @since 1.0
     */
    int G() {
        return mBooks.get(TitleType.EXPENSE) + mBooks.fixedAssetsValue()
                + mBooks.get(GovernmentTitle.FIXEDASSET_SUSPENSE_ACCOUNT);
    }
}
