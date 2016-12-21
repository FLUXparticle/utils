package de.fluxparticle.utils.chain;

/**
 * Created by sreinck on 21.12.16.
 */
public class EagerChain<T> extends Chain<T> {

    private final T head;

    private final Chain<T> tail;

    public EagerChain(T head, Chain<T> tail) {
        this.head = head;
        this.tail = tail;
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

}
