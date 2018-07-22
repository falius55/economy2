package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;

public interface Account {

    public Bank bank();

    public int amount();

    public int transfer(Account target, int amount);

    public int transfer(CentralBank central, int amount);

    public int increase(int amount);

    public int decrease(int amount);
}
