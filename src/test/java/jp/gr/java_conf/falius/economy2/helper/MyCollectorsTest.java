package jp.gr.java_conf.falius.economy2.helper;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;

public class MyCollectorsTest {

    @Test
    public void lastTest() {
        Stream<Integer> model = Stream.of(12, 45, 32, 1, 46);
        int result = model.collect(MyCollectors.last()).get();
        assertThat(result, is(46));
    }

    @Test
    public void lastTest2() {
        Stream<Integer> model = Stream.empty();
        Optional<Integer> result = model.collect(MyCollectors.last());
        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void integrationIsSameAllTest() {
        Stream<LocalDate> model = Stream.iterate(LocalDate.now(), date -> date.plusDays(1));
        boolean result = model.limit(10)
                .collect(MyCollectors.integrationIsSameAll((d1, d2) -> ChronoUnit.DAYS.between(d1, d2)));
        assertThat(result, is(true));
    }

    @Test
    public void toIntegrationTest() {
        Stream<LocalDate> model = Stream.iterate(LocalDate.now(), date -> date.plusDays(1));
        List<Long> result = model.limit(10).collect(MyCollectors.toIntegretionList((d1, d2) -> ChronoUnit.DAYS.between(d1, d2)));
        for (Long n : result) {
            assertThat(n, is(1L));
        }
    }

    @Test
    public void toIntegrationTest2() {
        Stream<LocalDate> model = Stream.iterate(LocalDate.now(), date -> date.plusMonths(2));
        List<Period> result = model.limit(10).collect(MyCollectors.toIntegretionList((d1, d2) -> d1.until(d2)));
        assertThat(result.stream().allMatch(period -> period.equals(Period.ofMonths(2))), is(true));
        assertThat(result.stream().collect(MyCollectors.sameAll()), is(true));
    }

    @Test
    public void toSubtractionListTest() {
        Stream<Integer> model = Stream.of(0, 10, 20, 30, 40, 50);
        List<Integer> result = model.collect(MyCollectors.toSubtractionList());
        for (int e : result) {
            assertThat(e, is(10));
        }
        assertThat(result.size(), is(5));
    }

    @Test
    public void toSubtractionListTest2() {
        Stream<Integer> model = Stream.of(5, 12, 4, 0, 40, 23, -3, -16, 21);
        List<Integer> result = model.collect(MyCollectors.toSubtractionList());
        assertThat(result.get(0), is(7));
        assertThat(result.get(1), is(8));
        assertThat(result.get(2), is(4));
        assertThat(result.get(3), is(40));
        assertThat(result.get(4), is(17));
        assertThat(result.get(5), is(26));
        assertThat(result.get(6), is(13));
        assertThat(result.get(7), is(37));
        assertThat(result.size(), is(8));
    }

    @Test
    public void sameAllTest() {
        Stream<Integer> model = Stream.of(10, 10, 10, 10, 10);
        boolean result = model.collect(MyCollectors.sameAll());
        assertThat(result, is(true));
    }

    @Test
    public void sameAllTest2() {
        Stream<Integer> model = Stream.of(10, 10, 14, 10, 10);
        boolean result = model.collect(MyCollectors.sameAll());
        assertThat(result, is(false));
    }

    @Test
    public void sameAllTest3() {
        Stream<String> model = Stream.of("abc", "abc", "abc", "abc"  );
        boolean result = model.collect(MyCollectors.sameAll());
        assertThat(result, is(true));
    }

    @Test
    public void sameAllTest4() {
        Stream<String> model = Stream.of("abc", "ac", "bcd", "bc"  );
        boolean result = model.collect(MyCollectors.sameAll());
        assertThat(result, is(false));
    }
}
