package umd.cis296.message;

import umd.cis296.Message;

public class IAmMessage extends Message {
    public String name;

    public IAmMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
