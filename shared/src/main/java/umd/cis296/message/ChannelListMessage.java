package umd.cis296.message;

import java.util.Collection;
import java.util.Vector;

import umd.cis296.Message;
import umd.cis296.objects.Channel;

public class ChannelListMessage extends Message {
    private Vector<Channel> channels;

    public ChannelListMessage(Collection<Channel> channels) {
        this.channels = new Vector<>(channels);
    }

    public Vector<Channel> getChannels() {
        return channels;
    }

    public void setChannels(Vector<Channel> channels) {
        this.channels = channels;
    }
}
