package umd.cis296.message;

import umd.cis296.Message;
import umd.cis296.objects.Channel;
import umd.cis296.objects.User;

public class TextMessage extends Message {
    private Channel channel;
    private User user;
    private String text;

    public TextMessage(Channel channel, User user, String text) {
        this.channel = channel;
        this.user = user;
        this.text = text;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
