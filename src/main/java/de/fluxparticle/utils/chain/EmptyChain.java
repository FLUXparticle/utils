package de.fluxparticle.utils.chain;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by sreinck on 21.12.16.
 */
public class EmptyChain<T> extends Chain<T> {

    private static Chain EMPTY = new EmptyChain();

    @SuppressWarnings("unchecked")
    public static <T> Chain<T> empty() {
        return EMPTY;
    }

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
        return Optional.empty();
    }

}
