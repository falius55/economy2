package jp.gr.java_conf.falius.economy2.account;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessAccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.util.timer.Timer;

public class PrivateBusinessAccountTest {

    @Test
    public void mainTest() {
        assertThat(55, is(55));

        PrivateBusinessAccount account = PrivateBusinessAccount.newInstance();
        account.add(PrivateBusinessAccountTitle.SALES, 2000);
        System.out.println(account);
        test_fixedAssets(1000, account);
        Product.printAll();
    }

    private void test_fixedAssets(int count, PrivateBusinessAccount account) {
        // 固定資産の追加と減価償却
        LocalDate date = LocalDate.now();
        int depreciatedBalance = 0;
        for (int i = 0; i < 100; i++) {
            account.addFixedAsset(date, 100000 * (i + 1), i % 10 + 1);
            depreciatedBalance += account.recordFixedAssets(date);
            date = date.plusDays(1);
        }

        depreciatedBalance += Stream.iterate(date, d -> d.plusDays(1)).limit(3000)
                .mapToInt(account::recordFixedAssets)
                .sum();

        Timer timer = new Timer();
        // 結果の表示
        timer.start("print()");
        account.printAllFixedAssets();
        timer.end("print()");
        System.out.printf("現在日: %s, 減価償却累計額:%d%n", date, depreciatedBalance);

        timer.start("addFixedAsset()");
        account.addFixedAsset(date, 100000, 8);
        timer.end("addFixedAsset()");
        timer.start("plusDays");
        date.plusDays(1);
        timer.end("plusDays");
        timer.start("record");
        account.recordFixedAssets(date);
        timer.end("record");
    }
}
