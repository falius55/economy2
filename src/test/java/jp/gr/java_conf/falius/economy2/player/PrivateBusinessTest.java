package jp.gr.java_conf.falius.economy2.player;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.gr.java_conf.falius.economy2.enumpack.Industry;

public class PrivateBusinessTest {

    @BeforeClass
    public static void first() {
        Bank bank = new PrivateBank();
    }

    @Test
    public void employerTest() {
        PrivateBusiness liblio = new PrivateBusiness(Industry.LIBLIO, Industry.LIBLIO.products());
        PrivateBusiness superMarket = new PrivateBusiness(Industry.SUPER_MARKET, Industry.SUPER_MARKET.products());


        Worker worker = new WorkerParson();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(false));

        worker.seekJob();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(true));

        worker.retireJob();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(false));

        worker.seekJob();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(true));

        worker.seekJob();
        assertThat(PrivateBusiness.stream().anyMatch(pb -> pb.has(worker)), is(true));
    }

    @After
    public void clearBusiness() {
        PrivateBusiness.clear();
    }

}
