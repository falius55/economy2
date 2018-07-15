package jp.gr.java_conf.falius.economy2.enumpack;

public enum PrivateBankAccountTitle implements AccountTitle {
    /** 減価償却費(費用) */ DEPRECIATION(AccountType.EXPENSE),
    /**  給料費用(費用) */ SALARIES_EXPENSE(AccountType.EXPENSE),
    /** 支払利息(費用) */ INTEREST_EXPENSE(AccountType.EXPENSE),

    /** 未収収益(収益) */ ACCRUED_REVENUE(AccountType.REVENUE),
    /** 受取利息(収益) */ RECEIVE_INTEREST(AccountType.REVENUE),

    /** 保有現金(資産) */ CASH(AccountType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする */ ACCUMULATED_DEPRECIATION(AccountType.ASSETS),
    /** 土地(資産) */ LAND(AccountType.ASSETS),
    /** 貸付金(資産) */ LOANS_RECEIVABLE(AccountType.ASSETS),
    /** 前払費用(資産) */ PREEPAID_EXPENSE(AccountType.ASSETS),
    /** 有形固定資産(資産) */ TANGIBLE_ASSETS(AccountType.ASSETS),
    /** 中央銀行の当座預金(資産) */ CHECKING_ACCOUNTS(AccountType.ASSETS),
    /** 建物(資産) */ BUILDINGS(AccountType.ASSETS),

    /** 借入金(負債) */ LOANS_PAYABLE(AccountType.LIABILITIES),
    /** 預金(負債) */ DEPOSIT(AccountType.LIABILITIES),

    /** 資本金(資本) */ CAPITAL_STOCK(AccountType.EQUITY);

    private final AccountType mType;
    private static final PrivateBankAccountTitle mDefaultItem = CHECKING_ACCOUNTS;

    PrivateBankAccountTitle(AccountType type) {
        mType = type;
    }

    public AccountType type() {
        return mType;
    }
    public static PrivateBankAccountTitle defaultItem() {
        return mDefaultItem;
    }
}
