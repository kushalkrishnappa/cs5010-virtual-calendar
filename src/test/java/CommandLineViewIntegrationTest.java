import static org.junit.Assert.assertTrue;

import controller.CalendarController;
import controller.ControllerMode;
import controller.IController;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import model.CalendarModel;
import org.junit.Before;
import org.junit.Test;
import view.CommandLineView;

public class CommandLineViewIntegrationTest {

  private IController controller;
  private final PipedOutputStream pipedInput;
  private final Readable input;
  private final StringBuilder output;
  private final BlockingQueue<String> outputSignal;

  public CommandLineViewIntegrationTest() {
    Readable readable;
    readable = null;
    pipedInput = new PipedOutputStream();
    try {
      readable = new InputStreamReader(new PipedInputStream(pipedInput));
    } catch (IOException e) {
      System.out.println("Error opening piped input stream");
    }
    input = readable;
    output = new StringBuilder();
    outputSignal = new LinkedBlockingQueue<>();
  }

  @Before
  public void setUp() throws InterruptedException {
    controller = new CalendarController(
        new CalendarModel(),
        new CommandLineView(input, output),
        ControllerMode.INTERACTIVE
    );
    Thread appThread = new Thread(() -> controller.run());

    Thread monitorThread = new Thread(() -> {
      int previousLength = 0;
      while (appThread.isAlive()) {
        if (output.length() > previousLength) {
          outputSignal.add("outputAvailable");
          previousLength = output.length();
        }
        try {
          Thread.sleep(10); // Small sleep to avoid busy-waiting.
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return;
        }
      }
      if(output.length() > 0){
        outputSignal.add("outputAvailable");
      }

    });

    appThread.start();
    monitorThread.start();
    outputSignal.take(); // for the first user prompt (calApp> )
  }

  @Test
  public void test() throws IOException, InterruptedException {
    pipedInput.write(
        "create event --autoDecline event1 from 2025-04-01T12:00 to 2025-04-01T01:00\n".getBytes());
    outputSignal.take();
    assertTrue(output.toString().contains("Start time cannot be after end time"));
  }

  @Test
  public void test2() throws IOException, InterruptedException {
    pipedInput.write(
        "create event --autoDecline event1 from 2025-04-01T12:00 to 2025-04-01T13:00\n".getBytes());
    outputSignal.take();
    assertTrue(output.toString().contains("Successfully created event event1"));
  }


}
