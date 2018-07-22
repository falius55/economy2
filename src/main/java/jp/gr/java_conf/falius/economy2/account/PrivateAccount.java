package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 * 民間銀行の預金口座
 * WorkerParsonとPrivateBusinessが持てる
 * @author "ymiyauchi"
 *
 */
public class PrivateAccount implements Account {
    private final PrivateBank mBank;
    private final AccountOpenable mOwner;
    private int mAmount = 0;

    public PrivateAccount(PrivateBank bank, AccountOpenable owner) {
        mBank = bank;
        mOwner = owner;
    }

    @Override
    public PrivateBank bank() {
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
        if (target instanceof PrivateAccount) {
            return transfer((PrivateAccount) target, amount);
        } else if (target instanceof NationAccount) {
            return transfer((NationAccount) target, amount);
        }
        throw new IllegalArgumentException();
    }

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
