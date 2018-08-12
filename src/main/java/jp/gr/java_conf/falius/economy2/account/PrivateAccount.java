package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 * 民間銀行の預金口座
 * WorkerParsonとPrivateBusinessが持てる
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class PrivateAccount implements Account {
    private final PrivateBank mBank;
    private final AccountOpenable mOwner;
    private int mAmount = 0;

    /**
     *
     * @param bank
     * @param owner
     * @since 1.0
     */
    public PrivateAccount(PrivateBank bank, AccountOpenable owner) {
        mBank = bank;
        mOwner = owner;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBank bank() {
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
        if (target instanceof PrivateAccount) {
            return transfer((PrivateAccount) target, amount);
        } else if (target instanceof NationAccount) {
            return transfer((NationAccount) target, amount);
        } else if (target instanceof CentralBank) {
            return transfer((CentralBank) target, amount);
        }
        throw new IllegalArgumentException();
    }

    /**
     * @since 1.0
     */
    @Override
    public int transfer(CentralBank target, int amount) {
        mBank.books().transfer(amount);
        return decrease(amount);
    }


    // WorkerParson(PrivateBankAccount) -> PrivateBusiness(PrivateBankAccount)
    // PrivateBusiness(PrivateBankAccount) -> PrivateBusiness(PrivateBankAccount)
    // PrivateBusiness(PrivateBankAccount) -> Nation(CentralBankAccount)
    // PrivateBank(CentralBankAccount) -> Nation(CentralBankAccount)
    // Nation(CentralBankAccount) -> PrivateBank(CentralBankAccount)
    private int transfer(PrivateAccount target, int amount) {
        mBank.books().transfer(amount);
        target.bank().books().transfered(amount);
        CentralAccount bankAccount = mBank.mainBank().account(mBank);
        CentralAccount targetBankAccount = target.bank().mainBank().account(target.bank());
        bankAccount.transfer(targetBankAccount, amount);
        target.increase(amount);
        return decrease(amount);
    }

    private int transfer(NationAccount target, int amount) {
        mBank.mainBank().books().transferToNationFromPrivateBank(amount);
        mBank.books().transfer(amount);
        CentralAccount bankAccount = mBank.mainBank().account(mBank);
        bankAccount.decrease(amount);
        target.increase(amount);
        return decrease(amount);
    }

    /**
     * @since 1.0
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PrivateAccount").append(System.lineSeparator())
        .append("Bank: ").append(mBank).append(System.lineSeparator())
        .append("Owner: ").append(mOwner).append(System.lineSeparator())
        .append("Amount: ").append(mAmount).append(System.lineSeparator());
        return sb.toString();
    }
}
