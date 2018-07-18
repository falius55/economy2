package jp.gr.java_conf.falius.economy2.enumpack;

public enum GovernmentAccountTitle implements AccountTitle {
    /** 減価償却費(費用) */ DEPRECIATION("減価償却費", AccountType.EXPENSE),
    /**  給料費用(費用) */ SALARIES_EXPENSE("給料費", AccountType.EXPENSE),
    /** 支払利息(費用) */ INTEREST_EXPENSE("支払利息", AccountType.EXPENSE),
    /** 補助金(費用) */ SUBSIDY("補助金", AccountType.EXPENSE),

    /** 受取利息(収益) */ RECEIVE_INTEREST("受取利息", AccountType.REVENUE),
    /** 租税収入(収益) */ TAX("租税", AccountType.REVENUE),

    /** 保有現金(資産) */ CASH("保有現金", AccountType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする */ ACCUMULATED_DEPRECIATION("減価償却累計額", AccountType.ASSETS),
    /** 土地(資産) */ LAND("土地", AccountType.ASSETS),
    /** 貸付金(資産) */ LOANS_RECEIVABLE("貸付金", AccountType.ASSETS),
    /** 前払費用(資産) */ PREEPAID_EXPENSE("前払い費用", AccountType.ASSETS),
    /** 有形固定資産(資産) */ TANGIBLE_ASSETS("有形固定資産", AccountType.ASSETS),
    /** 政府預金(資産) */ DEPOSIT("当座預金", AccountType.ASSETS),
    /** 建物(資産) */ BUILDINGS("建物", AccountType.ASSETS),

    /** 公債(負債) */ GOVERNMENT_BOND("公債発行残高", AccountType.LIABILITIES),

    /** 資本金(資本) */ CAPITAL_STOCK("資本金", AccountType.EQUITY);

    private final String mName;
    private final AccountType mType;

    GovernmentAccountTitle(String name, AccountType type) {
        mName = name;
        mType = type;
    }

    public AccountType type() {
        return mType;
    }

    @Override
    public String toString() {
        return mName;
    }
}
