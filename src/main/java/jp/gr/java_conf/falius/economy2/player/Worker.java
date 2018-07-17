package jp.gr.java_conf.falius.economy2.player;

public interface Worker extends Parson {

    public boolean hasJob();

    /**
     * 給与を受け取ります。
     * @param amount
     */
    public void getSalary(int amount);

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
