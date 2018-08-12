package jp.gr.java_conf.falius.economy2.account;

public interface Transferable {

    /**
     *
     * @param target
     * @param amount
     * @return
     * @since 1.0
     */
    public int transfer(Transferable target, int amount);

}
