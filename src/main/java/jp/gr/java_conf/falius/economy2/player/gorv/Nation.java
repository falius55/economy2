package jp.gr.java_conf.falius.economy2.player.gorv;

import java.time.Period;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.GovernmentAccount;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.loan.Bond;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.PrivateEntity;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class Nation implements Government, AccountOpenable {
    public static final Nation INSTANCE;

    private final GovernmentAccount mAccount = GovernmentAccount.newInstance();
    private final Set<Bond> mBondMarket = new HashSet<>();
    private final Set<Bond> mBonds = new HashSet<>();

    static {
        INSTANCE = new Nation();
    }

    private Nation() {}

    @Override
    public Account<GovernmentAccountTitle> accountBook() {
        return mAccount;
    }

    @Override
    public CentralBank mainBank() {
        return CentralBank.INSTANCE;
    }

    @Override
    public int cash() {
        return mAccount.get(GovernmentAccountTitle.CASH);
    }

    @Override
    public int deposit() {
        return mAccount.get(GovernmentAccountTitle.DEPOSIT);
    }

    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public Government issueBonds(int amount) {
        Bond bond = new Bond(mAccount, amount, Period.ofYears(1));
        mBondMarket.add(bond);
        return this;
    }

    @Override
    public Government redeemBonds(int amount) {
        mBonds.stream().sorted((b1, b2) -> b1.deadLine().compareTo(b2.deadLine()))
        .forEach(new Consumer<Bond>() {
            private int mAmount = amount;
            @Override
            public void accept(Bond bond) {
                if (bond.isPayOff()) { return; }
                if (bond.redeemed(mAmount)) {
                    mAmount -= bond.amount();
                }
            }
        });
        return this;
    }

    /**
     * 債券市場の国債をすべて中央銀行に引き受けさせる。
     */
    public void makeCentralBankUnderwriteBond() {
        Set<Bond> successed = CentralBank.INSTANCE.searchBonds(mBondMarket);
        mBondMarket.removeAll(successed);
        mBonds.addAll(successed);
    }

    /**
     * 国債発行を公示する
     */
    public void advertiseBonds() {
        Set<Bond> newBonds = PrivateBank.stream().map(pb -> pb.searchBonds(mBondMarket))
        .reduce(new HashSet<Bond>(), (collector, successed) -> {collector.addAll(successed); return collector;});
        mBondMarket.removeAll(newBonds);
        mBonds.addAll(newBonds);

        int amount = newBonds.stream().mapToInt(bond -> bond.amount()).sum();
        mainBank().transfered(amount);
    }

    @Override
    public Government collectTaxes(PrivateEntity entity) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Government order(Product product) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public  AccountOpenable saveMoney(int amount) {
        mAccount.saveMoney(amount);
        mainBank().keepByNation(amount);
        return this;
    }

    @Override
    public AccountOpenable downMoney(int amount) {
        mAccount.downMoney(amount);
        mainBank().paidOutByNation(amount);
        return this;
    }

    public void clear() {
        mAccount.clearBook();
    }

}
