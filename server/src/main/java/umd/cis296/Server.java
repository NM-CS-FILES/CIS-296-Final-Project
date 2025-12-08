package umd.cis296;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import umd.cis296.Database.Database;
import umd.cis296.Database.Table;
import umd.cis296.objects.Channel;

public class Server
{
    // "force" load classes so static blocks run
    private static void startup() {
      Broadcaster.instance();
      Listener.instance();
      Factory.instance();
      Database.instance();
    }

    public static void main(String[] args) throws Exception {
      startup();

      while (true);
    }
}
