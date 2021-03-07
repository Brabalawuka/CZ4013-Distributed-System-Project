package com.company.cz4013;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {


    static final int SERVER_PORT = 22222;

    public static void main(String[] args) {


        try {
            MainUDPServer mainUDPServer = new MainUDPServer(new DatagramSocket(SERVER_PORT));
            mainUDPServer.listen();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        // write your code here
    }
}
