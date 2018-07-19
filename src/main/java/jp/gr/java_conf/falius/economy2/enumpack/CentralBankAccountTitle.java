package jp.gr.java_conf.falius.economy2.enumpack;

public enum CentralBankAccountTitle implements AccountTitle {
    /** 減価償却費(費用) */ DEPRECIATION("減価償却費", AccountType.EXPENSE),
    /**  給料費用(費用) */ SALARIES_EXPENSE("給料費", AccountType.EXPENSE),
    /** 支払利息(費用) */ INTEREST_EXPENSE("支払利息", AccountType.EXPENSE),

    /** 受取利息(収益) */ RECEIVE_INTEREST("受取利息", AccountType.REVENUE),

    /** 保有現金(資産) */ CASH("保有現金", AccountType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする */ ACCUMULATED_DEPRECIATION("減価償却累計額", AccountType.ASSETS),
    /** 貸付金(資産) */ LOANS_RECEIVABLE("貸付金", AccountType.ASSETS),
    /** 未収収益(資産) */ ACCRUED_REVENUE("未収収益", AccountType.ASSETS),
    /** 前払費用(資産) */ PREEPAID_EXPENSE("前払い費用", AccountType.ASSETS),
    /** 有形固定資産(資産) */ TANGIBLE_ASSETS("有形固定資産", AccountType.ASSETS),
    /** 国債(資産) */ GOVERNMENT_BOND("国債", AccountType.ASSETS),

    /** 日銀当座預金(負債) */ DEPOSIT("日銀当座預金", AccountType.LIABILITIES),
    /** 発行銀行券(負債) */ BANK_NOTE("発行銀行券", AccountType.LIABILITIES),
    /** 政府預金(負債) */ GOVERNMENT_DEPOSIT("政府預金", AccountType.LIABILITIES),
    /** 預かり金(負債) */ DEPOSITS_RECEIVED("預かり金", AccountType.LIABILITIES),

    /** 資本金(資本) */ CAPITAL_STOCK("資本金", AccountType.EQUITY);

    private final String mName;
    private final AccountType mType;

    CentralBankAccountTitle(String name, AccountType type) {
        mName = name;
        mType = type;
    }

    @Override
    public AccountType type() {
        return mType;
    }

    @Override
    public String toString() {
        return mName;
    }

}
