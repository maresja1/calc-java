import executing.FloatSolver;
import org.junit.Assert;
import org.junit.Test;
import parsing.TokenReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Tom on 5.4.15.
 */
public class SemesterTest {
    private TokenReader createReader(String string){
        return new TokenReader(new StringReader(string));
    }

    @Test
    public void test1() throws IOException {
        TokenReader reader = createReader(
                "DEF sqr(a) a * a\n" +
                        "sqr(5+4)\n"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        Assert.assertEquals(null, floatExpression.readExpression());
    }
}