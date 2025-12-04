package umd.cis296;

import java.io.IOException;
import java.net.Socket;

import umd.cis296.states.InitialState;
import umd.cis296.events.ConnectedEvent;
import umd.cis296.events.MessageRecieveEvent;

public class Machine implements Runnable {

  private Context context;
  private State state;

  public Machine(Socket socket) throws IOException {
    this.context = new Context(new MessageSocket(socket));
    this.state = new InitialState();
  }

  @Override
  public void run() {
    this.state = this.state.handle(new ConnectedEvent(), this.context);

    while (!this.context.getSocket().isClosed()) {
      Message message = context.getSocket().read();

      if (message == null) {
        continue;
      }

      this.state = this.state.handle(new MessageRecieveEvent(message), this.context);
    }
  }


}
