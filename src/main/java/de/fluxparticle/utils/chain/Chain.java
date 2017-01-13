package de.fluxparticle.utils.chain;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

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

    public static Chain<Character> fromReader(Reader reader) {
        return new LazyChain<>(() -> {
            int ch;
            try {
                ch = reader.read();
            } catch (IOException e) {
                ch = -1;
            }
            if (ch >= 0) {
                return new EagerChain<>((char) ch, fromReader(reader));
            } else {
                return emptyChain();
            }
        });
    }

    public static <T> Chain<T> fromIterator(Iterator<? extends T> iterator) {
        return new LazyChain<>(() -> {
            if (iterator.hasNext()) {
                T next = iterator.next();
                return new EagerChain<>(next, fromIterator(iterator));
            } else {
                return emptyChain();
            }
        });
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
            return new ConcatChain<>(this, other);
        }
    }

    public <R> Chain<R> map(Function<T, R> function) {
        return new LazyChain<>(() -> {
            R mappedHead = function.apply(head());
            Chain<R> mappedTail = tail().map(function);
            return new EagerChain<>(mappedHead, mappedTail);
        });
    }

    public <R> R reduce(R initValue, BiFunction<R, T, R> function) {
        R reduction = function.apply(initValue, head());
        return tail().reduce(reduction, function);
    }

    public Optional<T> reduce(BinaryOperator<T> function) {
        return Optional.of(tail().reduce(head(), function));
    }

    public <R> Chain<R> flatMap(Function<T, Chain<R>> function) {
        return map(function).reduce(emptyChain(), Chain::concat);
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
