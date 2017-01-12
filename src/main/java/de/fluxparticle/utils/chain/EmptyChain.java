package de.fluxparticle.utils.chain;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Created by sreinck on 21.12.16.
 */
public class EmptyChain<T> extends Chain<T> {

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

    public <R> Chain<R> map(Function<T, R> function) {
        return emptyChain();
    }

    public <R> R reduce(R initValue, BiFunction<R, T, R> function) {
        return initValue;
    }

    public T reduce(BinaryOperator<T> function) {
        throw new NoSuchElementException();
    }

    public <R> Chain<R> flatMap(Function<T, Chain<R>> function) {
        return emptyChain();
    }

}
