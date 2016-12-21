package de.fluxparticle.utils.chain;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * Created by sreinck on 21.12.16.
 */
public abstract class Chain<T> implements Iterable<T> {

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
                return EmptyChain.empty();
            }
        });
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

}
