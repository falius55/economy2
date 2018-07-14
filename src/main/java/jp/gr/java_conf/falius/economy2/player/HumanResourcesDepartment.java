package jp.gr.java_conf.falius.economy2.player;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HumanResourcesDepartment {
    private final Set<Worker> mEmployers; // 社員のリスト
    private final Map<LocalDate, Set<Worker>> mWorkingRecord; // 出勤記録
    private final int mCapacity;

    public HumanResourcesDepartment(int capacity) {
        mEmployers = new HashSet<Worker>();
        mWorkingRecord = new HashMap<LocalDate, Set<Worker>>();
        mCapacity = capacity;
    }

    /**
     * 職員として採用します
     */
    public HumanResourcesDepartment employ(Worker parson) {
        mEmployers.add(parson);
        return this;
    }
    /**
     * 職員を解雇します
     */
    public HumanResourcesDepartment fire(Worker parson) {
        mEmployers.remove(parson);
        return this;
    }

    /**
     * 勤務記録をつけます
     */
    public HumanResourcesDepartment add(LocalDate date, Worker parson) {
        if (!mWorkingRecord.containsKey(date)) mWorkingRecord.put(date, new HashSet<Worker>());

        mWorkingRecord.get(date).add(parson);
        return this;
    }

    /**
     *
     * @return
     */
    public boolean isRecruit() {
     return mEmployers.size() < mCapacity;
    }

    public boolean has(Worker worker) {
        return mEmployers.contains(worker);
    }
}
