package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.player.bank.Bank;

public interface AccountOpenable extends Entity {

    public Bank mainBank();

    public int cash();

    public int deposit();

    /**
     * 貯金します
     * 対象はメインバンク
     * 銀行が実行すると中央銀行に預けます
     */
    public AccountOpenable saveMoney(int amount);

    /**
     * お金をおろします
     * 対象はメインバンク
     * 銀行が実行すると中央銀行からおろします
     */
    public AccountOpenable downMoney(int amount);

}
