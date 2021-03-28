package com.company.cz4013.util;

import java.io.IOException;
import java.util.Objects;

/**
 * A Reader holding bytes with automatically incremented pointers (no manual pointer moving is needed)
 * Easy to use when the bytes are to be read at one pass and switching between classes
 * Reduces possible values
 */
public class XYZZByteReader {

    protected byte[] messageBuffer;
    protected int currentPosition;
    protected int mark = 0;
    protected int count;


    public XYZZByteReader(byte[] messageBuffer) {
        this.messageBuffer = messageBuffer;
        this.currentPosition = 0;
        this.count = messageBuffer.length;
    }

    public synchronized int read(byte[] bytes, int offset, int len) {
        Objects.checkFromIndexSize(offset, len, bytes.length);

        if (currentPosition >= count) {
            return -1;
        }

        int available = count - currentPosition;
        if (len > available) {
            len = available;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(messageBuffer, currentPosition, bytes, offset, len);
        currentPosition += len;
        return len;
    }

    public synchronized int read() {
        return (currentPosition < count) ? (messageBuffer[currentPosition++] & 0xff) : -1;
    }


    public int readNBytes(byte[] bytes, int offset, int len) {
        int n = read(bytes, offset, len);
        return n == -1 ? 0 : n;
    }

    public byte[] readNBytes(int len) throws Exception {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        byte[] bytes = new byte[len];
        int result = read(bytes, 0, len);
        if (result == -1) {
            throw new IOException("Required array size too large");
        } else {
            return bytes;
        }
    }
}
