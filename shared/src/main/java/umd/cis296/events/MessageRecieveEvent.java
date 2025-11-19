package umd.cis296.events;

import umd.cis296.Event;
import umd.cis296.Message;

public class MessageRecieveEvent implements Event {
  private Message message;

  public MessageRecieveEvent(Message message) {
    this.message = message;
  }

  public Message getMessage() {
    return this.message;
  }
}
