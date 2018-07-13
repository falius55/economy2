package jp.gr.java_conf.falius.economy2.account;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.Test;

public class FixedAssetManagerTest {

    @Test
    public void simpleTest() {
        // 固定資産ひとつだけ
        // すべて償却される場合
        final int DAYS_OF_YEAR = 364;
        int acquisitionCost = 10000;  // 取得原価
        int serviceLife = 2;  // 耐用年数

        FixedAssetManager fm = new FixedAssetManager();
        LocalDate date = LocalDate.now();
        fm.add(date, acquisitionCost, serviceLife);

        int count = DAYS_OF_YEAR * serviceLife;
        for (int i = 0; i < count; i++, date = date.plusDays(1)) {
            fm.record(date);
        }

        assertThat(fm.unDepreciatedBalance(), is(0));
        assertThat(fm.depreciatedBalance(),
                is(acquisitionCost * (100 - FixedAssetManager.FixedAsset.RESIDUAL_PERCENT) / 100));
        fm.printAll();
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
            depreciatedBalance += fm.record(date);
            date = date.plusDays(1);
        }

        depreciatedBalance += Stream.iterate(date, d -> d.plusDays(1)).limit(600)
                .mapToInt(fm::record)
                .sum();

        // 結果の表示
        fm.printAll();
        System.out.printf("現在日: %s, 減価償却累計額:%d%n", date, depreciatedBalance);
    }

}
