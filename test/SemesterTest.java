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
        Assert.assertEquals(null, floatSolver.solveExpression(floatSolver.readExpression()));
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
        TokenReader reader = createReader("\n\n\n(7);;;\n(8);;;");
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(7.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(8.0, floatExpression.solveExpression(floatExpression.readExpression()));;
        Assert.assertEquals(null, floatExpression.readExpression());;
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
}