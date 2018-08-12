package jp.gr.java_conf.falius.economy2.player;

import java.util.Set;

import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public interface Employable extends Entity {
    static int LOWEST_SALARY = 100000;
    static double SALARIES_RATE = 0.6;

    /**
     * 求人の有無を返します。
     * @return
 * @since 1.0
     */
    public boolean isRecruit();

    /**
     * 労働者が所属しているかどうか
     * @param worker
     * @return
 * @since 1.0
     */
    public boolean has(Worker worker);

    /**
     * 人を雇用します
 * @since 1.0
     */
    public Employable employ(Worker worker);

    /**
     * 社員を解雇します
 * @since 1.0
     */
    public Employable fire(Worker worker);

    /**
     * 給与を支払います
     * @param worker 支払対象社員
     * @param salary
     * @return 計算された給与額
 * @since 1.0
     */
    public int paySalary(Worker worker, int salary);

    /**
     *
     * @param worker
     * @return
     * @since 1.0
     */
    public default int paySalary(Worker worker) {
        return paySalary(worker, LOWEST_SALARY);
    }

    /**
     * @since 1.0
     */
    public default void paySalaries() {
        int salary = computeSalary();
        employers().stream()
        .forEach(worker -> paySalary(worker, salary));
    }

    /**
     * @since 1.0
     */
    public default int computeSalary() {
        int countEmployers = employers().size();
        if (countEmployers == 0) {
            return 0;
        }
        int allSalary = (int) (books().benefit() * SALARIES_RATE);
        int salary = allSalary / countEmployers;
        return Math.max(salary, LOWEST_SALARY);
    }

    /**
     * @since 1.0
     */
    public Set<Worker> employers();

    /**
     *
     * @param nationBooks
     * @return
     * @since 1.0
     */
    public Employable payIncomeTax(GovernmentBooks nationBooks);

}
