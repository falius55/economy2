package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;

public class WorkerParsonTest {

    @Test
    public void jobTest() {
        PrivateBusiness liblio = new PrivateBusiness(Industry.LIBLIO, Industry.LIBLIO.products());
        PrivateBusiness superMarket = new PrivateBusiness(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products());


        Worker worker = new WorkerParson();
        assertThat(worker.hasJob(), is(false));

        worker.seekJob();
        assertThat(worker.hasJob(), is(true));

        worker.retireJob();
        assertThat(worker.hasJob(), is(false));


        worker.seekJob();
        assertThat(worker.hasJob(), is(true));

        worker.seekJob();
        assertThat(worker.hasJob(), is(true));
    }

}
