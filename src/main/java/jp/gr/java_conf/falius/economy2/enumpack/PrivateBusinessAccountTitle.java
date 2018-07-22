package jp.gr.java_conf.falius.economy2.enumpack;

import java.util.HashMap;
import java.util.Map;

/**
 * 民間企業の勘定科目
 * @since 1.0
 */
public enum PrivateBusinessAccountTitle implements AccountTitle {
    /** 雑費(費用) @since 1.0 */ MISCELLANEOUS_EXPENSE("雑費", AccountType.EXPENSE),
    /** 減価償却費(費用) @since 1.0  */ DEPRECIATION("減価償却費", AccountType.EXPENSE),
    /** 仕入費用(費用) @since 1.0  */ PURCHESES("仕入費", AccountType.EXPENSE),
    /** 支払家賃(費用) @since 1.0  */ RENT_EXPENSE("支払家賃", AccountType.EXPENSE),
    /**  給料費用(費用) @since 1.0  */ SALARIES_EXPENSE("給料費", AccountType.EXPENSE),
    /** 消耗品費(費用) @since 1.0  */ SUPPLIES_EXPENSE("消耗品費", AccountType.EXPENSE),
    /** 支払利息(費用) @since 1.0  */ INTEREST_EXPENSE("支払利息", AccountType.EXPENSE),
    /** 租税公課(費用) @since 1.0  */ TAX("租税公課", AccountType.EXPENSE),

    /** 売上高(収益) @since 1.0  */ SALES("売上高", AccountType.REVENUE),
    /** 未収収益(収益) @since 1.0  */ ACCRUED_REVENUE("未収収益", AccountType.REVENUE),
    /** 受取利息(収益) @since 1.0  */ RECEIVE_INTEREST("受取利息", AccountType.REVENUE),

    /** 小口現金(資産) @since 1.0  */ CASH("小口現金", AccountType.ASSETS),
    /** 売掛金(資産) @since 1.0  */ RECEIVABLE("売掛金", AccountType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする @since 1.0 */ ACCUMULATED_DEPRECIATION("減価償却累計額", AccountType.ASSETS),
    /** 土地(資産) @since 1.0  */ LAND("土地", AccountType.ASSETS),
    /** 貸付金(資産) @since 1.0  */ LOANS_RECEIVABLE("貸付金", AccountType.ASSETS),
    /** 商品(資産) @since 1.0  */ MERCHANDISE("商品", AccountType.ASSETS),
    /** 前払費用(資産) @since 1.0  */ PREEPAID_EXPENSE("前払費用", AccountType.ASSETS),
    /** 有形固定資産(資産) @since 1.0  */ TANGIBLE_ASSETS("有形固定資産", AccountType.ASSETS),
    /** 当座預金(資産) @since 1.0  */ CHECKING_ACCOUNTS("当座預金", AccountType.ASSETS),
    /** 建物(資産) @since 1.0  */ BUILDINGS("建物", AccountType.ASSETS),

    /** 買掛金(負債) @since 1.0  */ PAYABLE("買掛金", AccountType.LIABILITIES),
    /** 未払費用(負債) @since 1.0  */ ACCRUED_EXPENSE("未払費用", AccountType.LIABILITIES),
    /** 借入金(負債) @since 1.0  */ LOANS_PAYABLE("借入金", AccountType.LIABILITIES),
    /** 預かり金(負債) @since 1.0  */ DEPOSITS_RECEIVED("預かり金", AccountType.LIABILITIES),
    /** 未払消費税(負債) @since 1.0  */ ACCRUED_CONSUMPTION_TAX("未払消費税", AccountType.LIABILITIES),

    /** 資本金(資本) @since 1.0  */ CAPITAL_STOCK("資本金", AccountType.EQUITY);

    private final AccountType mType;
    private final String mName;

    private static final Map<String, PrivateBusinessAccountTitle> sStringToEnum = new HashMap<>(); // 日本語名から業種enumへのマップ
    static {
        for (PrivateBusinessAccountTitle title : values())
            sStringToEnum.put(title.toString(), title);
    }

    /**
     *
     * @param name
     * @param type
     * @since 1.0
     */
    PrivateBusinessAccountTitle(String name, AccountType type) {
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

    /**
     * @since 1.0
     */
    public static PrivateBusinessAccountTitle fromString(String name) {
        return sStringToEnum.get(name);
    }

}
