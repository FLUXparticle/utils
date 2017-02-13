package de.fluxparticle.utils.chain;

/**
 * Created by sreinck on 30.01.17.
 */
public interface Accumulator<T, R> {

    void accumulate(T data);

    @SuppressWarnings("unchecked")
    default R finish() {
        return (R) this;
    }

}
