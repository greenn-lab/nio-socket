package study.nio.socket.legacy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LegacyServer extends Thread {
  
  private final Socket client;
  
  public LegacyServer(Socket client) {
    this.client = client;
  }
  
  @Override
  public void run() {
    System.err.printf("%s standby...\n", getName());

    try (
        final DataOutputStream out = new DataOutputStream(client.getOutputStream());
        final DataInputStream in = new DataInputStream(client.getInputStream())
    ) {
      while (true) {
        final String message = in.readUTF();
        
        if ("exit".equals(message)) {
          client.close();
          break;
        }
  
        System.out.printf("receive: %s\n", message);
        out.writeUTF(String.format("%s`s echo \"%s\"\n", getName(), message));
        out.flush();
      }
    }
    catch (IOException e) {
      System.err.printf("%s closed!\n", getName());
    }
  }
  
  public static void main(String[] args) throws IOException {
    try (
        final ServerSocket server = new ServerSocket(1234)
    ) {
      
      Socket client;
      while ((client = server.accept()) != null) {
        new LegacyServer(client).start();
      }
    }
  }
}
