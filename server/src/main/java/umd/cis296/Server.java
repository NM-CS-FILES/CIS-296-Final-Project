package umd.cis296;

public class Server
{
    // "force" load classes so static blocks run
    private static void startup() {
      Configuration.instance();
      Broadcaster.instance();
      Listener.instance();
      Handler.instance();
    }

    public static void main(String[] args) throws Exception {
      startup();
      while (true);
    }
}
