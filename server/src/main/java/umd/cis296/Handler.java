package umd.cis296;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import umd.cis296.message.IAmMessage;
import umd.cis296.message.UserJoinMessage;
import umd.cis296.message.UserLeaveMessage;
import umd.cis296.objects.User;

public class Handler {

    private Map<Connection, Thread> connections;
    private ScheduledExecutorService scheduler;

    private static Handler INSTANCE;

    static {
        INSTANCE = new Handler();
        INSTANCE.connections = new HashMap();
        INSTANCE.scheduler = Executors.newSingleThreadScheduledExecutor();
        INSTANCE.scheduler.scheduleAtFixedRate(() -> INSTANCE.prune(), 0, 1, TimeUnit.SECONDS);
    }

    public static Handler instance() {
        return INSTANCE;
    }

    private void prune() {
        List<Connection> toPrune = new ArrayList<>();

        connections.forEach((connection, thread) -> {
            boolean success = connection.getSocket().send(new IAmMessage(Configuration.instance().name));

            if (!success) {
                toPrune.add(connection);
            }
        });

        toPrune.forEach((connection) -> {
            connection.getSocket().close();
            connection.setFlag(false);
            connections.remove(connection);

            broadcast(new UserLeaveMessage(connection.getUser()));

            Logger.info("Pruned Client {}:{}", connection.getUser().getName(), connection.getUser().getAddress());
        });
    }

    public void broadcast(Message message) {
        connections.forEach((connection, thread) -> {
            connection.getSocket().send(message);
        });
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        connections.forEach((connection, thread) -> {
            users.add(connection.getUser());
        });

        return users;
    }

    public void addConnection(Connection connection) {
        Thread thread = new Thread(connection);
        thread.start();
        connections.put(connection, thread);

        Logger.info("Accepted Client {}:{}", connection.getUser().getName(), connection.getUser().getAddress());
        
        broadcast(new UserJoinMessage(connection.getUser()));
    }
}
