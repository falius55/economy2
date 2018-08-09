package jp.gr.java_conf.falius.economy2.enumpack;

import java.util.HashMap;
import java.util.Map;

/**
 * 民間企業の勘定科目
 * @since 1.0
 */
public enum PrivateBusinessTitle implements Title {
    /** 雑費(費用) @since 1.0 */ MISCELLANEOUS_EXPENSE("雑費", TitleType.EXPENSE),
    /** 減価償却費(費用) @since 1.0  */ DEPRECIATION("減価償却費", TitleType.EXPENSE),
    /** 仕入費用(費用) @since 1.0  */ PURCHESES("仕入費", TitleType.EXPENSE),
    /** 支払家賃(費用) @since 1.0  */ RENT_EXPENSE("支払家賃", TitleType.EXPENSE),
    /**  給料費用(費用) @since 1.0  */ SALARIES_EXPENSE("給料費", TitleType.EXPENSE),
    /** 消耗品費(費用) @since 1.0  */ SUPPLIES_EXPENSE("消耗品費", TitleType.EXPENSE),
    /** 支払利息(費用) @since 1.0  */ INTEREST_EXPENSE("支払利息", TitleType.EXPENSE),
    /** 租税公課(費用) @since 1.0  */ TAX("租税公課", TitleType.EXPENSE),

    /** 売上高(収益) @since 1.0  */ SALES("売上高", TitleType.REVENUE),
    /** 未収収益(収益) @since 1.0  */ ACCRUED_REVENUE("未収収益", TitleType.REVENUE),
    /** 受取利息(収益) @since 1.0  */ RECEIVE_INTEREST("受取利息", TitleType.REVENUE),

    /** 小口現金(資産) @since 1.0  */ CASH("小口現金", TitleType.ASSETS),
    /** 売掛金(資産) @since 1.0  */ RECEIVABLE("売掛金", TitleType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする @since 1.0 */ ACCUMULATED_DEPRECIATION("減価償却累計額", TitleType.ASSETS),
    /** 土地(資産) @since 1.0  */ LAND("土地", TitleType.ASSETS),
    /** 貸付金(資産) @since 1.0  */ LOANS_RECEIVABLE("貸付金", TitleType.ASSETS),
    /** 商品(資産) @since 1.0  */ MERCHANDISE("商品", TitleType.ASSETS),
    /** 前払費用(資産) @since 1.0  */ PREEPAID_EXPENSE("前払費用", TitleType.ASSETS),
    /** 有形固定資産(資産) @since 1.0  */ TANGIBLE_ASSETS("有形固定資産", TitleType.ASSETS),
    /** 当座預金(資産) @since 1.0  */ CHECKING_ACCOUNTS("当座預金", TitleType.ASSETS),
    /** 建物(資産) @since 1.0  */ BUILDINGS("建物", TitleType.ASSETS),

    /** 買掛金(負債) @since 1.0  */ PAYABLE("買掛金", TitleType.LIABILITIES),
    /** 未払費用(負債) @since 1.0  */ ACCRUED_EXPENSE("未払費用", TitleType.LIABILITIES),
    /** 借入金(負債) @since 1.0  */ LOANS_PAYABLE("借入金", TitleType.LIABILITIES),
    /** 預かり金(負債) @since 1.0  */ DEPOSITS_RECEIVED("預かり金", TitleType.LIABILITIES),
    /** 未払消費税(負債) @since 1.0  */ ACCRUED_CONSUMPTION_TAX("未払消費税", TitleType.LIABILITIES),

    /** 資本金(資本) @since 1.0  */ CAPITAL_STOCK("資本金", TitleType.EQUITY);

    private final TitleType mType;
    private final String mName;

    private static final Map<String, PrivateBusinessTitle> sStringToEnum = new HashMap<>(); // 日本語名から業種enumへのマップ
    static {
        for (PrivateBusinessTitle title : values())
            sStringToEnum.put(title.toString(), title);
    }

    /**
     *
     * @param name
     * @param type
     * @since 1.0
     */
    PrivateBusinessTitle(String name, TitleType type) {
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

    /**
     * @since 1.0
     */
    public static PrivateBusinessTitle fromString(String name) {
        return sStringToEnum.get(name);
    }

}
