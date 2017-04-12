package de.fluxparticle.utils.chain;

import java.util.Iterator;

/**
 * Created by sreinck on 12.04.17.
 */
public class ChainIterator<T> implements Iterator<T> {

    private Chain<T> chain;

    public ChainIterator(Chain<T> chain) {
        this.chain = chain;
    }

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

    public Chain<T> getChain() {
        return chain;
    }

}
