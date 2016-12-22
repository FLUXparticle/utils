package de.fluxparticle.utils.chain;

import java.util.Optional;
import java.util.function.Supplier;

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
    protected Optional<String> optionalHead() {
        if (chain == null) {
            throw new IllegalStateException();
        }
        return chain.optionalHead();
    }

    private Chain<T> getChain() {
        if (chain == null) {
            chain = supplier.get();
        }
        return chain;
    }

}
