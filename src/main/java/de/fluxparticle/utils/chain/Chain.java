package de.fluxparticle.utils.chain;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

/**
 * Created by sreinck on 21.12.16.
 */
public abstract class Chain<T> implements Iterable<T> {

    @SuppressWarnings("unchecked")
    public static <T> Chain<T> emptyChain() {
        return EmptyChain.EMPTY;
    }

    public static <T> Chain<T> singletonChain(T t) {
        return new EagerChain<>(t, emptyChain());
    }

    public static <T> Chain<T> cons(T head, Chain<T> tail) {
        return new EagerChain<>(head, tail);
    }

    public static <T> Chain<T> repeatChain(T t) {
        return new LazyChain<>(() -> new EagerChain<>(t, repeatChain(t)));
    }

    public static Chain<Integer> rangeChain(int start, int end) {
        if (start <= end) {
            return new LazyChain<>(() -> new EagerChain<>(start, rangeChain(start+1, end)));
        } else {
            return emptyChain();
        }
    }

    public static Chain<Character> fromReader(Reader reader) {
        try {
            int ch = reader.read();
            if (ch >= 0) {
                return new LazyChain<>(() -> new EagerChain<>((char) ch, fromReader(reader)));
            }
        } catch (IOException ignored) {
        }
        return emptyChain();
    }

    public static <T> Chain<T> fromNullable(T t) {
        if (t == null) {
            return emptyChain();
        } else {
            return singletonChain(t);
        }
    }

    public static <T> Chain<T> fromIterator(Iterator<? extends T> iterator) {
        if (iterator.hasNext()) {
            T next = iterator.next();
            return new LazyChain<>(() -> new EagerChain<>(next, fromIterator(iterator)));
        } else {
            return emptyChain();
        }
    }

    @SafeVarargs
    public static <T> Chain<T> from(T... ts) {
        return fromIterator(asList(ts).iterator());
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Chain<T> chain = Chain.this;

            @Override
            public boolean hasNext() {
                return !chain.isEmpty();
            }

            @Override
            public T next() {
                T head = chain.head();
                chain = chain.tail();
                return head;
            }

        };
    }

    public abstract boolean isEmpty();

    public abstract T head();

    public abstract Chain<T> tail();

    protected abstract Optional<String> optionalHead();

    public Chain<T> concat(Chain<T> other) {
        if (other.isEmpty()) {
            return this;
        } else {
            return new LazyChain<>(() -> new EagerChain<>(head(), tail().concat(other)));
        }
    }

    public Chain<T> take(int count) {
        if (count > 0) {
            return new LazyChain<>(() -> new EagerChain<>(head(), tail().take(count - 1)));
        } else {
            return emptyChain();
        }
    }

    public <R> Chain<R> map(Function<T, R> function) {
        return new LazyChain<>(() -> {
            R mappedHead = function.apply(head());
            Chain<R> mappedTail = tail().map(function);
            return new EagerChain<>(mappedHead, mappedTail);
        });
    }

    public <S, R> Chain<R> zipWith(Chain<S> other, BiFunction<T, S, R> function) {
        if (other.isEmpty()) {
            return emptyChain();
        } else {
            return new LazyChain<>(() -> new EagerChain<>(function.apply(head(), other.head()), tail().zipWith(other.tail(), function)));
        }
    }

    public <R> R foldl(R initValue, BiFunction<R, T, R> function) {
        R reduction = function.apply(initValue, head());
        return tail().foldl(reduction, function);
    }

    public Optional<T> foldl1(BinaryOperator<T> function) {
        return Optional.of(tail().foldl(head(), function));
    }

    public <R> Chain<R> flatMap(Function<T, Chain<R>> function) {
        return map(function).foldl(emptyChain(), Chain::concat);
    }

    public final Stream<T> asStream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();

        try {
            sb.append('[');

            String delimiter = "";
            for (Chain<T> chain = this; ; chain = chain.tail()) {
                Optional<String> optString = chain.optionalHead();
                sb.append(delimiter);
                if (optString.isPresent()) {
                    sb.append(optString.get());
                } else {
                    break;
                }
                delimiter = ", ";
            }

            sb.append("...");
        } catch (NoSuchElementException e) {
            sb.append("]");
        }

        return sb.toString();
    }

}
