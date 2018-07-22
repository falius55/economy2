package jp.gr.java_conf.falius.economy2.helper;

import jp.gr.java_conf.falius.util.range.IntRange;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 */
public class Taxes {
    // 消費税額
    private static final double CONSUMPTION_TAX = 0.08;
    // 所得税(月額)
    // 超過累進税率
    private static final double[][] INCOME_TAX = { // {下限,税率}
            { 0, 0.05 }, // 0円以上、1,950,000円以下は5%
            { 1950000, 0.1 }, // 1,950,000円以上、3,300,000円以下は10%
            { 3300000, 0.2 },
            { 6950000, 0.23 },
            { 9000000, 0.33 },
            { 18000000, 0.4 },
            { 40000000, 0.45 }
    };
    private static final double[][] CORPORATION_TAX = {
            { 0, 0.15 },
            { 8000000, 0.255 }
    };
    /**
     * 復興所得税
     */
    private static final double RECONSTRUCTION_TAX = 0.021;

    /**
     * 税込額から、消費税額を計算します。
     * @param price 税込額
     * @return
     * @since 1.0
     */
    public static int computeConsumptionTax(int price) {
        return (int) (price - ((double) price / (1 + CONSUMPTION_TAX)));
    }

    /**
     * 税抜価格から、税込価格を計算します。
     * @param price
     * @return
     * @since 1.0
     */
    public static int computeInConsumptionTax(int price) {
        return (int) (price * (1 + CONSUMPTION_TAX));
    }

    /**
     * 売値と原価から未払消費税額を計算します。
     * @param price 税込みの売値
     * @param cost 税込みの原価
     * @return
     * @since 1.0
     */
    public static int computeAccruedConsumptionTax(int price, int cost) {
        int purchaseTax = computeConsumptionTax(cost);
        int consumptionTax = computeConsumptionTax(price);  // 内消費税
        return consumptionTax - purchaseTax;
    }

    /**
     *
     * @param monthlyIncome
     * @return
     * @since 1.0
     */
    public static int computeIncomeTaxFromManthly(int monthlyIncome) {
        return computeIncomeTax(monthlyIncome * 12) / 12;
    }

    /**
     * 所得から、１年分の所得税額を計算します。
     * @param income
     * @return
     * @since 1.0
     */
    public static int computeIncomeTax(int income) {
        return (int) (computeProgressiveTax(income, INCOME_TAX) * (1 + RECONSTRUCTION_TAX));
    }

    /**
     * 企業の一年間の利益から、法人税額を計算します。
     * @param income
     * @return
     * @since 1.0
     */
    public static int computeCorporationTax(int income) {
        if (income < 0) { return 0; }
        return computeProgressiveTax(income, CORPORATION_TAX);
    }

    // 超過累進税額の取得
    private static int computeProgressiveTax(int income, double[][] taxBox) {
        double tax = 0;
        // 所得税額の計算
        // 所得税率の分類の数だけ繰り返す
        for (int i : new IntRange(taxBox.length)) {
            double under = taxBox[i][0];
            double rate = taxBox[i][1];
            // 最高分類以上の収入があれば、収入から最高分類の金額を控除した残額に最高分類の税率をかけて税額化する。i+1を判定に使う条件式に入る前にループを抜ける。
            if (i == taxBox.length - 1) {
                tax += (income - under) * rate;
                break;
            }
            double upper = taxBox[i + 1][0];
            if (income > upper) {
                // 収入が現分類より一つ上の分類超あるなら、現分類の一つ上の分類額から現分類の金額を控除した残額に現分類の税率をかけて税額化する
                tax += (upper - under) * rate;
            } else {
                // 収入が現分類より一つ上の分類以下ならば、収入から現分類の金額を控除した残額に現分類の税率をかけて税額化する
                tax += (income - under) * rate;
                break;
            }
        }
        return (int) tax;
    }
}
