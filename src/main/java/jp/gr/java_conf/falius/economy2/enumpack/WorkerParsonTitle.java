package jp.gr.java_conf.falius.economy2.enumpack;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0n
 *
 */
public enum WorkerParsonTitle implements Title {
    /** 食費 @since 1.0 */ FOOD_EXPENSE("食費", TitleType.EXPENSE),
    /** 雑費(費用) @since 1.0  */ MISCELLANEOUS_EXPENSE("雑費", TitleType.EXPENSE),
    /** 減価償却費(費用) @since 1.0  */ DEPRECIATION("減価償却費", TitleType.EXPENSE),
    /** 支払家賃(費用) @since 1.0  */ RENT_EXPENSE("支払家賃", TitleType.EXPENSE),
    /** 消耗品費(費用) @since 1.0  */ SUPPLIES_EXPENSE("消耗品費", TitleType.EXPENSE),
    /** 支払利息(費用) @since 1.0  */ INTEREST_EXPENSE("支払利息", TitleType.EXPENSE),
    /** 会社設立費(費用) @since 1.0  */ ESTABLISH_EXPENSES("創業費", TitleType.EXPENSE),
    /** 所得税(費用) @since 1.0  */ TAX("所得税", TitleType.EXPENSE),
    /** 最終消費(費用) @since 1.0  */ CONSUMPTION("最終消費", TitleType.EXPENSE),

    /** 給料 @since 1.0 */ SALARIES("給与", TitleType.REVENUE),
    /** 受取利息(収益) @since 1.0  */ RECEIVE_INTEREST("受取利息", TitleType.REVENUE),

    /** 現金(資産) @since 1.0  */ CASH("現金", TitleType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする @since 1.0 */ ACCUMULATED_DEPRECIATION("減価償却累計額", TitleType.ASSETS),
    /** 土地(資産) @since 1.0  */ LAND("土地", TitleType.ASSETS),
    /** 貸付金(資産) @since 1.0  */ LOANS_RECEIVABLE("貸付金", TitleType.ASSETS),
    /** 前払費用(資産) @since 1.0  */ PREEPAID_EXPENSE("前払費用", TitleType.ASSETS),
    /** 有形固定資産(資産) @since 1.0  */ TANGIBLE_ASSETS("有形固定資産", TitleType.ASSETS),
    /** 普通預金(資産) @since 1.0  */ ORDINARY_DEPOSIT("普通預金", TitleType.ASSETS),
    /** 建物(資産) @since 1.0  */ BUILDINGS("建物", TitleType.ASSETS),

    /** 未払費用(負債) @since 1.0  */ ACCRUED_EXPENSE("未払費用", TitleType.LIABILITIES),
    /** 借入金(負債) @since 1.0  */ LOANS_PAYABLE("借入金", TitleType.LIABILITIES),

    /** 開始残高(資本) @since 1.0  */ OPENING_BALANCE("開始残高", TitleType.EQUITY);

    private final String mName;
    private final TitleType mType;
    private static final Map<Product, WorkerParsonTitle> sProductToTitle;

    static {
        sProductToTitle = new EnumMap<Product, WorkerParsonTitle>(Product.class) {
            {
                put(Product.RICE_BALL, FOOD_EXPENSE);
            }
        };
    }

    /**
     *
     * @param name
     * @param type
     * @since 1.0
     */
    WorkerParsonTitle(String name, TitleType type) {
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
     * 製品を購入した際に利用する費用・資産科目を返します。
     * @param product
     * @return 労働者が購入するような製品でない場合は空のOptional
     * @since 1.0
     */
    public static Optional<WorkerParsonTitle> titleFrom(Product product) {
        return Optional.ofNullable(CONSUMPTION);
//        return Optional.ofNullable(sProductToTitle.get(product));
    }

}
