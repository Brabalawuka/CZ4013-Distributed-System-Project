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
    protected byte[] messageBuffer;

    //The number of valid bytes in the buffer.
    protected int count;

    //Default 64 bytes
    public XYZZByteWriter() {
        this(64);
    }


    public XYZZByteWriter(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        messageBuffer = new byte[size];
    }

    public synchronized void write(int b) {
        checkCapacity(count + 1);
        messageBuffer[count] = (byte) b;
        count += 1;
    }

    public void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    public synchronized void write(byte[] bytes, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, bytes.length);
        checkCapacity(count + length);
        System.arraycopy(bytes, offset, messageBuffer, count, length);
        count += length;
    }

    private void checkCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - messageBuffer.length > 0)
            bufferExpand(minCapacity);
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void bufferExpand(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = messageBuffer.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = Integer.MAX_VALUE;
        messageBuffer = Arrays.copyOf(messageBuffer, newCapacity);
    }

    public synchronized byte[] toByteArray() {
        return Arrays.copyOf(messageBuffer, count);
    }


}
