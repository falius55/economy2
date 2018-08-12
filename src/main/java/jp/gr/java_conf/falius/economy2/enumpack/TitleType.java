package jp.gr.java_conf.falius.economy2.enumpack;

/**
 * 科目種別(費用、収益、資産、負債、資本)
 * @since 1.0
 */
public enum TitleType {
    /** 費用 @since 1.0 */ EXPENSE("費用", RL.LEFT),
    /** 収益 @since 1.0 */ REVENUE("収益", RL.RIGHT),
    /** 資産 @since 1.0 */ ASSETS("資産", RL.LEFT),
    /** 負債 @since 1.0 */ LIABILITIES("負債", RL.RIGHT),
    /** 資本 @since 1.0 */ EQUITY("資本", RL.RIGHT);

    private final String mName;
    private final RL rl; // 貸借対照表、損益計算書で左右どちらに表記されるか(借方科目か貸方科目か)

    /**
     *
     * @param name
     * @param rl
     * @since 1.0
     */
    TitleType(String name, RL rl) {
        mName = name;
        this.rl = rl;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public RL rl() {
        return this.rl;
    }

    /**
     * @since 1.0
     */
    @Override
    public String toString() {
        return mName;
    }

    /**
     * 貸借を表す列挙型
     * @since 1.0
     */
    public enum RL {
        RIGHT,LEFT;

        /**
         *
         * @return
         * @since 1.0
         */
        public RL inverse() {
            switch (this) {
                case RIGHT: return RL.LEFT;
                case LEFT: return RL.RIGHT;
                default: throw new AssertionError("Unknown rl: "+ this);
            }
        }
    }

}
