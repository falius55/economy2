package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public interface Worker extends Parson, AccountOpenable {

    public boolean hasJob();

    public PrivateBank mainBank();

    /**
     * 給与を受け取ります。
     * @param amount
     */
    public void getSalary(Employable from, int amount);

    /**
     * 求職活動をします。
     * @return
     */
    public boolean seekJob();

    /**
     * 退職します。
     * @return
     */
    public void retireJob();
}
