package com.company.cz4013.util;

import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 * A util class to get the hashed result of bytes using Adler32 algorithm
 */
public class AdlerCheckSum {

    /**
     * Hash the input bytes using Adler32 algorithm
     * @param input bytes to be hashed
     * @return hashed result in long format
     */
    public static long checkSum(byte[] input){
        Checksum checksumEngine = new Adler32();
        checksumEngine.update(input, 0, input.length);
        long checksum = checksumEngine.getValue();
        //System.out.println(checksum);
        return checksum;
    }
}
