package umd.cis296.objects;

public class Channel extends Idable {

    public enum Type {
        TEXT
    }

    //
    //

    private String name;
    private int type;

    public Channel(int id, String name, Type type) {
        super(id);
        this.name = name;
        this.type = type.ordinal();
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return Type.values()[this.type];
    }
}
