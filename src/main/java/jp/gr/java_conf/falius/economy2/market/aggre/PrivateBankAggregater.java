package jp.gr.java_conf.falius.economy2.market.aggre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankTitle;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
class PrivateBankAggregater {
    private Collection<PrivateBank> mBanks = new ArrayList<>();

    /**
     * @since 1.0
     */
    PrivateBankAggregater() {
    }

    Collection<PrivateBank> collection() {
        return Collections.unmodifiableCollection(mBanks);
    }

    /**
     *
     * @param privateBank
     * @since 1.0
     */
    void add(PrivateBank privateBank) {
        mBanks.add(privateBank);
    }

    /**
     * 預金総額
     * @return
     * @since 1.0
     */
    int deposits() {
        return mBanks.stream()
                .mapToInt(pb -> pb.books().get(PrivateBankTitle.DEPOSIT))
                .sum();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    int cashAndAccounts() {
        return mBanks.stream()
                .mapToInt(pb -> pb.cash() + pb.deposit())
                .sum();
    }

    /**
     * 民間保有国債総額
     * @return
     * @since 1.0
     */
    int governmentBonds() {
        return mBanks.stream()
                .map(PrivateBank::books)
                .mapToInt(book -> book.get(PrivateBankTitle.GOVERNMENT_BOND))
                .sum();
    }

    /**
     * @since 1.0
     */
    void clear() {
        mBanks.clear();
    }

}
