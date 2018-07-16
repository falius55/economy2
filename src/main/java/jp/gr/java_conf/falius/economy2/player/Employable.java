package jp.gr.java_conf.falius.economy2.player;

public interface Employable extends Entity {

    /**
     * 求人の有無を返します。
     * @return
     */
    public boolean isRecruit();

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

    /**
     * 労働者が所属しているかどうか
     * @param worker
     * @return
     */
    public boolean has(Worker worker);

}
