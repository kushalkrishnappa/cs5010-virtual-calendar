import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.StandardFileWriter;

public class StandardFileWriterTest {

  private String fileName;

  @Before
  public void setUp() {
    fileName = "StandardFileWriterTestOut.txt";
    File file = new File(fileName);
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  public void test() throws IOException {
    StandardFileWriter writer = new StandardFileWriter();
    String absoluteFilePath = writer.write(fileName,
        "Test Data"
            + System.lineSeparator()
            + "This is a test paragraph."
            + System.lineSeparator()
            + "This is also a test paragraph.");

    File file = new File(fileName);
    assertTrue(file.exists());
    assertEquals(absoluteFilePath, file.getAbsolutePath());
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      assertEquals("Test Data", br.readLine());
      assertEquals("This is a test paragraph.", br.readLine());
      assertEquals("This is also a test paragraph.", br.readLine());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  @After
  public void tearDown() {
    File file = new File(fileName);
    if (file.exists()) {
      file.delete();
    }
  }
}