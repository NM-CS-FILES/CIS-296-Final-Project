package umd.cis296;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import umd.cis296.message.ChannelListMessage;
import umd.cis296.message.IAmMessage;
import umd.cis296.message.RequestChannelListMessage;
import umd.cis296.message.RequestUserListMessage;
import umd.cis296.message.TextMessage;
import umd.cis296.message.UserListMessage;
import umd.cis296.objects.Channel;
import umd.cis296.objects.User;

public class Connection implements Runnable {
    private MessageSocket socket;
    private User user;
    private boolean flag = true;
    
    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public MessageSocket getSocket() {
        return socket;
    }

    public User getUser() {
        return user;
    }

    private void handleTextMessage(TextMessage message) {
        Logger.info("{} Got Message", getUser().getName());

        Handler.instance().broadcast((Message)message);
    }

    private void handleRequestChannelList(RequestChannelListMessage message) {
        Logger.info("{} Got Channel List Request", getUser().getName());

        try {
            List<Channel> channels = new ArrayList();

            Configuration.instance().channels.forEach((name) -> {
                channels.add(new Channel(name));
            });

            socket.send(new ChannelListMessage(channels));
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
    }

    private void handleRequestUserList(RequestUserListMessage message) {
        Logger.info("{} Got User List Request", getUser().getName());

        try {
            socket.send(new UserListMessage(Handler.instance().getUsers()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleMessage(Message in) {
        switch ((Object)in) {
            case TextMessage message               -> handleTextMessage(message);
            case RequestChannelListMessage message -> handleRequestChannelList(message);
            case RequestUserListMessage message    -> handleRequestUserList(message);
            default -> { }
        }
    }

    @Override
    public void run() {
        Logger.info("{} Running", getUser().getName());
        
        while (flag) {
            Message message = null;

            while ((message = socket.read()) != null) {
                handleMessage(message);
            }
        }
    }

    public static Connection fromSocket(MessageSocket socket) {
        IAmMessage iam = socket.waitFor(IAmMessage.class, 3000);

        if (iam == null) {
            socket.close();
            return null;
        }

        Connection connection = new Connection();

        connection.socket = socket;
        connection.user = new User(iam.name, socket.getAddress());

        return connection;
    }
}
