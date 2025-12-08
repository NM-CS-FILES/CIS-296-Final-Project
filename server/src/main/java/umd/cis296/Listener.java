package umd.cis296;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

public class Listener {

    private ServerSocketChannel socket;
    private ScheduledExecutorService scheduler;

    private static Listener INSTANCE = null;

    static {
        INSTANCE = new Listener();

        Logger.info("Creating Listener");

        try {
            INSTANCE.socket = ServerSocketChannel.open();
            INSTANCE.socket.bind(new InetSocketAddress(Configuration.instance().port));
            INSTANCE.socket.configureBlocking(false);
        } catch (IOException e) {
            Logger.error("Failed To Create Listener Server Socket Channel");
            e.printStackTrace();
        }

        INSTANCE.scheduler = Executors.newSingleThreadScheduledExecutor();
        INSTANCE.scheduler.scheduleAtFixedRate(() -> {
            try {
                SocketChannel clientChannel = INSTANCE.socket.accept();
                
                if (clientChannel != null) {
                    Socket client = clientChannel.socket();
                    Logger.info("Got Client `{}`", client.getInetAddress());
                    Factory.addMachine(new Machine(client));
                }
            } 
            catch (SocketTimeoutException e) {}
            catch (Exception ex) {
                Logger.error(ex, "Failed To Accept Client");
                INSTANCE.scheduler.shutdown();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        
        Logger.info("Listening...");
    }

    public static Listener instance() {
        return INSTANCE;
    }
}
