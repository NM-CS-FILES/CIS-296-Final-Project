package umd.cis296;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{

    public static final int PORT = 5050;

    public static void main(String[] args) {
        System.out.println("Starting server...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            System.out.println("Waiting for clients...");

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}