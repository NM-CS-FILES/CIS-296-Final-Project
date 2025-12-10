package umd.cis296;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import umd.cis296.message.BeaconMessage;

public class Broadcaster {
    
    private DatagramSocket socket;
    private DatagramPacket message;
    private ScheduledExecutorService scheduler;

    private static Broadcaster INSTANCE;

    static {
        try {
            createBroadcaster();
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }

    private static void createMessage() throws IOException {
        Logger.info("Creating Message");

        BeaconMessage beacon = new BeaconMessage(
            Configuration.instance().port,
            0,
            Configuration.instance().name
        );

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

        objOut.writeObject(beacon);
        objOut.flush();

        byte[] bytes = byteOut.toByteArray();

        INSTANCE.message = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("255.255.255.255"), beacon.getPort());
    }

    private static void createBroadcaster() throws IOException {
        INSTANCE = new Broadcaster();
        
        createMessage();

        Logger.info("Creating Broadcaster");

        INSTANCE.socket = new DatagramSocket();
        INSTANCE.socket.setBroadcast(true);

        INSTANCE.scheduler = Executors.newSingleThreadScheduledExecutor();
        INSTANCE.scheduler.scheduleAtFixedRate(() -> {
            try {
                INSTANCE.socket.send(INSTANCE.message);
                Logger.info("Broadcasted Beacon");
            } catch (IOException e) {
                Logger.warn(e);
                Logger.warn("Broadcasting Failed");
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public static Broadcaster instance() {
        return INSTANCE;
    }
}
