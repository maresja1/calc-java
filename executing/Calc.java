package executing;

import exception.BadExpressionFormatException;
import expression.IExpression;
import parsing.TokenReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;

/**
 * Created by Jan Mares on 31.03.2015
 */
public class Calc {
    public static boolean debugMode = false;

    public static void compute(InputStream in, PrintStream out) {
        TokenReader tokenReader = new TokenReader(new InputStreamReader(in));
        FloatSolver parser = new FloatSolver(tokenReader);
        for (; ; ) {
            try{
                IExpression<BigDecimal> expression = parser.readExpression();
                if(expression == null){
                    break;
                }
                printResult(out, parser.solveExpression(expression));
            } catch (BadExpressionFormatException e){
                if(debugMode){
                    throw e;
                } else {
                    out.println("ERROR");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void printResult(PrintStream out, BigDecimal expression) {
        if(expression.compareTo(BigDecimal.ZERO) == 0){
            out.println("0");
        } else {
            out.println(expression.stripTrailingZeros().toPlainString());
        }
    }
}