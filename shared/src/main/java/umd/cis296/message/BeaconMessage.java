package umd.cis296.message;

import java.net.InetAddress;

import umd.cis296.Message;

public class BeaconMessage extends Message {
    private InetAddress address;
    private int port;
    private int users;
    private String name;

    public BeaconMessage(InetAddress address, int port, int users, String name) {
        this.address = address;
        this.port = port;
        this.users = users;
        this.name = name;
    }

    public BeaconMessage(int port, int users, String name) {
        this(InetAddress.getLoopbackAddress(), port, users, name);
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        // collisions???
        return address.hashCode() + port + name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BeaconMessage message) {
            return message.address.equals(this.address) 
                && message.name.equals(this.name)
                && message.port == this.port;
        }

        return false;
    }
}
