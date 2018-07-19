package jp.gr.java_conf.falius.economy2.player;

import jp.gr.java_conf.falius.economy2.account.GovernmentAccount;

public interface Employable extends Entity {

    /**
     * 求人の有無を返します。
     * @return
     */
    public boolean isRecruit();

    /**
     * 労働者が所属しているかどうか
     * @param worker
     * @return
     */
    public boolean has(Worker worker);

    /**
     * 人を雇用します
     */
    public Employable employ(Worker worker);

    /**
     * 社員を解雇します
     */
    public Employable fire(Worker worker);

    /**
     * 給与を支払います
     * @param worker 支払対象社員
     * @return 計算された給与額
     */
    public int paySalary(Worker worker);

    public Employable payIncomeTax(GovernmentAccount nationAccount);

}
