package de.fluxparticle.utils.chain;

import java.util.Optional;
import java.util.function.*;

/**
 * Created by sreinck on 21.12.16.
 */
public class EagerChain<T> extends Chain<T> {

    private final T head;

    private final Chain<T> tail;

    private final Lazy<Integer> length;

    public EagerChain(T head, Chain<T> tail) {
        this.head = head;
        this.tail = tail;
        length = new Lazy<>(() -> 1 + tail.length());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public Chain<T> tail() {
        return tail;
    }

    @Override
    protected Optional<String> optionalHead() {
        return Optional.of(String.valueOf(head));
    }

    @Override
    public int length() {
        return length.get();
    }

    @Override
    public Chain<T> concat(Chain<T> other) {
        if (other.isEmpty()) {
            return this;
        } else {
            return new LazyChain<>(() -> new EagerChain<>(head(), tail().concat(other)));
        }
    }

    @Override
    public Chain<T> take(int count) {
        if (count > 0) {
            return new LazyChain<>(() -> new EagerChain<>(head(), tail().take(count - 1)));
        } else {
            return emptyChain();
        }
    }

    @Override
    public Chain<T> takeWhile(Predicate<T> predicate) {
        if (predicate.test(head)) {
            return new LazyChain<>(() -> new EagerChain<>(head, tail.takeWhile(predicate)));
        } else {
            return emptyChain();
        }
    }

    @Override
    public Chain<T> dropWhile(Predicate<T> predicate) {
        if (predicate.test(head)) {
            return tail.dropWhile(predicate);
        } else {
            return this;
        }
    }

    @Override
    public <R> Chain<R> map(Function<T, R> function) {
        return new LazyChain<>(() -> {
            R mappedHead = function.apply(head());
            Chain<R> mappedTail = tail().map(function);
            return new EagerChain<>(mappedHead, mappedTail);
        });
    }

    @Override
    public Chain<T> filter(Predicate<T> predicate) {
        if (predicate.test(head)) {
            return new LazyChain<>(() -> new EagerChain<>(head, tail.filter(predicate)));
        } else {
            return tail.filter(predicate);
        }
    }

    @Override
    public <S, R> Chain<R> zipWith(Chain<S> other, BiFunction<T, S, R> function) {
        if (other.isEmpty()) {
            return emptyChain();
        } else {
            return new LazyChain<>(() -> new EagerChain<>(function.apply(head(), other.head()), tail().zipWith(other.tail(), function)));
        }
    }

    @Override
    public <R> R foldl(R initValue, BiFunction<R, T, R> function) {
        R reduction = function.apply(initValue, head());
        return tail().foldl(reduction, function);
    }

    @Override
    public <R> R foldlMutable(R accumulator, BiConsumer<R, T> consumer) {
        consumer.accept(accumulator, head());
        return tail().foldlMutable(accumulator, consumer);
    }

    @Override
    public Optional<T> foldl1(BinaryOperator<T> function) {
        return Optional.of(tail().foldl(head(), function));
    }

    @Override
    public <R> R foldr(BiFunction<T, R, R> function, R initValue) {
        R reduction = tail().foldr(function, initValue);
        return function.apply(head(), reduction);
    }

    @Override
    public Chain<Chain<T>> groupBy(BiPredicate<T, T> predicate) {
        BiFunction<T, Chain<Chain<T>>, Chain<Chain<T>>> f = (u, chainChain) -> {
            if (chainChain.isEmpty()) {
                return singletonChain(singletonChain(u));
            } else {
                Chain<T> chain = chainChain.head();
                T head = chain.head();
                if (predicate.test(u, head)) {
                    return cons(cons(u, chain), chainChain.tail());
                } else {
                    return cons(singletonChain(u), chainChain);
                }
            }
        };
        return foldr(f, emptyChain());
    }

}
