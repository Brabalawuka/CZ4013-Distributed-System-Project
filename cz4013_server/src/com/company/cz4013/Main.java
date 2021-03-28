package com.company.cz4013;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;

public class Main {


    static final int SERVER_PORT = 22222;

    public static MainUDPServer mainUDPServer;

    /**
     * Entrance of the program
     */
    public static void main(String[] args) {
        try {
            Timer t = new Timer();
            Data data = new Data();

            Calendar calEnd = new GregorianCalendar();
            calEnd.setTime(new Date());
            calEnd.set(Calendar.DAY_OF_YEAR, calEnd.get(Calendar.DAY_OF_YEAR)+1);
            calEnd.set(Calendar.HOUR_OF_DAY, 0);
            calEnd.set(Calendar.MINUTE, 0);
            calEnd.set(Calendar.SECOND, 0);
            calEnd.set(Calendar.MILLISECOND, 0);
            Date midnightTonight = calEnd.getTime();

            t.schedule(data, midnightTonight, 86400000);

            mainUDPServer = new MainUDPServer(new DatagramSocket(SERVER_PORT));
            mainUDPServer.listen();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }
}
