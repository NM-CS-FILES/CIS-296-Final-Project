package umd.cis296;

import java.io.IOException;
import java.net.Socket;

public class Client
{

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 5050;

    public static void main(String[] args) {
        ClientGUI.launch(ClientGUI.class, args);
    }
}
