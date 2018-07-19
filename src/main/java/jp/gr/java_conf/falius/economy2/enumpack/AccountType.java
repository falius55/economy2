package jp.gr.java_conf.falius.economy2.enumpack;

/**
 * 科目種別(費用、収益、資産、負債、資本)
 */
public enum AccountType {
    /** 費用 */ EXPENSE("費用", RL.LEFT),
    /** 収益 */ REVENUE("収益", RL.RIGHT),
    /** 資産 */ ASSETS("資産", RL.LEFT),
    /** 負債 */ LIABILITIES("負債", RL.RIGHT),
    /** 資本 */ EQUITY("資本", RL.RIGHT);

    private final String mName;
    private final RL rl; // 貸借対照表、損益計算書で左右どちらに表記されるか(借方科目か貸方科目か)

    AccountType(String name, RL rl) {
        mName = name;
        this.rl = rl;
    }

    public RL rl() {
        return this.rl;
    }

    @Override
    public String toString() {
        return mName;
    }

    /**
     * 貸借を表す列挙型
     */
    public enum RL {
        RIGHT,LEFT;
        public RL inverse() {
            switch (this) {
                case RIGHT: return RL.LEFT;
                case LEFT: return RL.RIGHT;
                default: throw new AssertionError("Unknown rl: "+ this);
            }
        }
    }

}
