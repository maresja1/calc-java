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
    private TokenReader createReader(String string){
        return new TokenReader(new StringReader(string));
    }

    @Test
    public void testFunctionDefinition() throws IOException {
        TokenReader reader = createReader(
                "DEF sqr(a) a * a\n" +
                        "sqr(5+4)\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(null, floatExpression.readExpression());
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