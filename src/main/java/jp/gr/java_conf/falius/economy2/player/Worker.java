package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.book.WorkerParsonBooks;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Worker extends Parson, AccountOpenable {

    @Override
    public WorkerParsonBooks books();

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
