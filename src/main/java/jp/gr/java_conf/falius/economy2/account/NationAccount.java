package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

/**
 * 政府預金口座
 * @author "ymiyauchi"
 *
 */
public class NationAccount implements Account {
    private final CentralBank mBank;
    private int mAmount = 0;

    public NationAccount(CentralBank bank) {
        mBank = bank;
    }

    @Override
    public CentralBank bank() {
        return mBank;
    }

    @Override
    public int amount() {
        return mAmount;
    }

    @Override
    public int increase(int amount) {
        mAmount += amount;
        return mAmount;
    }

    @Override
    public int decrease(int amount) {
        mAmount -= amount;
        return mAmount;
    }

    @Override
    public int transfer(Account target, int amount) {
        if (target instanceof CentralAccount) {
            return transfer((CentralAccount) target, amount);
        } else if (target instanceof PrivateAccount) {
            return transfer((PrivateAccount) target, amount);
        }
        throw new IllegalArgumentException();
    }

    // WorkerParson(PrivateBankAccount) -> PrivateBusiness(PrivateBankAccount)
    // PrivateBusiness(PrivateBankAccount) -> PrivateBusiness(PrivateBankAccount)
    // PrivateBusiness(PrivateBankAccount) -> Nation(CentralBankAccount)
    // PrivateBank(CentralBankAccount) -> Nation(CentralBankAccount)
    // Nation(CentralBankAccount) -> PrivateBank(CentralBankAccount)
    /**
     * 政府が民間銀行の日銀当座預金に振り込み
     * @param target
     * @param amount
     * @return
     */
    public int transfer(CentralAccount target, int amount) {
        target.bank().books().transferToPrivateBankFromNation(amount);
        target.increase(amount);
        return decrease(amount);
    }

    public int transfer(PrivateAccount target, int amount) {
        target.increase(amount);
        return decrease(amount);
    }

    @Override
    public int transfer(CentralBank central, int amount) {
        return decrease(amount);
    }

    public void clear() {
        mAmount = 0;
    }
}
