import executing.Calc;
import org.junit.Assert;
import org.junit.Test;
import parsing.TokenReader;

import java.io.*;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Created by jan on 12/7/14.
 */
public class As5DataTest {
    private TokenReader createReader(String string){
        return new TokenReader(new StringReader(string));
    }

    private String readFile(String name) throws FileNotFoundException {
        return new Scanner(new File(name)).useDelimiter("\\Z").next();
    }

    private void testByDataAS5(int index) throws IOException {
        String inputName = "test/INPUT_AS5_"+index;
        String outputName = "test/OUTPUT_AS5_"+index;

        testByName(inputName, outputName);
    }

    private void testByDataAS4(int index) throws IOException {
        String inputName = "test/INPUT_AS4_"+index;
        String outputName = "test/OUTPUT_AS4_"+index;

        testByName(inputName, outputName);
    }

    private void testByDataAS3(int index) throws IOException {
        String inputName = "test/INPUT_AS3_"+index;
        String outputName = "test/OUTPUT_AS3_"+index;

        testByName(inputName, outputName);
    }

    private void testByName(String inputName, String outputName) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream("tmp");
        PrintStream out = new PrintStream(fileOutputStream);
        Calc.compute(new FileInputStream(inputName), out);
        String expected = readFile(outputName);
        String actual = readFile("tmp");
        assertEquals(expected, actual);
    }


    @Test
    public void testDataAS5_1() throws IOException {
        testByDataAS5(1);
    }
}
