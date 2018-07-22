package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Worker extends Parson, AccountOpenable {

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean hasJob();

    /**
     * @since 1.0
     */
    public PrivateBank mainBank();

    /**
     * 給与を受け取ります。
     * @param amount
     * @since 1.0
     */
    public void getSalary(Employable from, int amount);

    /**
     * 求職活動をします。
     * @return
     * @since 1.0
     */
    public boolean seekJob();

    /**
     * 退職します。
     * @since 1.0
     */
    public void retireJob();
}
