package jp.gr.java_conf.falius.economy2.player;

public interface Borrowable extends Entity {

    public boolean borrow(int amount);

    /**
     * 借金を返済します
     */
    public void repay(int amount);

}
