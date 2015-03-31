import executing.Calc;
import org.junit.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link CodEx}.
 *
 * @author Jan Mares
 */
public class CodExAs4Test {

    private String readFile(String name) throws FileNotFoundException {
        return new Scanner(new File(name)).useDelimiter("\\Z").next();
    }

    private void testByData(int index) throws IOException {
        String inputName = "test/INPUT_AS4_"+index;
        String outputName = "test/OUTPUT_AS4_"+index;

        testByName(inputName, outputName);
    }

    private void testByName(String inputName, String outputName) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream("tmp");
        PrintStream out = new PrintStream(fileOutputStream);
        Calc.compute(new FileInputStream(inputName), out);
        String expected = readFile(outputName);
        String actual = readFile("tmp");
        assertEquals(expected,actual);
    }

    @Test
    public void testData1() throws IOException {
        testByData(1);
    }
}