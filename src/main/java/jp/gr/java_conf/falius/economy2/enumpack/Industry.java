package jp.gr.java_conf.falius.economy2.enumpack;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.stockmanager.Factory;
import jp.gr.java_conf.falius.economy2.stockmanager.Farm;
import jp.gr.java_conf.falius.economy2.stockmanager.HumanResource;
import jp.gr.java_conf.falius.economy2.stockmanager.Repository;
import jp.gr.java_conf.falius.economy2.stockmanager.StockManager;

/**
 * 業種
 * @since 1.0
 */
public enum Industry {
    // このコンストラクタ実行時点でProductが初期化されているとは限らない
    /**
     * @since 1.0
     */
    LIBLIO("書店", Type.RETAIL) {
        public Set<Product> products() {
            return EnumSet.of(Product.NOVEL);
        }
    },
    /**
     * @since 1.0
     */
    REALTOR("不動産屋", Type.BIGMOUTHED_RETAIL) {
        public Set<Product> products() {
            return EnumSet.of(Product.LAND, Product.BUILDINGS);
        }
    },
    /**
     * @since 1.0
     */
    FARMER("農家", Type.FARMER) {
        public Set<Product> products() {
            return EnumSet.of(Product.RICE);
        }
    },
    /**
     * @since 1.0
     */
    SUPER_MARKET("スーパー", Type.RETAIL) {
        public Set<Product> products() {
            return EnumSet.of(Product.NOVEL, Product.RICE_BALL);
        }
    },
    /**
     * @since 1.0
     */
    FOOD_MAKER("飲食メーカー", Type.MAKER) {
        public Set<Product> products() {
            return EnumSet.of(Product.RICE_BALL);
        }
    },
    /**
     * @since 1.0
     */
    ARCHITECTURE("建築会社", Type.CONTRACT) {
        public Set<Product> products() {
            return EnumSet.of(Product.BUILDINGS);
        }
    };

    private final String mName; // 日本語名
    private final Type mType;

    private static final Map<String, Industry> sStringToEnum = new HashMap<String, Industry>(); // 日本語名から業種enumへのマップ
    static {
        for (Industry industry : values())
            sStringToEnum.put(industry.toString(), industry);
    }

    /**
     * @param name 日本語名
     * @since 1.0
     */
    Industry(String name, Type type) {
        mName = name;
        mType = type;
    }

    /**
     * 日本語名から対象のenumインスタンスを取得します
     * @param name 日本語名
     * @return 対象のenum
     * @since 1.0
     */
    public static Industry fromString(String name) {
        return sStringToEnum.get(name);
    }

    /**
     * @return 日本語名
     * @since 1.0
     */
    @Override
    public String toString() {
        return mName;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public Type type() {
        return mType;
    }

    /**
     * 取扱商品の集合を返します
     * @since 1.0
     */
    abstract public Set<Product> products();

    /**
     * 商品取り扱いの有無を返します
     * @since 1.0
     */
    public boolean hasProduct(Product product) {
        return products().contains(product);
    }

    /**
     * @since 1.0
     */
    public void print() {
        System.out.printf("%s%n", this);
        System.out.printf("取扱商品:%s%n", products());
    }

    /**
     * filterによってtrueと判定された要素の集合を返します
     * @param filter 要素の判定に使用する関数型インタフェース
     * @return 判定がtrueである要素の集合
     * @since 1.0
     */
    static public Set<Industry> selectSet(Predicate<Industry> filter) {
        return Arrays.stream(values())
                .filter(filter)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Industry.class)));
    }

    /**
     * 流通経路は、第一次産業 → メーカー → 小売り
     * @author "ymiyauchi"
     * @since 1.0
     */
    public enum Type {
        /** 小売り @since 1.0 */
        RETAIL("小売") {
            @Override
            public StockManager newManager(Product product, HumanResourcesDepartment stuffs  ) {
                return new Repository(product, MAKER);
            }
        },
        /** メーカー @since 1.0 */
        MAKER("メーカー") {
            @Override
            public StockManager newManager(Product product, HumanResourcesDepartment stuffs  ) {
                return new Factory(product);
            }
        },
        /** 第一次産業 @since 1.0 */
        FARMER("第一次産業") {
            @Override
            public StockManager newManager(Product product, HumanResourcesDepartment stuffs  ) {
                return new Farm(product);
            }
        },
        /** 流通業 @since 1.0 */
        DISTRIBUTOR("流通業") {
            @Override
            public StockManager newManager(Product product, HumanResourcesDepartment stuffs) {
                return new Repository(product, MAKER);
            }
        },
        /** 業務請負 @since 1.0 */
        CONTRACT("業務請負") {
            @Override
            public StockManager newManager(Product product, HumanResourcesDepartment stuffs) {
                return new HumanResource(product, stuffs);
            }
        },
        /** 大口小売り @since 1.0 */
        BIGMOUTHED_RETAIL("大口小売");

        private final String mName;

        /**
         *
         * @param name
         * @since 1.0
         */
        Type(String name) {
            mName = name;
        }

        /**
         *
         * @param product
         * @param stuffs
         * @return
         * @since 1.0
         */
        public StockManager newManager(Product product, HumanResourcesDepartment stuffs) {
            return new Repository(product, MAKER);
        }

        /**
         * @since 1.0
         */
        @Override
        public String toString() {
            return mName;
        }
    }

}
