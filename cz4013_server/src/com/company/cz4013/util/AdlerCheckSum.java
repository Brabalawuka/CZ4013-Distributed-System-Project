package com.company.cz4013.util;

import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class AdlerCheckSum {


    public static int checkSum(byte[] input){
        Checksum checksumEngine = new Adler32();
        checksumEngine.update(input, 0, input.length);
        int checksum = (int)checksumEngine.getValue();
        System.out.println(checksum);
        return checksum;
    }
}
