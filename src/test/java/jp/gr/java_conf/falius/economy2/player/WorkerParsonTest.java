package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;

public class WorkerParsonTest {

    @After
    public void clearBusiness() {
        PrivateBusiness.clear();
    }

    @Test
    public void jobTest() {
        PrivateBusiness liblio = new PrivateBusiness(Industry.LIBLIO, Industry.LIBLIO.products());
        PrivateBusiness superMarket = new PrivateBusiness(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products());

        Worker worker = new WorkerParson();
        assertThat(worker.hasJob(), is(false));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
        worker.retireJob();
        assertThat(worker.hasJob(), is(false));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
    }

    @Test
    public void jobTest2() {
        PrivateBusiness liblio = new PrivateBusiness(Industry.LIBLIO, Industry.LIBLIO.products());

        Worker worker = new WorkerParson();
        assertThat(worker.hasJob(), is(false));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
        worker.retireJob();
        assertThat(worker.hasJob(), is(false));
        assertThat(worker.seekJob(), is(true));
        assertThat(worker.hasJob(), is(true));
        assertThat(worker.seekJob(), is(false));  // 今働いている会社以外が存在しないため、転職失敗
        assertThat(worker.hasJob(), is(true));  // 転職に失敗したので、もとの会社も辞めない
    }

}
