package de.fluxparticle.utils.chain;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sreinck on 12.04.17.
 */
public class ChainInputStream extends InputStream {

    private Chain<Integer> chain;

    private Chain<Integer> mark;

    public ChainInputStream(Chain<Integer> chain) {
        this.chain = chain;
    }

    public byte[] getMarkedBytes() {
        Chain<Integer> markedBytes = Chain.subtractChain(mark, chain);

        byte[] result = new byte[markedBytes.length()];

        for (int i = 0; i < result.length; i++) {
            result[i] = markedBytes.head().byteValue();
            markedBytes = markedBytes.tail();
        }

        return result;
    }

    @Override
    public int read() throws IOException {
        if (chain.isEmpty()) {
            return -1;
        }

        int b = chain.head();
        chain = chain.tail();
        return b;
    }

    @Override
    public synchronized void mark(int readlimit) {
        mark = chain;
    }

    @Override
    public synchronized void reset() throws IOException {
        chain = mark;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

}
