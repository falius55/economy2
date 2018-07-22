package jp.gr.java_conf.falius.economy2.enumpack;

import java.time.Period;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.falius.util.table.TableBuilder;

/**
 * 製品
 * @since 1.0
 */
public enum Product {
    // Enumのコンストラクタ実行時点でEnum全体の初期化が終わっているわけではないためか、コンストラクタの引数に直接EnumSet.noneOf(Product.class)を渡して
    // コンストラクタ内でメンバー変数に保存しようとするとExceptionInInitializerErrorが起こる(Product.classが不可？)
    // また、その要素より下で宣言されている要素を引数内で使用していると「前方参照が不正です」というコンパイルエラー
    // abstractメソッドで取り出すことにして、オーバーライドしたメソッドに値を直接記述することで回避。要素追加の際もコンストラクタ引数に渡す場合とあまり手間が変わらない
    /**
     * @since 1.0
     */
    LAND("土地", 10000000 /* 円 */, Type.LAND, 100 /* 坪 */, Period.ZERO, 100 /* 坪 */) {
        // 原材料からその利用数量へのマップを作成する
        protected Map<Product, Integer> createMaterialMap() {
            Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
            return ret;
        }
    },
    /**
     * @since 1.0
     */
    WOOD("木材", 600, Type.CONSUMER, 1000 /* g */, Period.ofDays(364), 100000 /* g */) {
        protected Map<Product, Integer> createMaterialMap() {
            Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
            return ret;
        }
    },
    /**
     * @since 1.0
     */
    RICE("米", 800, Type.CONSUMER, 1000 /* g */, Period.ofDays(364), 1000000 /* g */) {
        protected Map<Product, Integer> createMaterialMap() {
            Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
            return ret;
        }
    },
    /**
     * @since 1.0
     */
    PAPER("紙", 300, Type.CONSUMER, 500 /* 枚 */, Period.ofDays(1), 2000 /* 枚 */) {
        protected Map<Product, Integer> createMaterialMap() {
            Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
            ret.put(WOOD, 5 /* g */);
            return ret;
        }
    },
    /**
     * @since 1.0
     */
    BUILDINGS("建物", 5000000, Type.FIXED_ASSET, 45 /* 年 */, 1 /* 棟 */, Period.ofDays(364), 1 /* 棟 */) {
        protected Map<Product, Integer> createMaterialMap() {
            Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
            ret.put(WOOD, 100000 /* g */);
            return ret;
        }
    },
    /**
     * @since 1.0
     */
    NOVEL("小説", 480, Type.CURRENT_ASSET, 1 /* 冊 */, Period.ofDays(1), 10 /* 冊 */) {
        protected Map<Product, Integer> createMaterialMap() {
            Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
            ret.put(PAPER, 500 /* 枚 */);
            return ret;
        }
    },
    /**
     * @since 1.0
     */
    RICE_BALL("おにぎり", 120, Type.CONSUMER, 1 /* 個 */, Period.ofDays(1), 100 /* 個 */) {
        protected Map<Product, Integer> createMaterialMap() {
            Map<Product, Integer> ret = new EnumMap<Product, Integer>(Product.class);
            ret.put(RICE, 100 /* g */);
            return ret;
        }
    };

    private final String mName; // 日本語名
    private final int mPrice; // 値段(ロットあたり)
    private final Type mType; // 資産としての種類
    private final int mServiceLife; // 耐用年数
    private final int mNumOfLot; // 購入単位あたり数量
    private final Period mManufacturePeriod; // 製造期間
    private final int mProductionVolume; // 一度の製造数

    private static final Map<String, Product> sStringToEnum = new HashMap<String, Product>(); // 日本語名から商品enumへのマップ
    private static final Map<Product, Map<Product, Integer>> sMaterials = new EnumMap<Product, Map<Product, Integer>>(
            Product.class); // 原材料から必要数量へのマップ

    static {
        for (Product product : values()) {
            sStringToEnum.put(product.toString(), product);
        }

        for (Product product : values()) {
            sMaterials.put(product, product.createMaterialMap());
        }
    }

    /**
     * @param name 日本語名
     * @param price 値段(１単位あたり)
     * @param type 資産としての種類(消費財、固定資産など)
     * @param numOfLot １単位あたり数量
     * @param manufacturePeriod 製造期間
     * @param puroductionVolume 一度の製造数
     * @since 1.0
     */
    Product(String name, int price, Type type, int numOfLot, Period manufacturePeriod, int productionVolume) {
        this(name, price, type, 0, numOfLot, manufacturePeriod, productionVolume);
        if (type == Type.FIXED_ASSET)
            throw new IllegalArgumentException("arguments has no mServiceLife");
    }

    /**
     * 固定資産に利用するコンストラクタ
     * @param name 日本語名
     * @param price 値段(１単位あたり)
     * @param type 資産としての種類(消費財、固定資産など)
     * @param serviceLife 耐用年数
     * @param numOfLot １単位あたり数量
     * @param manufacturePeriod 製造期間
     * @param productionVolume 一度の製造数
     * @throws IllegalArgumentException typeが固定資産ではない場合
     * @since 1.0
     */
    Product(String name, int price, Type type, int serviceLife, int numOfLot, Period manufacturePeriod,
            int productionVolume) {
        mName = name;
        mPrice = price;
        mType = type;
        mServiceLife = serviceLife;
        mNumOfLot = numOfLot;
        mManufacturePeriod = manufacturePeriod;
        mProductionVolume = productionVolume;

        if (type == Type.FIXED_ASSET && serviceLife == 0)
            throw new IllegalArgumentException();
    }

    /**
     * 日本語名から対象のenumインスタンスを取得します
     * @param name 日本語名
     * @return 対象のenum
     * @since 1.0
     */
    public static Product fromString(String name) {
        return sStringToEnum.get(name);
    }

    /**
     * @return この商品の日本語表現
     * @since 1.0
     */
    @Override
    public String toString() {
        return mName;
    }

    /**
     * @return この商品の値段
     * @since 1.0
     */
    public int price() {
        return mPrice;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int serviceLife() {
        return mServiceLife;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public Period manufacturePeriod() {
        return mManufacturePeriod;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int productionVolume() {
        return mProductionVolume;
    }

    /**
     * この商品を取り扱っている業種の集合を返します
     * @since 1.0
     */
    public Set<Industry> industries() {
        return Industry.selectSet(industry -> industry.hasProduct(this));
    }

    /**
     * 原材料の集合を返します
     * @since 1.0
     */
    public Set<Product> materialSet() {
        Map<Product, Integer> materials = Product.sMaterials.get(this);
        return materials.keySet();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public Map<Product, Integer> materials() {
        // ないとは思うが、もしオーバーライドしたcreateMaterialMap()でEnumMap以外を返すようにした場合にもHashMapで対応する
        Map<Product, Integer> materials = Product.sMaterials.get(this);
        if (materials instanceof EnumMap) {
            return new EnumMap<Product, Integer>((EnumMap<Product, Integer>) materials);
        } else {
            return new HashMap<Product, Integer>(materials);
        }
    }

    /**
     *
     * @return
     * @since 1.0
     */
    abstract protected Map<Product, Integer> createMaterialMap();

    /**
     *
     * @return
     * @since 1.0
     */
    public Type type() {
        return mType;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int numOfLot() {
        return mNumOfLot;
    }

    /**
     *
     * @since 1.0
     */
    public void print() {
        System.out.printf("%s%n", this);
        System.out.printf("値段:%d%n", price());
        System.out.printf("単位あたり数量:%d%n", numOfLot());
        System.out.printf("種別:%s%n", type());
        System.out.printf("耐用年数:%d%n", serviceLife());
        System.out.printf("原材料:%s", materials());
        System.out.printf("取扱業者:%s%n", industries());
        System.out.println("");
    }

    /**
     *
     * @since 1.0
     */
    public static void printAll() {
        // for (Product pd : values()) {
        //  pd.print();
        // }

        TableBuilder tb = new TableBuilder("商品名", "値段", "単位あたり数量", "種別", "耐用年数", "原材料", "取扱業者");
        for (Product pd : values())
            tb.insert(pd)
                    .add("値段", pd.price())
                    .add("単位あたり数量", pd.numOfLot())
                    .add("種別", null)
                    .add("耐用年数", pd.serviceLife())
                    .add("原材料", pd.materials())
                    .add("取扱業者", pd.industries());
        tb.print();
    }

    /**
     * 製品の分類
     * @since 1.0
     */
    public enum Type {
        CONSUMER("消費財"), // 消費財
        CURRENT_ASSET("流動資産"), // 流動資産
        LAND("土地"), FIXED_ASSET("固定資産"); // 固定資産

        private final String name;

        /**
         *
         * @param name
         * @since 1.0
         */
        Type(String name) {
            this.name = name;
        }

        /**
         * @since 1.0
         */
        @Override
        public String toString() {
            return name;
        }
    }
}
