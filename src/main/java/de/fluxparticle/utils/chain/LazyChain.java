package de.fluxparticle.utils.chain;

import java.util.Optional;
import java.util.function.*;

/**
 * Created by sreinck on 21.12.16.
 */
public class LazyChain<T> extends Chain<T> {

    private final Supplier<Chain<T>> supplier;

    private Chain<T> chain;

    public LazyChain(Supplier<Chain<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean isEmpty() {
        return getChain().isEmpty();
    }

    @Override
    public T head() {
        return getChain().head();
    }

    @Override
    public Chain<T> tail() {
        return getChain().tail();
    }

    @Override
    public int length() {
        return getChain().length();
    }

    @Override
    public Chain<T> concat(Chain<T> other) {
        return getChain().concat(other);
    }

    @Override
    public <S, R> Chain<R> zipWith(Chain<S> other, BiFunction<T, S, R> function) {
        return getChain().zipWith(other, function);
    }

    @Override
    public <R> Chain<R> map(Function<T, R> function) {
        return getChain().map(function);
    }

    @Override
    public Chain<T> filter(Predicate<T> predicate) {
        return getChain().filter(predicate);
    }

    @Override
    public <R> R foldl(R initValue, BiFunction<R, T, R> function) {
        return getChain().foldl(initValue, function);
    }

    @Override
    public <R> R foldlMutable(R accumulator, BiConsumer<R, T> consumer) {
        return getChain().foldlMutable(accumulator, consumer);
    }

    @Override
    public Optional<T> foldl1(BinaryOperator<T> function) {
        return getChain().foldl1(function);
    }

    @Override
    public Chain<T> take(int count) {
        return getChain().take(count);
    }

    @Override
    public Chain<T> takeWhile(Predicate<T> predicate) {
        return getChain().takeWhile(predicate);
    }

    @Override
    public Chain<T> dropWhile(Predicate<T> predicate) {
        return getChain().dropWhile(predicate);
    }

    @Override
    public <R> R foldr(BiFunction<T, R, R> function, R initValue) {
        return getChain().foldr(function, initValue);
    }

    @Override
    public Chain<Chain<T>> groupBy(BiPredicate<T, T> predicate) {
        return getChain().groupBy(predicate);
    }

    @Override
    protected Optional<String> optionalHead() {
        return Optional.ofNullable(chain).flatMap(Chain::optionalHead);
    }

    private Chain<T> getChain() {
        if (chain == null) {
            chain = supplier.get();
        }
        return chain;
    }

}
