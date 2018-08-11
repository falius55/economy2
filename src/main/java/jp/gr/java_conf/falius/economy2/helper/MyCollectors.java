package jp.gr.java_conf.falius.economy2.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 */
public class MyCollectors {

    private MyCollectors() {
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public static <T> Collector<T, ?, Optional<T>> last() {
        return Collectors.collectingAndThen(Collectors.toList(), list -> getOpt(list, list.size() - 1));
    }

    private static <T> Optional<T> getOpt(List<T> list, int n) {
        if (list.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(list.get(n));
    }

    /**
     * 要素がすべて同じものであればtrueを返すコレクタを作成します。
     * @return
     * @since 1.0
     */
    public static <T> Collector<T, ?, Boolean> sameAll() {
        return Collectors.collectingAndThen(Collectors.toSet(), set -> set.size() == 1);
    }

    /**
     * @return
     * @since 1.0
     */
    public static <T> Collector<T, ?, Boolean> integrationIsSameAll(BiFunction<T, T, ?> integrator) {
        return Collectors.collectingAndThen(toIntegretionSet(integrator), set -> set.size() == 1);
    }

    /**
     * @return
     * @since 1.0
     */
    public static <T, R> Collector<T, ?, Set<R>> toIntegretionSet(BiFunction<T, T, R> integrator) {
        return Collectors.collectingAndThen(toIntegretionList(integrator), list -> new HashSet<>(list));
    }

    /**
     * ひとつ目とふたつ目を統合したもの、ふたつ目と三つ目を統合したもの...のリストを返すコレクターを作成します。
     * 要素が一つしかない場合には空のリストを返します。
     * @param integrator
     * @return
     * @since 1.0
     */
    public static <T, R> Collector<T, ?, List<R>> toIntegretionList(BiFunction<T, T, R> integrator) {
        return Collector.<T, IntegretionHelper<T, R>, List<R>>of(
                // suplier
                IntegretionHelper<T, R>::new,
                // accumulator
               (helper, e) -> {
                   T before = helper.exchange(e);
                   if (Objects.nonNull(before)) {
                       helper.add(integrator.apply(before, e));
                   }
               },
               // combiner
               (helper1, helper2) -> helper1.combine(helper2),
               // finisher
               IntegretionHelper<T, R>::finish
                );
    }

    private static class IntegretionHelper<T, R> {
        private final List<R> mList;
        private T mBeforeEntity = null;

        private IntegretionHelper() {
            mList = new ArrayList<>();
        }

        private void add(R entity) {
            mList.add(entity);
        }

        private T exchange(T entity) {
            T ret = mBeforeEntity;
            mBeforeEntity = entity;
            return ret;
        }

        private IntegretionHelper<T, R> combine(IntegretionHelper<T, R> other) {
            mList.addAll(other.mList);
            return this;
        }

        private List<R> finish() {
            return mList;
        }
    }

    /**
     * Integerのクラスから、ひとつ目とふたつ目の差、ふたつ目と三つめの差... といった集合を返すCollectorを作成します。
     * @return
     * @since 1.0
     */
    // T, A, R
    public static Collector<Integer, ?, List<Integer>> toSubtractionList() {
        return toIntegretionList((i1, i2) -> Math.abs(i1 - i2));
    }

}
