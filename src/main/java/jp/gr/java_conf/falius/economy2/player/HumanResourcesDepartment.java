package jp.gr.java_conf.falius.economy2.player;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class HumanResourcesDepartment {
    private final Set<Worker> mEmployers; // 社員のリスト
    private final Map<LocalDate, Set<Worker>> mWorkingRecord; // 出勤記録
    private final int mCapacity;

    /**
     *
     * @param capacity
     * @since 1.0
     */
    public HumanResourcesDepartment(int capacity) {
        mEmployers = new HashSet<Worker>();
        mWorkingRecord = new HashMap<LocalDate, Set<Worker>>();
        mCapacity = capacity;
    }

    public Set<Worker> employers() {
        return Collections.unmodifiableSet(mEmployers);
    }

    /**
     * 職員として採用します
     * @since 1.0
     */
    public HumanResourcesDepartment employ(Worker parson) {
        mEmployers.add(parson);
        return this;
    }
    /**
     * 職員を解雇します
     * @since 1.0
     */
    public HumanResourcesDepartment fire(Worker parson) {
        mEmployers.remove(parson);
        return this;
    }

    /**
     * 勤務記録をつけます
     * @since 1.0
     */
    public HumanResourcesDepartment add(LocalDate date, Worker parson) {
        if (!mWorkingRecord.containsKey(date)) {
            mWorkingRecord.put(date, new HashSet<Worker>());
        }

        mWorkingRecord.get(date).add(parson);
        return this;
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public boolean isRecruit() {
     return mEmployers.size() < mCapacity;
    }

    /**
     *
     * @param worker
     * @return
     * @since 1.0
     */
    public boolean has(Worker worker) {
        return mEmployers.contains(worker);
    }

    public int count() {
        return mEmployers.size();
    }

    public void clear() {
        mEmployers.clear();
        mWorkingRecord.clear();
    }
}
