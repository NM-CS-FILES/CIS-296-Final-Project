package umd.cis296;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import umd.cis296.message.UserJoinMessage;
import umd.cis296.objects.User;

public class Handler {

    private Map<Connection, Thread> connections;

    private static Handler INSTANCE;

    static {
        INSTANCE = new Handler();
        INSTANCE.connections = new HashMap();
    }

    public static Handler instance() {
        return INSTANCE;
    }

    public void broadcast(Message message) {
        connections.forEach((connection, thread) -> {
            try {
                connection.getSocket().send(message);
            } catch (IOException e) { }
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
