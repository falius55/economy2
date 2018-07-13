package jp.gr.java_conf.falius.economy2.player;

public interface Organization extends Entity {

    /**
     * 人を雇用します
     */
    Organization employ(Parson parson);

    /**
     * 社員を解雇します
     */
    Organization fire(Parson parson);

    /**
     * 給与を支払います
     * @param parson 支払対象社員
     * @return 計算された給与額
     */
    int paySalary(Parson parson);

}
