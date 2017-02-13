package de.fluxparticle.utils.chain;

import java.util.function.Supplier;

/**
 * Created by sreinck on 14.06.16.
 */
public class Lazy<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private T data = null;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (data == null) {
            data = supplier.get();
        }
        return data;
    }

}
