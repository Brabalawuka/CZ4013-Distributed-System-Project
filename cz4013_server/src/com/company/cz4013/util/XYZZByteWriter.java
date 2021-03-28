package com.company.cz4013.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * A Writer to store bytes with variable length in an incremental way
 * Easy to use when the messages are serialized part by part
 */
public class XYZZByteWriter {


    //The buffer where data is stored.
    protected byte buf[];

    //The number of valid bytes in the buffer.
    protected int count;


    //Default 64 bytes
    public XYZZByteWriter() {
        this(64);
    }


    public XYZZByteWriter(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: "
                    + size);
        }
        buf = new byte[size];
    }

    public synchronized void write(int b) {
        checkCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }

    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    public synchronized void write(byte b[], int off, int len) {
        Objects.checkFromIndexSize(off, len, b.length);
        checkCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    private void checkCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buf.length > 0)
            bufferExpand(minCapacity);
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void bufferExpand(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = Integer.MAX_VALUE;
        buf = Arrays.copyOf(buf, newCapacity);
    }

    public synchronized byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }


}
