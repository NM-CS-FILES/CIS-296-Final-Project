package umd.cis296;

public class Context {
  private MessageSocket socket;

  public Context(MessageSocket socket) {
    this.socket = socket;
  }

  public MessageSocket getSocket() {
    return this.socket;
  }
}
