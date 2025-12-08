package umd.cis296.objects;

import java.io.Serializable;

public abstract class Idable implements Serializable {
    private int id;

    public Idable(int id) {
        this.id = id;
    }

    public Idable() {
        this(Integer.MIN_VALUE);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
