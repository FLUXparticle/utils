package de.fluxparticle.utils.chain;

import java.util.Optional;

/**
 * Created by sreinck on 26.12.16.
 */
public class ConcatChain<T> extends Chain<T> {

    private final Chain<T> left;

    private final Chain<T> right;

    public ConcatChain(Chain<T> left, Chain<T> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isEmpty() {
        return left.isEmpty();
    }

    @Override
    public T head() {
        return left.head();
    }

    @Override
    public Chain<T> tail() {
        return left.tail().concat(right);
    }

    @Override
    protected Optional<String> optionalHead() {
        return left.optionalHead();
    }


}
