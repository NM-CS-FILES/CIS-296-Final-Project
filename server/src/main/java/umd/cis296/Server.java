package umd.cis296;

public class Server
{
    // "force" load classes so static blocks run
    private static void startup() {
      Broadcaster.instance();
      Listener.instance();
      Handler.instance();
      Configuration.instance();
    }

    public static void main(String[] args) throws Exception {
      startup();

      while (true);
    }
}
