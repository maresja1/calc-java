import exception.BadExpressionFormatException;
import executing.Calc;
import executing.FloatSolver;
import org.junit.Assert;
import org.junit.Test;
import parsing.TokenReader;

import java.io.*;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Created by jan on 12/7/14.
 */
public class As4DataTest {
    private TokenReader createReader(String string){
        return new TokenReader(new StringReader(string));
    }

    private String readFile(String name) throws FileNotFoundException {
        return new Scanner(new File(name)).useDelimiter("\\Z").next();
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
    public void testDataAS4_1() throws IOException {
        testByDataAS4(1);
    }

    @Test
    public void testDataAS3_1() throws IOException {
        testByDataAS3(1);
    }

    @Test
    public void testDataAS3_3() throws IOException {
        testByDataAS3(3);
    }


    @Test
    public void test1() throws IOException {
        TokenReader reader = createReader("3 + 2");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test
    public void test2() throws IOException {
        TokenReader reader = createReader("1e2");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(100.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test
    public void test3() throws IOException {
        TokenReader reader = createReader("2 * ( 3 + 1 )");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(8.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test
    public void test4() throws IOException {
        TokenReader reader = createReader("1.2 + 4.3");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(5.5, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test5() throws IOException {
        TokenReader reader = createReader("\n3 ** 5");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(5.5, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test
    public void test6() throws IOException {
        TokenReader reader = createReader("2*(3+1)");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(8.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test
    public void test7() throws IOException {
        TokenReader reader = createReader("22 * ( 3 + 1 )\n1e2");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(88.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(100.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test
    public void test8() throws IOException {
        TokenReader reader = createReader("2+2*3");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(8.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test
    public void test9() throws IOException {
        TokenReader reader = createReader("\n\n\n(7)\n\n\n\n(8)");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(7.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(8.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test10() throws IOException {
        TokenReader reader = createReader("\n\n\n\n()");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(8.0, floatExpression.solveExpression(floatExpression.readExpression()));;
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test11() throws IOException {
        TokenReader reader = createReader("\n\n\n\n2 3");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(3.0, floatExpression.solveExpression(floatExpression.readExpression()));;
    }

    @Test
    public void test12() throws IOException {
        TokenReader reader = createReader("\n\n\n\n1++4");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(null, floatExpression.readExpression());;
    }
    @Test(expected = BadExpressionFormatException.class)
    public void test13() throws IOException {
        TokenReader reader = createReader("\n\n\n\n1++");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(3.0, floatExpression.solveExpression(floatExpression.readExpression()));;
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test14() throws IOException {
        TokenReader reader = createReader("\n\n\n\n+");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(3.0, floatExpression.solveExpression(floatExpression.readExpression()));;
    }

    @Test
    public void test15() throws IOException {
        TokenReader reader = createReader("\n\n\n\n2*+3");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(6.0, floatExpression.solveExpression(floatExpression.readExpression()));
    }


    @Test(expected = BadExpressionFormatException.class)
    public void test16() throws IOException {
        TokenReader reader = createReader("\n\n\n\n(2");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(6.0, floatExpression.solveExpression(floatExpression.readExpression()));
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test17() throws IOException {
        TokenReader reader = createReader("\n\n\n\n(2 2 2");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(6.0, floatExpression.solveExpression(floatExpression.readExpression()));
    }

    @Test
    public void test18() throws IOException {
        TokenReader reader = createReader("2-2*3");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(-4.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test19() throws IOException {
        TokenReader reader = createReader("2\t/\t0");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(-4.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(null, floatExpression.readExpression());;
    }


    @Test
    public void test20() throws IOException {
        TokenReader reader = createReader("");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(null, floatExpression.readExpression());;
    }

    @Test
    public void test21() throws IOException {
        TokenReader reader = createReader("\n\n\n\n");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(null, floatExpression.readExpression());;
    }


    @Test
    public void test22() throws IOException {
        TokenReader reader = createReader("DEF sqr(a) a * a\nsqr(5)");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(25.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test
    public void test23() throws IOException {
        TokenReader reader = createReader(
                "DEF sqr(a) a * a\n" +
                "sqr(5)\n" +
                "DEF\tfoo(a,b)\t 2*a\t\t + 2*b\n" +
                "last\n" +
                "foo(1,1)"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(25.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(0.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(4.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test
    public void test24() throws IOException {
        TokenReader reader = createReader(
                "y = 5\ny"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test25() throws IOException {
        TokenReader reader = createReader(
                "DEF sqr(a) a * a\n" +
                "sqr(5+4)\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test26() throws IOException {
        TokenReader reader = createReader(
                "b = a = 2\n"+
                "a\n"+
                "b\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Pri pokusu zavoalt neexistujici funkci se vypise error
    @Test(expected = BadExpressionFormatException.class)
    public void test27() throws IOException {
        TokenReader reader = createReader(
                "error()"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test
    public void test28() throws IOException {
        TokenReader reader = createReader(
                "DEF sqr() 1\n" +
                "sqr()\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Last obsahuje hodnotu posledniho vyrazu
    @Test
    public void test29() throws IOException {
        TokenReader reader = createReader(
                "5\nlast=2\nlast\nlast\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Ve funkci lze pouzit pouze argumenty
    @Test(expected = BadExpressionFormatException.class)
    public void test30() throws IOException {
        TokenReader reader = createReader(
                "a=5\nDEF f() a\nf()\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Ve funkci lze pouzit pouze argumenty
    @Test(expected = BadExpressionFormatException.class)
    public void test30_b() throws IOException {
        TokenReader reader = createReader(
                "a=5\nDEF f() a\n5\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Prirazeni neni vyraz - nemuze figurovat v jinych vyrazech
    @Test(expected = BadExpressionFormatException.class)
    public void test31() throws IOException {
        TokenReader reader = createReader(
                "b + a = 2"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Po prirazenı do promenne je v last ulozena hodnota teto promenne
    @Test
    public void test32() throws IOException {
        TokenReader reader = createReader(
                "b = 2\nlast"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Pri definici funkce nesmi existovat jiz takova promenna
    @Test(expected = BadExpressionFormatException.class)
    public void test33() throws IOException {
        TokenReader reader = createReader(
                "b = 2\nDEF b() 1\nb()"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Pri definici promenna nesmi existovat jiz takova funkce
    @Test(expected = BadExpressionFormatException.class)
    public void test33_b() throws IOException {
        TokenReader reader = createReader(
                "DEF b() 1\nb = 2\nb()"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Pri definici promenna nesmi existovat jiz takova funkce
    @Test(expected = BadExpressionFormatException.class)
    public void test33_c() throws IOException {
        TokenReader reader = createReader(
                "DEF b() 1\na +b"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test34() throws IOException {
        TokenReader reader = createReader(
                "DEF b() 1 1\nb()"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test
    public void test35() throws IOException {
        TokenReader reader = createReader(
                "1+1\n1 1\nlast"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        try{
            floatExpression.readExpression();
            Assert.fail();
        } catch (BadExpressionFormatException e){

        }
        Assert.assertEquals(0.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test
    public void test36() throws IOException {
        TokenReader reader = createReader(
                "Ahoj=1\nahoj=2\nahoJ=3\nAhoj\nahoj\nahoJ"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(3.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(3.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test
    public void test37() throws IOException {
        TokenReader reader = createReader(
                "DEFsqr(a)a*a\n" +
                        "sqr(5)\n" +
                        "DEFfoo(a,b)2*a+2*b\n" +
                        "last\n" +
                        "foo(1,1)"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(25.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(0.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(4.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test38() throws IOException {
        TokenReader reader = createReader(
                "DEF sqr()\n 1\n" +
                        "sqr()\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test39() throws IOException {
        TokenReader reader = createReader(
                "DEF\n sqr() 1\n" +
                        "sqr()\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test40() throws IOException {
        TokenReader reader = createReader(
                "DEF abc() 2\n" +
                        "abc()\n" +
                "DEF sqr() abc()\n" +
                        "sqr()\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        floatExpression.readExpression();
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test41() throws IOException {
        TokenReader reader = createReader(
                "DEF sqr() 1\n" +
                        "sqr(1)\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    //Po startu je v last 0
    @Test
    public void test42() throws IOException {
        TokenReader reader = createReader(
                "last\nlast\nlast\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(0.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(0.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(0.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test43() throws IOException {
        TokenReader reader = createReader(
                "sqr()\n" +
                "DEF sqr() 1\n" +
                "sqr()\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test
    public void test44() throws IOException {
        TokenReader reader = createReader(
                "DEF a() 1\n" +
                "DEF b() 2\n" +
                "a()\n" +
                "b()\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test45() throws IOException {
        TokenReader reader = createReader(
                        "DEF last() 1\n" +
                        "last()\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(1.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }
    @Test
    public void test46() throws IOException {
        TokenReader reader = createReader(
                "DEF foo() 1\n" +
                "foo() + 1\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(2.0, floatExpression.solveExpression(floatExpression.readExpression()));
        Assert.assertEquals(null, floatExpression.readExpression());
    }
}
