package de.fluxparticle.utils.chain;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

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
    public Chain<T> concat(Chain<T> other) {
        return other;
    }

    public Chain<T> take(int count) {
        return this;
    }

    public <R> Chain<R> map(Function<T, R> function) {
        return emptyChain();
    }

    public <S, R> Chain<R> zipWith(Chain<S> other, BiFunction<T, S, R> function) {
            return emptyChain();
    }

    public <R> R foldl(R initValue, BiFunction<R, T, R> function) {
        return initValue;
    }

    public Optional<T> foldl1(BinaryOperator<T> function) {
        return Optional.empty();
    }

    public <R> Chain<R> flatMap(Function<T, Chain<R>> function) {
        return emptyChain();
    }

}
