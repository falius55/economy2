package jp.gr.java_conf.falius.economy2.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
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
     * 各要素すべての組み合わせにcombinatorを適用し、その結果をcollectionFactoryから得られるコレクションに格納して返すコレクタを作成します。
     * @param combinator
     * @param collectionFactory
     * @return
     */
    public static <T, R, C extends Collection<R>> Collector<T, ?, C> toCombination(BiFunction<T, T, R> combinator,
            Supplier<C> collectionFactory) {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            C ret = collectionFactory.get();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                T object = list.get(i);
                for (int j = i + 1; j < size; j++) {
                    ret.add(combinator.apply(object, list.get(j)));
                }
            }
            return ret;
        });
    }

    /**
     * @return
     * @since 1.0
     */
    public static <T> Collector<T, ?, Boolean> integrationIsSameAll(BiFunction<T, T, ?> integrator) {
        return Collectors.collectingAndThen(toIntegrationSet(integrator), set -> set.size() == 1);
    }

    /**
     * @return
     * @since 1.0
     */
    public static <T, R> Collector<T, ?, Set<R>> toIntegrationSet(BiFunction<T, T, R> integrator) {
        return toIntegration(integrator, HashSet<R>::new);
    }

    /**
     * ひとつ目とふたつ目を統合したもの、ふたつ目と三つ目を統合したもの...のリストを返すコレクターを作成します。
     * 要素が一つしかない場合には空のリストを返します。
     * @param integrator
     * @return
     * @since 1.0
     */
    public static <T, R> Collector<T, ?, List<R>> toIntegrationList(BiFunction<T, T, R> integrator) {
        return toIntegration(integrator, ArrayList<R>::new);
    }

    /**
     * Integerのクラスから、ひとつ目とふたつ目の差、ふたつ目と三つめの差... といった集合を返すCollectorを作成します。
     * @return
     * @since 1.0
     */
    // T, A, R
    public static Collector<Integer, ?, List<Integer>> toSubtractionList() {
        return toIntegrationList((i1, i2) -> Math.abs(i1 - i2));
    }

    public static <T, R, C extends Collection<R>> Collector<T, ?, C> toIntegration(BiFunction<T, T, R> integrator,
            Supplier<C> collectionFactory) {
        return Collector.<T, IntegrationHelper<T, R, C>, C> of(
                // suplier
                () -> new IntegrationHelper<T, R, C>(collectionFactory),
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
                IntegrationHelper<T, R, C>::finish);
    }

    private static class IntegrationHelper<T, R, C extends Collection<R>> {
        private final C mCollection;
        private T mBeforeEntity = null;

        private IntegrationHelper(Supplier<C> collectionFactory) {
            mCollection = collectionFactory.get();
        }

        private void add(R entity) {
            mCollection.add(entity);
        }

        private T exchange(T entity) {
            T ret = mBeforeEntity;
            mBeforeEntity = entity;
            return ret;
        }

        private IntegrationHelper<T, R, C> combine(IntegrationHelper<T, R, C> other) {
            mCollection.addAll(other.mCollection);
            return this;
        }

        private C finish() {
            return mCollection;
        }
    }

}
