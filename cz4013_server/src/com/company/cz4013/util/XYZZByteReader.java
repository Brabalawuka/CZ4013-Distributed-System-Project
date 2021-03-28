package com.company.cz4013.util;

import com.company.cz4013.exception.DeserialisationError;

import java.io.IOException;
import java.util.Objects;

public class XYZZByteReader {

    protected byte buf[];
    protected int pos;
    protected int mark = 0;
    protected int count;


    public XYZZByteReader (byte buf[]) {
        this.buf = buf;
        this.pos = 0;
        this.count = buf.length;
    }

    public synchronized int read(byte b[], int off, int len) {
        Objects.checkFromIndexSize(off, len, b.length);

        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    public synchronized int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }


    public int readNBytes(byte[] b, int off, int len) {
        int n = read(b, off, len);
        return n == -1 ? 0 : n;
    }

    public byte[] readNBytes(int len) throws Exception {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        byte[] bytes = new byte[len];
        int result = read(bytes, 0, len);
        if (result == -1){
            throw new IOException("Required array size too large");
        } else {
            return bytes;
        }
    }
}
