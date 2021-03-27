package com.company.cz4013.util;

import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class AdlerCheckSum {


    public static long checkSum(byte[] input){
        Checksum checksumEngine = new Adler32();
        checksumEngine.update(input, 0, input.length);
        long checksum = checksumEngine.getValue();
        System.out.println(checksum);
        return checksum;
    }
}
