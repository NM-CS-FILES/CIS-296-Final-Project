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
    private DatagramPacket packet;
    private BeaconMessage beacon;
    private ScheduledExecutorService scheduler;

    private static Broadcaster INSTANCE;

    static {
        INSTANCE = new Broadcaster();
        
        try {
            INSTANCE.initializeSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    private byte[] getPacketBytes() throws Exception {
        if (beacon == null) {
            beacon = new BeaconMessage(
                Configuration.instance().port, 
                0,
                Configuration.instance().name
            );
        }

        beacon.setUsers(Handler.instance().getUsers().size());

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

        objOut.writeObject(beacon);
        objOut.flush();

        return byteOut.toByteArray();
    }

    private DatagramPacket getPacket() throws Exception {
        byte[] packetData = getPacketBytes();

        if (packet == null) {
            packet = new DatagramPacket(
                packetData, 
                packetData.length, 
                InetAddress.ofLiteral("255.255.255.255"), 
                Configuration.instance().port
            );
        } else {
            packet.setData(packetData);
        }

        return packet;
    }

    private void initializeSocket() throws IOException {
        Logger.info("Creating Broadcaster");

        socket = new DatagramSocket();
        socket.setBroadcast(true);

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                socket.send(getPacket());
                Logger.info("Broadcasted Beacon");
            } catch (Exception e) {
                Logger.warn(e);
                Logger.warn("Broadcasting Failed");
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public static Broadcaster instance() {
        return INSTANCE;
    }
}
