package study.nio.socket.legacy;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class LegacyClient implements Runnable {
  
  private final Socket socket;
  private final DataOutputStream out;
  private boolean closed = false;
  
  public LegacyClient() {
    try {
      this.socket = new Socket("localhost", 1234);
      this.out = new DataOutputStream(this.socket.getOutputStream());
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }
  
  public void sendMessage(final String message) throws IOException {
    if ("exit".equals(message)) {
      closed = true;
    }
    
    out.writeUTF(String.format("[%s] says \"%s\"", Thread.currentThread().getName(), message));
    out.flush();
  }
  
  @Override
  public void run() {
    final int i = ThreadLocalRandom.current().nextInt(10);
  
    while (true) {
      try {
        sendMessage(i + Thread.currentThread().getName());
      }
      catch (IOException e) {
        e.printStackTrace();
      }
  
      if (closed || socket.isClosed()) {
        break;
      }
      
      
      
      try {
        TimeUnit.SECONDS.sleep(i);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  private static final int MAX_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
  
  public static void main(String[] args) throws IOException {
    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    
    IntStream.range(0, MAX_THREAD_COUNT).forEach(value -> {
      executorService.execute(new LegacyClient());
    });
  }
}
