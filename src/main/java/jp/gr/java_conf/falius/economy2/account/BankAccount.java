package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;

public interface BankAccount<T extends Enum<T> & AccountTitle> extends EmployableAccount<T> {

    /**
     * お金を預かる
     */
    public BankAccount<T> keep(int amount);

    /**
     * 預金返済処理
     */
    public BankAccount<T> paidOut(int amount);

    /**
     * 振り込みを受ける
     * 中央銀行の場合は、政府への支払い
     * @param amount
     */
    public BankAccount<T> transfered(int amount);

    public BankAccount<T> transfer(int amount);

    /**
     * 国債を引き受けます。
     * @param amount
     * @return
     */
    public BankAccount<T> acceptGovernmentBond(int amount);

    /**
     * 保有国債が償還されます。
     * @param amount
     * @return
     */
    public BankAccount<T> redeemedGovernmentBond(int amount);

    public BankAccount<T> buyGorvementBond(int amount);

    public BankAccount<T> sellGovernmentBond(int amount);
}
