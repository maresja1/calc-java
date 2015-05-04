import exception.BadExpressionFormatException;
import executing.FloatSolver;
import org.junit.Assert;
import org.junit.Test;
import parsing.TokenReader;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

/**
 * Created by Tom on 5.4.15.
 */
public class SemesterTest {
    private static TokenReader createReader(String string){
        return new TokenReader(new StringReader(string));
    }

    private static void testValidExpressions(String expressionString, Double... values) throws IOException {
        TokenReader reader = createReader(expressionString);
        FloatSolver floatSolver = new FloatSolver(reader);
        for (int i = 0; i < values.length; ++i)
        {
            Assert.assertEquals(new BigDecimal(values[i]).setScale(floatSolver.getPrecision(),BigDecimal.ROUND_HALF_UP), floatSolver.solveExpression(floatSolver.readExpression()));
        }
        Assert.assertEquals(null, floatSolver.readExpression());
    }

    @Test
    public void testFunctionDefinition () throws IOException {
        testValidExpressions(
                "DEF sqr(a) a * a;\n" +
                        "sqr(5+4);\n",
                81.0);
    }

    @Test
    public void testSemiColon() throws IOException {
        testValidExpressions("\n\n\n(7);;;\n(8);;;", 7.0, 8.0);
    }

    @Test(expected = BadExpressionFormatException.class)
    public void testSemiColon_2() throws IOException
    {
        testValidExpressions("2 +3 \n 5*4", 0.0);
    }

    @Test
    public void testCompoundStatement () throws IOException {
        testValidExpressions("DEF sqr(a) { b = a * a;  b; }\n" +
                             "sqr(5);\n" +
                             "DEF func(a, b, c) {a + b*c}\n" +
                             "DEF func_2 (a, b) {a/b}\n" +
                             "func_2(1, func(2, 3, func_2(8, 2)))*14;\n" +
                             "func_2(sqr(last), last);", 25.0, 1.0, 1.0);
    }

    @Test
    public void testGlobalVariable () throws IOException {
        testValidExpressions("a = 10;" +
                             "DEF addToA(b) a + b;\n" +
                             "addToA(0);" +
                             "a = addToA(last); addToA(1);", 10.0, 10.0, 20.0, 21.0);
    }

    private double factorial(int i)
    {
        double factVal = 1.0;
        for (int j = 2; j <= i; ++j)
        {
            factVal *= j;
        }
        return factVal;
    }

    @Test
    public void testFor () throws IOException {
        testValidExpressions("DEF factorial(n) {f = 1; for(i, 2, n - 1){f = f*i}; f}" +
                             "factorial(0); factorial(1); factorial(2); factorial(3); factorial(10);",
                             factorial(0), factorial(1), 2.0, 6.0, factorial(10));
    }

    @Test
    public void nestedFunctionCalls () throws IOException {
        testValidExpressions("DEF times(x, y) {x*y}" +
                             "DEF pow(x, n) {p = 1; for(i,1,n}{p = x*p}; p};" +
                             "DEF sqr(a) pow(a, 2);" +
                             "times(2, 5); pow(1, 0); sqr(3); pow(2,10);",
                              10.0, 1.0, 9.0, 1024.0);
    }

    @Test
    public void recursionTest () throws IOException {

    }
}