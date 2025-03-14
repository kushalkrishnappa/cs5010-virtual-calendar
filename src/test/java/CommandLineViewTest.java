import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import exception.DisplayException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import view.CommandLineView;

/**
 * This is a test class for the CommandLineView class.
 */
public class CommandLineViewTest {
  private MockReadable mockInput;
  private MockAppendable mockOutput;
  private CommandLineView view;

  // Mock Appendable
  private static class MockAppendable implements Appendable {
    private final List<String> content;
    private boolean shouldThrowIOException;

    MockAppendable() {
      content = new ArrayList<>();
      shouldThrowIOException = false;
    }

    public void setShouldThrowIOException(boolean shouldThrow) {
      this.shouldThrowIOException = shouldThrow;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
      if (shouldThrowIOException) {
        throw new IOException("Simulated IO Exception");
      }
      content.add(csq.toString());
      return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
      if (shouldThrowIOException) {
        throw new IOException("Simulated IO Exception");
      }
      content.add(csq.subSequence(start, end).toString());
      return this;
    }

    @Override
    public Appendable append(char c) throws IOException {
      if (shouldThrowIOException) {
        throw new IOException("Simulated IO Exception");
      }
      content.add(String.valueOf(c));
      return this;
    }

    public List<String> getContent() {
      return content;
    }
  }


  // Mock Readable
  private static class MockReadable implements Readable {
    @Override
    public int read(java.nio.CharBuffer cb) throws IOException {
      return 0;
    }
  }

  @Before
  public void setUp() {
    mockInput = new MockReadable();
    mockOutput = new MockAppendable();
    view = new CommandLineView(mockInput, mockOutput);
  }

  @Test
  public void testConstructorNullInput() {
    try {
      new CommandLineView(null, mockOutput);
      fail("Should throw NullPointerException for null input");
    } catch (NullPointerException e) {
      assertEquals("InputStream cannot be null", e.getMessage());
    }
  }

  @Test
  public void testConstructorNullOutput() {
    try {
      new CommandLineView(mockInput, null);
      fail("Should throw NullPointerException for null output");
    } catch (NullPointerException e) {
      assertEquals("OutputStream cannot be null", e.getMessage());
    }
  }

  @Test
  public void testDisplayMessageSuccess() {
    String message = "Test message";
    view.displayMessage(message);
    assertEquals(1, mockOutput.getContent().size());
    assertEquals(message, mockOutput.getContent().get(0));
  }

  @Test
  public void testDisplayErrorSuccess() {
    String error = "Test error";
    view.displayError(error);
    assertEquals(1, mockOutput.getContent().size());
    assertEquals(error, mockOutput.getContent().get(0));
  }

  @Test
  public void testGetInputStream() {
    assertSame(mockInput, view.getInputStream());
  }

  @Test
  public void testDisplayMessageWithIOException() {
    mockOutput.setShouldThrowIOException(true);
    String message = "Test message";
    try {
      view.displayMessage(message);
      fail("Should throw DisplayException when IOException occurs");
    } catch (DisplayException e) {
      assertEquals("Error displaying message: " + message, e.getMessage());
    }
  }

  @Test
  public void testDisplayErrorWithIOException() {
    mockOutput.setShouldThrowIOException(true);
    String error = "Test error";
    try {
      view.displayError(error);
      fail("Should throw DisplayException when IOException occurs");
    } catch (DisplayException e) {
      assertEquals("Error displaying error: " + error, e.getMessage());
    }
  }
}