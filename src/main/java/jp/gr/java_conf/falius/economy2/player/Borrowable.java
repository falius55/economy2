package jp.gr.java_conf.falius.economy2.player;

public interface Borrowable extends Entity {

    public void borrow(int amount);

    /**
     * 借金を返済します
     */
    public void repay(int amount);

}
