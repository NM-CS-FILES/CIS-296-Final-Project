package umd.cis296;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class MessageSocket {
  private class MessageBuffer {
    private Queue<Message> messages = new LinkedList<Message>();

    public synchronized void push(Message message) {
      this.messages.add(message);
    }

    public synchronized Message pop() {
      return this.messages.poll();
    }

    public synchronized int size() {
      return this.messages.size();
    }
  }

  //
  //

  private Socket socket;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private MessageBuffer buffer;
  private Thread readThread;

  public MessageSocket(Socket socket) throws IOException {
    this.socket = socket;
    this.output = new ObjectOutputStream(this.socket.getOutputStream());
    this.input = new ObjectInputStream(this.socket.getInputStream());
    this.buffer = new MessageBuffer();
    this.readThread = new Thread(() -> {
      try {
        while (!socket.isClosed()) {
          Object object = this.input.readObject();

          if (!(object instanceof Message)) {
            throw new Exception();
          }

          buffer.push((Message) object);
        }
      } catch (Exception ex) {
      }
    });
    this.readThread.start();
  }

  public void send(Message message) throws IOException {
    this.output.writeObject(message);
    this.output.flush();
  }

  public Message read() {
    return this.buffer.size() == 0 ? null : this.buffer.pop();
  }

  public int waitingMessageCount() {
    return this.buffer.size();
  }

  public boolean isClosed() {
    return this.socket.isClosed();
  }


}
