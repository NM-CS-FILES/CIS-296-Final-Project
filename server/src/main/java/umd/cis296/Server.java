package umd.cis296;

import umd.cis296.Database.Database;
import umd.cis296.Database.Table;
import umd.cis296.objects.Channel;

public class Server {

  public static void main(String[] args) throws Exception {
    Table<Channel> channelTable = Database.getTable("Channels", Channel.class);

    Channel ch = new Channel(100, "New Name", Channel.Type.TEXT);

    channelTable.delete(ch);
  }
}
