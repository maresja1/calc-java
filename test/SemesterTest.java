import executing.FloatSolver;
import expression.IExpression;
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

    private static void testFirstValidExpression(String expressionString, Double value) throws IOException {
        TokenReader reader = createReader(expressionString);
        FloatSolver floatSolver = new FloatSolver(reader);
        BigDecimal result = floatSolver.solveExpression(floatSolver.readExpression());
        Assert.assertEquals(new BigDecimal(value).setScale(floatSolver.getPrecision(),BigDecimal.ROUND_HALF_UP), result);
    }

    @Test
    public void testFunctionDefinition() throws IOException {
        testFirstValidExpression(
                "DEF sqr(a) a * a\n" +
                        "sqr(5+4)\n",
                81.0);
    }

    @Test
    public void testCompoundStatement() throws IOException {
        TokenReader reader = createReader(
                "DEF sqr(a) { b = a * a\n  b }\n" +
                        "sqr(5)\n"
        );
        FloatSolver floatSolver = new FloatSolver(reader);
        IExpression<BigDecimal> expression = floatSolver.readExpression();
        Assert.assertEquals(new BigDecimal(25), expression.solve(floatSolver));
    }

}