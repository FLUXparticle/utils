package de.fluxparticle.utils.chain;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.*;

/**
 * Created by sreinck on 21.12.16.
 */
class EmptyChain<T> extends Chain<T> {

    static Chain EMPTY = new EmptyChain();

    private EmptyChain() {
        // empty
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public T head() {
        throw new NoSuchElementException();
    }

    @Override
    public Chain<T> tail() {
        throw new NoSuchElementException();
    }

    @Override
    protected Optional<String> optionalHead() {
        throw new NoSuchElementException();
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public Chain<T> concat(Chain<T> other) {
        return other;
    }

    @Override
    public Chain<T> take(int count) {
        return this;
    }

    @Override
    public Chain<T> takeWhile(Predicate<T> predicate) {
        return this;
    }

    @Override
    public Chain<T> dropWhile(Predicate<T> predicate) {
        return this;
    }

    @Override
    public <R> Chain<R> map(Function<T, R> function) {
        return emptyChain();
    }

    @Override
    public Chain<T> filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public <S, R> Chain<R> zipWith(Chain<S> other, BiFunction<T, S, R> function) {
            return emptyChain();
    }

    @Override
    public <R> R foldl(R initValue, BiFunction<R, T, R> function) {
        return initValue;
    }

    @Override
    public <R> R foldlMutable(R accumulator, BiConsumer<R, T> consumer) {
        return accumulator;
    }

    @Override
    public Optional<T> foldl1(BinaryOperator<T> function) {
        return Optional.empty();
    }

    @Override
    public <R> R foldr(BiFunction<T, R, R> function, R initValue) {
        return initValue;
    }

    @Override
    public Chain<Chain<T>> groupBy(BiPredicate<T, T> predicate) {
        return emptyChain();
    }

}
