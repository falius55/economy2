package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 * 日銀当座預金
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class CentralAccount implements Account {
    private final CentralBank mBank;
    private final PrivateBank mOwner;
    private int mAmount = 0;

    /**
     *
     * @param bank
     * @param owner
     * @since 1.0
     */
    public CentralAccount(CentralBank bank, PrivateBank owner) {
        mBank = bank;
        mOwner = owner;
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
        if (target instanceof NationAccount) {
            return transfer((NationAccount) target, amount);
        } else if (target instanceof PrivateAccount) {
            return transfer((PrivateAccount) target, amount);
        } else if (target instanceof CentralAccount) {
            return transfer((CentralAccount) target, amount);
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
    private int transfer(NationAccount target, int amount) {
        target.bank().books().transferToNationFromPrivateBank(amount);
        target.increase(amount);
        return decrease(amount);
    }

    // lend - borrow
    private int transfer(PrivateAccount target, int amount) {
        target.bank().books().transfered(amount);
        target.increase(amount);
        PrivateBank targetBank = target.bank();
        CentralBank.INSTANCE.account(targetBank).increase(amount);
        return decrease(amount);
    }

    @Override
    public int transfer(CentralBank central, int amount) {
        return decrease(amount);
    }

    private int transfer(CentralAccount target, int amount) {
        target.increase(amount);
        return decrease(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CentralAccount").append(System.lineSeparator())
        .append("Bank: ").append(mBank).append(System.lineSeparator())
        .append("Owner: ").append(mOwner).append(System.lineSeparator())
        .append("Amount: ").append(mAmount).append(System.lineSeparator());
        return sb.toString();
    }
}
