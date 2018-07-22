package jp.gr.java_conf.falius.economy2.book;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;

public interface BankBooks<T extends Enum<T> & AccountTitle> extends EmployableBooks<T> {

    /**
     * お金を預かる
     */
    public BankBooks<T> keep(int amount);

    /**
     * 預金返済処理
     */
    public BankBooks<T> paidOut(int amount);

    /**
     * 国債を引き受けます。
     * @param amount
     * @return
     */
    public BankBooks<T> acceptGovernmentBond(int amount);

    /**
     * 保有国債が償還されます。
     * @param amount
     * @return
     */
    public BankBooks<T> redeemedGovernmentBond(int amount);

    public BankBooks<T> buyGorvementBond(int amount);

    public BankBooks<T> sellGovernmentBond(int amount);

    public BankBooks<T> createAccount(AccountOpenable accountOpenable);
}
