package jp.gr.java_conf.falius.economy2.enumpack;

public enum GovernmentTitle implements Title {
    /** 減価償却費(費用) @since 1.0 */ DEPRECIATION("減価償却費", TitleType.EXPENSE),
    /**  給料費用(費用) @since 1.0  */ SALARIES_EXPENSE("給料費", TitleType.EXPENSE),
    /** 支払利息(費用) @since 1.0  */ INTEREST_EXPENSE("支払利息", TitleType.EXPENSE),
    /** 補助金(費用) @since 1.0  */ SUBSIDY("補助金", TitleType.EXPENSE),

    /** 受取利息(収益) @since 1.0  */ RECEIVE_INTEREST("受取利息", TitleType.REVENUE),
    /** 所得税収入(収益) @since 1.0  */ INCOME_TAX("所得税", TitleType.REVENUE),
    /** 消費税収入(収益) @since 1.0  */ CONSUMPTION_TAX("消費税", TitleType.REVENUE),

    /** 保有現金(資産) @since 1.0  */ CASH("保有現金", TitleType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする @since 1.0 */ ACCUMULATED_DEPRECIATION("減価償却累計額", TitleType.ASSETS),
    /** 土地(資産) @since 1.0  */ LAND("土地", TitleType.ASSETS),
    /** 貸付金(資産) @since 1.0  */ LOANS_RECEIVABLE("貸付金", TitleType.ASSETS),
    /** 前払費用(資産) @since 1.0  */ PREEPAID_EXPENSE("前払い費用", TitleType.ASSETS),
    /** 有形固定資産(資産) @since 1.0  */ TANGIBLE_ASSETS("有形固定資産", TitleType.ASSETS),
    /** 政府預金(資産) @since 1.0  */ DEPOSIT("政府預金", TitleType.ASSETS),
    /** 建物(資産) @since 1.0  */ BUILDINGS("建物", TitleType.ASSETS),
    /** システム(資産) @since 1.0 */ SYSTEM("システム", TitleType.ASSETS),
    /** 固定資産仮勘定(資産) @since 1.0 */ FIXEDASSET_SUSPENSE_ACCOUNT("固定資産仮勘定", TitleType.ASSETS),

    /** 公債(負債) @since 1.0  */ GOVERNMENT_BOND("公債発行残高", TitleType.LIABILITIES),
    /** 預かり金(負債) @since 1.0  */ DEPOSITS_RECEIVED("預かり金", TitleType.LIABILITIES),

    /** 資本金(資本) @since 1.0  */ CAPITAL_STOCK("資本金", TitleType.EQUITY);

    private final String mName;
    private final TitleType mType;

    /**
     *
     * @param name
     * @param type
     * @since 1.0
     */
    GovernmentTitle(String name, TitleType type) {
        mName = name;
        mType = type;
    }

    /**
     * @since 1.0
     */
    public TitleType type() {
        return mType;
    }

    /**
     * @since 1.0
     */
    @Override
    public String toString() {
        return mName;
    }
}
