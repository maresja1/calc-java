import exception.BadExpressionFormatException;
import executing.FloatSolver;
import org.junit.Assert;
import org.junit.Test;
import parsing.TokenReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Jan Mares on 7.12.2014
 */
public class As3DataTest {
    private TokenReader createReader(String string){
        return new TokenReader(new StringReader(string));
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
        TokenReader reader = createReader("2/0");
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
}
