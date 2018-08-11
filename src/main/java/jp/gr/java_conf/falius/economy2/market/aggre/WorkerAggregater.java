package jp.gr.java_conf.falius.economy2.market.aggre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonTitle;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
class WorkerAggregater {
    private final Collection<WorkerParson> mWorkers = new ArrayList<>();

    /**
     * @since 1.0
     */
    WorkerAggregater() {
    }

    Collection<WorkerParson> collection() {
        return Collections.unmodifiableCollection(mWorkers);
    }

    /**
     *
     * @param worker
     * @since 1.0
     */
    void add(WorkerParson worker) {
        mWorkers.add(worker);
    }

    /**
     * 雇用者報酬
     * @return
     * @since 1.0
     */
    int salary() {
        return mWorkers.stream()
                .map(WorkerParson::books)
                .mapToInt(book -> book.get(WorkerParsonTitle.SALARIES))
                .sum();
    }

    /**
     * 消費総額
     * @return
     * @since 1.0
     */
    int consumption() {
        return mWorkers.stream()
                .map(WorkerParson::books)
                .mapToInt(book -> book.get(WorkerParsonTitle.CONSUMPTION))
                .sum();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    int cashAndDeposits() {
        return mWorkers.stream()
                .mapToInt(worker -> worker.cash() + worker.deposit())
                .sum();
    }

    /**
     * @since 1.0
     */
    void clear() {
        mWorkers.clear();
    }

}
