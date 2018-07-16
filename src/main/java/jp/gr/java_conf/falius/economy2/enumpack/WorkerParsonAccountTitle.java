package jp.gr.java_conf.falius.economy2.enumpack;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public enum WorkerParsonAccountTitle implements AccountTitle {
    /** 食費 */ FOOD_EXPENSE("食費", AccountType.EXPENSE),
    /** 雑費(費用) */ MISCELLANEOUS_EXPENSE("雑費", AccountType.EXPENSE),
    /** 減価償却費(費用) */ DEPRECIATION("減価償却費", AccountType.EXPENSE),
    /** 支払家賃(費用) */ RENT_EXPENSE("支払家賃", AccountType.EXPENSE),
    /** 消耗品費(費用) */ SUPPLIES_EXPENSE("消耗品費", AccountType.EXPENSE),
    /** 支払利息(費用) */ INTEREST_EXPENSE("支払利息", AccountType.EXPENSE),
    /** 会社設立費(費用) */ ESTABLISH_EXPENSES("創業費", AccountType.EXPENSE),

    /** 給料 */ SALARIES("給与", AccountType.REVENUE),
    /** 受取利息(収益) */ RECEIVE_INTEREST("受取利息", AccountType.REVENUE),

    /** 現金(資産) */ CASH("現金", AccountType.ASSETS),
    /** 減価償却累計額(資産) 貸方にaddする */ ACCUMULATED_DEPRECIATION("減価償却累計額", AccountType.ASSETS),
    /** 土地(資産) */ LAND("土地", AccountType.ASSETS),
    /** 貸付金(資産) */ LOANS_RECEIVABLE("貸付金", AccountType.ASSETS),
    /** 前払費用(資産) */ PREEPAID_EXPENSE("前払費用", AccountType.ASSETS),
    /** 有形固定資産(資産) */ TANGIBLE_ASSETS("有形固定資産", AccountType.ASSETS),
    /** 普通預金(資産) */ ORDINARY_DEPOSIT("普通預金", AccountType.ASSETS),
    /** 建物(資産) */ BUILDINGS("建物", AccountType.ASSETS),

    /** 未払費用(負債) */ ACCRUED_EXPENSE("未払費用", AccountType.LIABILITIES),
    /** 借入金(負債) */ LOANS_PAYABLE("借入金", AccountType.LIABILITIES),

    /** 開始残高(資本) */ OPENING_BALANCE("開始残高", AccountType.EQUITY);

    private final String mName;
    private final AccountType mType;
    private static final WorkerParsonAccountTitle sDefaultItem = CASH;
    private static final Map<Product, WorkerParsonAccountTitle> sProductToTitle;

    static {
        sProductToTitle = new EnumMap<Product, WorkerParsonAccountTitle>(Product.class) {
            {
                put(Product.RICE_BALL, FOOD_EXPENSE);
            }
        };
    }

    WorkerParsonAccountTitle(String name, AccountType type) {
        mName = name;
        mType = type;
    }

    public AccountType type() {
        return mType;
    }
    public static WorkerParsonAccountTitle defaultItem() {
        return sDefaultItem;
    }

    @Override
    public String toString() {
        return mName;
    }

    /**
     * 製品を購入した際に利用する費用・資産科目を返します。
     * @param product
     * @return 労働者が購入するような製品でない場合は空のOptional
     */
    public static Optional<WorkerParsonAccountTitle> titleFrom(Product product) {
        return Optional.ofNullable(sProductToTitle.get(product));
    }

}
