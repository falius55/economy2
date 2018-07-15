package jp.gr.java_conf.falius.economy2.enumpack;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.Retail;
import jp.gr.java_conf.falius.economy2.stockmanager.Factory;
import jp.gr.java_conf.falius.economy2.stockmanager.Farm;
import jp.gr.java_conf.falius.economy2.stockmanager.Repository;
import jp.gr.java_conf.falius.economy2.stockmanager.StockManager;

/**
 * 業種
 */
public enum Industry {
    // このコンストラクタ実行時点でProductが初期化されているとは限らない
    LIBLIO("書店", Type.RETAIL) {
        public Set<Product> products() {
            return EnumSet.of(Product.NOVEL);
        }
    },
    REALTOR("不動産屋", Type.BIGMOUTHED_RETAIL) {
        public Set<Product> products() {
            return EnumSet.of(Product.LAND, Product.BUILDINGS);
        }
    },
    FARMER("農家", Type.FARMER) {
        public Set<Product> products() {
            return EnumSet.of(Product.RICE);
        }
    },
    SUPER_MARKET("スーパー", Type.RETAIL) {
        public Set<Product> products() {
            return EnumSet.of(Product.NOVEL, Product.RICE_BALL);
        }
    },
    FOOD_MAKER("飲食メーカー", Type.MAKER) {
        public Set<Product> products() {
            return EnumSet.of(Product.RICE_BALL);
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
     * @param mName 日本語名
     */
    Industry(String name, Type type) {
        mName = name;
        mType = type;
    }

    /**
     * 日本語名から対象のenumインスタンスを取得します
     * @param mName 日本語名
     * @return 対象のenum
     */
    public static Industry fromString(String name) {
        return sStringToEnum.get(name);
    }

    /**
     * @return 日本語名
     */
    @Override
    public String toString() {
        return mName;
    }

    public Type type() {
        return mType;
    }

    /**
     * 取扱商品の集合を返します
     */
    abstract public Set<Product> products();

    /**
     * 商品取り扱いの有無を返します
     */
    public boolean hasProduct(Product product) {
        return products().contains(product);
    }

    public PrivateBusiness createInstance(int initialExpenses) {
        return type().createInstance(this, products(), initialExpenses);
    }

    public void print() {
        System.out.printf("%s%n", this);
        System.out.printf("取扱商品:%s%n", products());
    }

    /**
     * filterによってtrueと判定された要素の集合を返します
     * @param filter 要素の判定に使用する関数型インタフェース
     * @return 判定がtrueである要素の集合
     */
    static public Set<Industry> selectSet(Predicate<Industry> filter) {
        return Arrays.stream(values())
                .filter(filter)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Industry.class)));
    }

    /**
     * 流通経路は、第一次産業 -> メーカー -> 小売り
     * @author "ymiyauchi"
     *
     */
    public enum Type {
        /** 小売り */ RETAIL("小売") {
            @Override
            public PrivateBusiness createInstance(Industry industry, Set<Product> products, int initialExpenses) {
                return new Retail(industry, products, initialExpenses);
            }

            @Override
            public StockManager newManager(Product product) {
                return new Repository(product, MAKER);
            }
        },
        /** メーカー */ MAKER("メーカー") {
            @Override
            public StockManager newManager(Product product) {
                return new Factory(product);
            }
        },
        /** 第一次産業 */ FARMER("第一次産業") {
            @Override
            public StockManager newManager(Product product) {
                return new Farm(product);
            }
        },
        /** 流通業 */ DISTRIBUTOR("流通業") {
            @Override
            public StockManager newManager(Product product) {
                return new Repository(product, MAKER);
            }
        },
        /** 大口小売り */ BIGMOUTHED_RETAIL("大口小売");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public PrivateBusiness createInstance(Industry industry, Set<Product> products, int initialExpenses) {
            return new PrivateBusiness(industry, products, initialExpenses);
        }

        public StockManager newManager(Product product) {
            return new Repository(product, MAKER);
        }
    }

}
