package de.fluxparticle.utils.chain;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.*;
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

    public static Chain<Integer> fromInputStream(InputStream stream) {
        try {
            int b = stream.read();
            if (b >= 0) {
                return new LazyChain<>(() -> new EagerChain<>(b, fromInputStream(stream)));
            }
        } catch (IOException ignored) {
        }
        return emptyChain();
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
        if (t != null) {
            return singletonChain(t);
        } else {
            return emptyChain();
        }
    }

    public static <T> Chain<T> fromOptional(Optional<T> opt) {
        if (opt.isPresent()) {
            return singletonChain(opt.get());
        } else {
            return emptyChain();
        }
    }

    public static <T> Chain<T> fromOptionalChain(Chain<Optional<T>> chainOpt) {
        return chainOpt.filter(Optional::isPresent).map(Optional::get);
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

    public abstract int length();

    public abstract Chain<T> concat(Chain<T> other);

    public abstract Chain<T> take(int count);

    public abstract Chain<T> takeWhile(Predicate<T> predicate);

    public abstract Chain<T> dropWhile(Predicate<T> predicate);

    public abstract <R> Chain<R> map(Function<T, R> function);

    public abstract Chain<T> filter(Predicate<T> predicate);

    public abstract <S, R> Chain<R> zipWith(Chain<S> other, BiFunction<T, S, R> function);

    public abstract <R> R foldl(R initValue, BiFunction<R, T, R> function);

    public final <R> R accumulate(Accumulator<T, R> accumulator) {
        foldlMutable(accumulator, Accumulator::accumulate);
        return accumulator.finish();
    }

    public abstract <R> R foldlMutable(R accumulator, BiConsumer<R, T> consumer);

    public abstract Optional<T> foldl1(BinaryOperator<T> function);

    public abstract <R> R foldr(BiFunction<T, R, R> function, R initValue);

    public final Optional<T> foldr1(BinaryOperator<T> function) {
        BiFunction<T, Optional<T>, Optional<T>> optFunction = (u, optV) -> Optional.of(optV.map(v -> function.apply(u, v)).orElse(u));
        return foldr(optFunction, Optional.empty());
    }

    public final <R> Chain<R> flatMap(Function<T, Chain<R>> function) {
        return map(function).foldl(emptyChain(), Chain::concat);
    }

    public abstract Chain<Chain<T>> groupBy(BiPredicate<T, T> predicate);

    public final <K> Chain<Chain<T>> groupOn(Function<T, K> keyMapper) {
        return groupBy((u, v) -> Objects.equals(keyMapper.apply(u), keyMapper.apply(v)));
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
