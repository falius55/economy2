package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Employable extends Entity {

    /**
     * 求人の有無を返します。
     * @return
 * @since 1.0
     */
    public boolean isRecruit();

    /**
     * 労働者が所属しているかどうか
     * @param worker
     * @return
 * @since 1.0
     */
    public boolean has(Worker worker);

    /**
     * 人を雇用します
 * @since 1.0
     */
    public Employable employ(Worker worker);

    /**
     * 社員を解雇します
 * @since 1.0
     */
    public Employable fire(Worker worker);

    /**
     * 給与を支払います
     * @param worker 支払対象社員
     * @return 計算された給与額
 * @since 1.0
     */
    public int paySalary(Worker worker);

    /**
     *
     * @param workerAccount
     * @param amount
     * @return
     * @since 1.0
     */
    public int transfer(PrivateAccount workerAccount, int amount);

    /**
     *
     * @param nationBooks
     * @return
     * @since 1.0
     */
    public Employable payIncomeTax(GovernmentBooks nationBooks);

}
