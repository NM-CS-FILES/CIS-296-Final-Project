package umd.cis296.objects;

import java.nio.ByteBuffer;

public class ChannelMessage extends Idable {
    public enum Type {
        TEXT,
        IMAGE,
        FILE
    }

    public int fromId;
    public Type type;
    public ByteBuffer content;

    public ChannelMessage(int id, int fromId, Type type, byte[] content) {
        super(id);
        this.fromId = fromId;
        this.type = type;
        this.content = ByteBuffer.wrap(content);
    }
}
