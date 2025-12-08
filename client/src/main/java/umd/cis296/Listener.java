package umd.cis296;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import umd.cis296.message.BeaconMessage;

public class Listener {

    private ConcurrentHashMap<InetSocketAddress, BeaconMessage> beacons;
    private DatagramChannel channel;
    private ByteBuffer messageBuffer;
    private ScheduledExecutorService scheduler; 

    private static Listener INSTANCE;

    static {
        try {
            INSTANCE = new Listener();
            INSTANCE.channel = DatagramChannel.open();
            INSTANCE.channel.bind(new InetSocketAddress(5005));
            INSTANCE.messageBuffer = ByteBuffer.allocate(0xFFFF);
            INSTANCE.scheduler = Executors.newSingleThreadScheduledExecutor();
            INSTANCE.beacons = new ConcurrentHashMap<>();
            INSTANCE.scheduler.scheduleAtFixedRate(() -> {
                try {
                    InetSocketAddress from = (InetSocketAddress)INSTANCE.channel.receive(INSTANCE.messageBuffer);

                    if (from != null) {
                        ByteArrayInputStream bytesIn = new ByteArrayInputStream(INSTANCE.messageBuffer.array());
                        ObjectInputStream objIn = new ObjectInputStream(bytesIn);

                        Object obj = objIn.readObject();

                        if (!(obj instanceof BeaconMessage)) {
                            throw new Exception();
                        }

                        INSTANCE.beacons.put(from, (BeaconMessage)obj);   
                    }
                } catch (Exception ex) {

                }
            }, 0, 100, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            
        }
    }

    public static Listener instance() {
        return INSTANCE;
    }

    public ConcurrentHashMap<InetSocketAddress, BeaconMessage> beacons() {
        return this.beacons;
    }
    
}
