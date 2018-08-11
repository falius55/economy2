package jp.gr.java_conf.falius.economy2.book;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.book.FixedAssetManager.FixedAsset;
import jp.gr.java_conf.falius.economy2.helper.MyCollectors;
import jp.gr.java_conf.falius.economy2.market.Market;

public class FixedAssetManagerTest {

    @After
    public void after() {
        Market.INSTANCE.clear();
    }

    @Test
    public void simpleTest() {
        System.out.println("--- simple test ---");
        // 固定資産ひとつだけ
        // すべて償却される場合
        final int DAYS_OF_YEAR = 366;
        int acquisitionCost = 10000;  // 取得原価
        int serviceLife = 2;  // 耐用年数

        FixedAssetManager fm = new FixedAssetManager();
        LocalDate date = LocalDate.now();
        fm.add(date, acquisitionCost, serviceLife);

        int count = DAYS_OF_YEAR * serviceLife;
        Market.INSTANCE.nextDay(count);
        fm.update();

        fm.printAll();
        System.out.printf("today: %s%n", Market.INSTANCE.nowDate().toString());
        System.out.println("--- simple test ---");
        assertThat(fm.unDepreciatedBalance(), is(0));
        assertThat(fm.depreciatedBalance(),
                is(acquisitionCost * (100 - FixedAssetManager.FixedAsset.RESIDUAL_PERCENT) / 100));
    }

    @Test
    public void mainTest() {
        FixedAssetManager fm = new FixedAssetManager();

        int count = 10;

        // 固定資産の追加と減価償却
        LocalDate date = LocalDate.now();
        int depreciatedBalance = 0;
        for (int i = 0; i < count; i++) {
            // 取得日、取得原価、耐用年数
            fm.add(date, 100000 * (i + 1), i % 10 + 1);
            date = date.plusDays(1);
        }

        Market.INSTANCE.nextDay(600);
        depreciatedBalance += fm.update();

        // 結果の表示
        fm.printAll();
        System.out.printf("現在日: %s, 減価償却累計額:%d%n", date, depreciatedBalance);

        fm.assets().stream()
        .map(FixedAsset::recordMap).forEach(recode -> {
            boolean periodCheck = recode.keySet().stream().collect(MyCollectors.integrationIsSameAll((d1, d2) -> d1.until(d2)));
            assertThat(periodCheck, is(true));
            boolean amountCheck = recode.values().stream().collect(MyCollectors.integrationIsSameAll((i1, i2) -> i1 - i2));
            assertThat(amountCheck, is(true));
        });;
    }

}
