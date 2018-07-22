package jp.gr.java_conf.falius.economy2.enumpack;

public enum PrivateBankAccountTitle implements AccountTitle {
    /** 減価償却費(費用) @since 1.0 */ DEPRECIATION("減価償却費", AccountType.EXPENSE),
    /**  給料費用(費用) @since 1.0  */ SALARIES_EXPENSE("給料費", AccountType.EXPENSE),
    /** 支払利息(費用) @since 1.0  */ INTEREST_EXPENSE("支払利息", AccountType.EXPENSE),

    /** 受取利息(収益) @since 1.0  */ RECEIVE_INTEREST("受取利息", AccountType.REVENUE),

    /** 保有現金(資産) @since 1.0  */ CASH("保有現金", AccountType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする @since 1.0 */ ACCUMULATED_DEPRECIATION("減価償却累計額", AccountType.ASSETS),
    /** 土地(資産) @since 1.0  */ LAND("土地", AccountType.ASSETS),
    /** 貸付金(資産) @since 1.0  */ LOANS_RECEIVABLE("貸付金", AccountType.ASSETS),
    /** 未収収益(資産) @since 1.0  */ ACCRUED_REVENUE("未収収益", AccountType.ASSETS),
    /** 前払費用(資産) @since 1.0  */ PREEPAID_EXPENSE("前払い費用", AccountType.ASSETS),
    /** 有形固定資産(資産) @since 1.0  */ TANGIBLE_ASSETS("有形固定資産", AccountType.ASSETS),
    /** 中央銀行の当座預金(資産) @since 1.0  */ CHECKING_ACCOUNTS("日銀当座預金", AccountType.ASSETS),
    /** 建物(資産) @since 1.0  */ BUILDINGS("建物", AccountType.ASSETS),
    /** 国債(資産) @since 1.0  */ GOVERNMENT_BOND("国債", AccountType.ASSETS),

    /** 借入金(負債) @since 1.0  */ LOANS_PAYABLE("借入金", AccountType.LIABILITIES),
    /** 預金(負債) @since 1.0  */ DEPOSIT("預金", AccountType.LIABILITIES),
    /** 預かり金(負債) @since 1.0  */ DEPOSITS_RECEIVED("預かり金", AccountType.LIABILITIES),

    /** 資本金(資本) @since 1.0  */ CAPITAL_STOCK("資本金", AccountType.EQUITY);

    private final String mName;
    private final AccountType mType;

    /**
     *
     * @param name
     * @param type
     * @since 1.0
     */
    PrivateBankAccountTitle(String name, AccountType type) {
        mName = name;
        mType = type;
    }

    /**
     * @since 1.0
     */
    public AccountType type() {
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
