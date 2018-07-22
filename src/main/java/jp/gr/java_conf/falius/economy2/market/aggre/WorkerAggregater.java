package jp.gr.java_conf.falius.economy2.market.aggre;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;

public class WorkerAggregater {
    private final List<WorkerParson> mWorkers = new ArrayList<>();

    WorkerAggregater() {
    }

    public void add(WorkerParson worker) {
        mWorkers.add(worker);
    }

    /**
     * 雇用者報酬
     * @return
     */
    public int salary() {
        return mWorkers.stream()
                .map(WorkerParson::books)
                .mapToInt(book -> book.get(WorkerParsonAccountTitle.SALARIES))
                .sum();
    }

    /**
     * 消費総額
     * @return
     */
    public int consumption() {
        return mWorkers.stream()
                .map(WorkerParson::books)
                .mapToInt(book -> book.get(WorkerParsonAccountTitle.CONSUMPTION))
                .sum();
    }

    public int cashAndDeposits() {
        return mWorkers.stream()
                .mapToInt(worker -> worker.cash() + worker.deposit())
                .sum();
    }

    public void clear() {
        mWorkers.clear();
    }

}
