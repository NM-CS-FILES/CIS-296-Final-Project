package umd.cis296.message;

import java.net.InetAddress;

import umd.cis296.Message;

public class BeaconMessage extends Message {
    public int port;
    public int users;
    public String name;
    
    public BeaconMessage(int port, int users, String name) {
        this.port = port;
        this.users = users;
        this.name = name;
    }
}
