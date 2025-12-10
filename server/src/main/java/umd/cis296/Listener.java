package umd.cis296;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.tinylog.Logger;

public class Listener {

    private ServerSocketChannel socket;
    private boolean flag;
    private Thread thread;

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

        INSTANCE.flag = true;
        INSTANCE.thread = new Thread(() -> {
            while (INSTANCE.flag) {
                try {
                    SocketChannel clientChannel = INSTANCE.socket.accept();
                
                    if (clientChannel != null) {
                        Socket client = clientChannel.socket();
                        Connection connection = Connection.fromSocket(new MessageSocket(client));

                        if (connection != null) {
                            Handler.instance().addConnection(connection);
                        }
                    }
                } 
                catch (SocketTimeoutException e) {}
                catch (Exception ex) {
                    Logger.error(ex, "Failed To Accept Client");
                }
            }
        });
        INSTANCE.thread.start();

        Logger.info("Listening...");
    }

    public static Listener instance() {
        return INSTANCE;
    }
}
