package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

/**
 * 政府預金口座
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class NationAccount implements Account {
    private final CentralBank mBank;
    private int mAmount = 0;

    /**
     *
     * @param bank
     * @since 1.0
     */
    public NationAccount(CentralBank bank) {
        mBank = bank;
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBank bank() {
        return mBank;
    }

    /**
     * @since 1.0
     */
    @Override
    public int amount() {
        return mAmount;
    }

    /**
     * @since 1.0
     */
    @Override
    public int increase(int amount) {
        mAmount += amount;
        return mAmount;
    }

    /**
     * @since 1.0
     */
    @Override
    public int decrease(int amount) {
        mAmount -= amount;
        return mAmount;
    }

    /**
     * @since 1.0
     */
    @Override
    public int transfer(Transferable target, int amount) {
        if (target instanceof CentralAccount) {
            return transfer((CentralAccount) target, amount);
        } else if (target instanceof PrivateAccount) {
            return transfer((PrivateAccount) target, amount);
        } else if (target instanceof CentralBank) {
            return transfer((CentralBank) target, amount);
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
    /**
     *
     * @param target
     * @param amount
     * @return
     * @since 1.0
     */
    public int transfer(CentralAccount target, int amount) {
        target.bank().books().transferToPrivateBankFromNation(amount);
        target.increase(amount);
        return decrease(amount);
    }

    /**
     *
     * @param target
     * @param amount
     * @return
     * @since 1.0
     */
    public int transfer(PrivateAccount target, int amount) {
        target.increase(amount);
        return decrease(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public int transfer(CentralBank central, int amount) {
        return decrease(amount);
    }

    /**
     * @since 1.0
     */
    public void clear() {
        mAmount = 0;
    }
}
