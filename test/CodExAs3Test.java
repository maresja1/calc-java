import static org.junit.Assert.assertEquals;

import com.sun.org.apache.bcel.internal.classfile.Code;
import executing.Calc;
import org.junit.Test;
import org.junit.Ignore;

import java.io.*;
import java.util.Scanner;

/**
 * Tests for {@link CodEx}.
 *
 * @author Jan Mares
 */
public class CodExAs3Test {

    private String readFile(String name) throws FileNotFoundException {
        return new Scanner(new File(name)).useDelimiter("\\Z").next();
    }

    private void testByData(int index) throws IOException {
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
        assertEquals(expected,actual);
    }

    @Test
    public void testData1() throws IOException {
        testByData(1);
    }


    @Test
    public void testData2() throws IOException {
        testByData(2);
    }

    @Test
    public void testData3() throws IOException {
        testByData(3);
    }

//    @Test
//    public void testData4() throws IOException {
//        testByData(4);
//    }


    @Test
    public void testDataGen() throws IOException {
        testByName("test/INPUT_GEN_1","test/OUTPUT_GEN_1");
    }
}