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
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.PrivateEntity;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class Nation implements Government, AccountOpenable {
    public static final Nation INSTANCE;

    private final GovernmentAccount mAccount = GovernmentAccount.newInstance();
    /**
     * 未成約の国債
     */
    private final Set<Bond> mBondMarket = new HashSet<>();
    /**
     * 成約済みの国債
     */
    private final Set<Bond> mBonds = new HashSet<>();

    static {
        INSTANCE = new Nation();
    }

    private Nation() {
    }

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

    public Set<Bond> bonds() {
        return mBonds;
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
        mBonds.stream()
                .filter(bond -> !bond.isPayOff())
                .sorted((b1, b2) -> b1.deadLine().compareTo(b2.deadLine()))
                .forEach(new Consumer<Bond>() {
                    private int mAmount = amount;

                    @Override
                    public void accept(Bond bond) {
                        if (bond.amount() > mAmount) {
                            return;
                        }
                        if (bond.redeemed()) {
                            mAmount -= bond.amount();
                        }
                    }
                });
        return this;
    }

    /**
     * 債券市場の国債を引き受けさせる。
     * 市中の銀行は財務状況に応じて引き受け、中央銀行は市場に残っているすべての国債を引き受けます。
     * @param bank
     */
    public void makeUnderwriteBonds(Bank bank) {
        Set<Bond> successed = bank.searchBonds(mBondMarket);
        mBondMarket.removeAll(successed);
        mBonds.addAll(successed);

        if (bank instanceof PrivateEntity) {
            int amount = successed.stream().mapToInt(Bond::amount).sum();
            mainBank().transfered(amount);
        }
    }

    /**
     * 国債発行を公示する
     */
    public void advertiseBonds() {
        PrivateBank.stream().forEach(this::makeUnderwriteBonds);
    }

    @Override
    public Government collectTaxes() {
        Market.INSTANCE.employables().forEach(ep -> ep.payIncomeTax(mAccount));
        PrivateBusiness.stream().forEach(pb -> pb.payConsumptionTax(mAccount));
        return this;
    }

    @Override
    public Government order(Product product) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public AccountOpenable saveMoney(int amount) {
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
        mBondMarket.clear();
        mBonds.clear();
    }

}
