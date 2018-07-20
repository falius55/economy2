package jp.gr.java_conf.falius.economy2.market.aggre;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.falius.economy2.enumpack.PrivateBankAccountTitle;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class PrivateBankAggregater {
    private List<PrivateBank> mBanks = new ArrayList<>();

    PrivateBankAggregater() {
    }

    public void add(PrivateBank privateBank) {
        mBanks.add(privateBank);
    }

    /**
     * 預金総額
     * @return
     */
    public int deposits() {
        return mBanks.stream()
                .mapToInt(pb -> pb.accountBook().get(PrivateBankAccountTitle.DEPOSIT))
                .sum();
    }

    public int cashAndAccounts() {
        return mBanks.stream()
                .mapToInt(pb -> pb.cash() + pb.deposit())
                .sum();
    }

    /**
     * 民間保有国債総額
     * @return
     */
    public int governmentBonds() {
        return mBanks.stream()
                .map(PrivateBank::accountBook)
                .mapToInt(book -> book.get(PrivateBankAccountTitle.GOVERNMENT_BOND))
                .sum();
    }

    public void clear() {
        mBanks.clear();
    }

}
