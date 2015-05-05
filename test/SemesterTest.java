import exception.BadExpressionFormatException;
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

    private static void testValidExpressions(String expressionString, Double... values) throws IOException {
        TokenReader reader = createReader(expressionString);
        FloatSolver floatSolver = new FloatSolver(reader);
        for (int i = 0; i < values.length; ++i)
        {
            IExpression<BigDecimal> expression = floatSolver.readExpression();
            Assert.assertNotNull(expression);
            assertResult(values[i],floatSolver.solveExpression(expression),floatSolver.getPrecision());
        }
        Assert.assertEquals(null, floatSolver.readExpression());
    }

    private static void assertResult(Double expected, BigDecimal exprResult, int precision){
        Assert.assertEquals(new BigDecimal(expected).setScale(precision, BigDecimal.ROUND_HALF_UP), exprResult);
    }

    @Test
    public void test1() throws IOException {
        testValidExpressions("3+2;", 5.0);
    }

    @Test
    public void test2() throws IOException {
        testValidExpressions("1e2;", 100.0);
    }

    @Test
    public void test3() throws IOException {
        testValidExpressions("2 * ( 3 + 1 );",8.0);
    }

    @Test
    public void test4() throws IOException {
        testValidExpressions("1.2 + 4.3;", 5.5);
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test5() throws IOException {
        testValidExpressions(";3 ** 5;", 0.0);
    }

    @Test
    public void test6() throws IOException {
        testValidExpressions("2*(3+1);",8.0);
    }

    @Test
    public void test7() throws IOException {
        testValidExpressions("22 * ( 3 + 1 );1e2;", 88.0, 100.0);
    }

    @Test
    public void test8() throws IOException {
        testValidExpressions("2+2*3;", 8.0);
    }

    @Test
    public void test9() throws IOException {
        testValidExpressions("\n\n\n(7)\n\n;;;\n\n(8);", 7.0, 8.0);
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test10() throws IOException {
        testValidExpressions("\n;\n;\n;\n;()",0.0);
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test11() throws IOException {
        testValidExpressions("\n\n\n\n2 3",2.0,3.0);
    }

    @Test
    public void test12() throws IOException {
        testValidExpressions("\n\n\n\n1++4;",5.0);
    }
    @Test(expected = BadExpressionFormatException.class)
    public void test13() throws IOException {
        testValidExpressions("\n\n\n\n1++;",0.0);
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test14() throws IOException {
        testValidExpressions("\n\n\n\n+;",0.0);
    }

    @Test
    public void test15() throws IOException {
        testValidExpressions("\n\n\n\n2*+3;",6.0);
    }


    @Test(expected = BadExpressionFormatException.class)
    public void test16() throws IOException {
        testValidExpressions("\n\n\n\n(2;",0.0);
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test17() throws IOException {
        testValidExpressions("\n\n\n\n(2 2 2;",0.0);
    }

    @Test
    public void test18() throws IOException {
        testValidExpressions("2-2*3;",-4.0);
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test19() throws IOException {
        testValidExpressions("2/0;",0.0);
    }


    @Test
    public void test20() throws IOException {
        testValidExpressions("");
    }

    @Test
    public void test21() throws IOException {
        testValidExpressions(";\n;\n;\n;\n");
    }

    @Test
    public void test22() throws IOException {
        testValidExpressions("DEF sqr(a) a * a;\nsqr(5);", 25.0);
    }

    @Test
    public void test23() throws IOException {
        testValidExpressions(
                "DEF sqr(a) a * a;\n" +
                        "sqr(5);\n" +
                        "DEF\tfoo(a,b)\t 2*a\t\t + 2*b;\n" +
                        "last;\n" +
                        "foo(1,1);",
                25.0, 0.0, 4.0
        );
    }

    @Test
    public void test24() throws IOException {
        testValidExpressions(
                "y = 5;y;",
                5.0, 5.0
        );
    }

    @Test
    public void test25() throws IOException {
        testValidExpressions(
                "DEF sqr(a) a * a;\n" +
                        "sqr(5+4);\n",
                81.0
        );
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test26() throws IOException {
        testValidExpressions(
                "b = a = 2;\n" +
                        "a;\n" +
                        "b;\n",
                2.0, 2.0, 2.0
        );
    }

    //Pri pokusu zavoalt neexistujici funkci se vypise error
    @Test(expected = BadExpressionFormatException.class)
    public void test27() throws IOException {
        testValidExpressions(
                "error();",
                0.0
        );
    }

    @Test
    public void test28() throws IOException {
        testValidExpressions(
                "DEF sqr() 1;" +
                        "sqr();",
                1.0
        );
    }

    //Last obsahuje hodnotu posledniho vyrazu
    @Test
    public void test29() throws IOException {
        testValidExpressions(
                "5;last=2;last;last;",
                5.0, 2.0, 2.0, 2.0
        );
    }

    @Test
    public void test30() throws IOException {
        testValidExpressions(
                "a=5;DEF f() a;f();",
                5.0, 5.0
        );
    }

    @Test
    public void test30_b() throws IOException {
        testValidExpressions(
                "a=5;DEF f() a;5;",
                5.0, 5.0
        );
    }

    //Prirazeni neni vyraz - nemuze figurovat v jinych vyrazech
    @Test(expected = BadExpressionFormatException.class)
    public void test31() throws IOException {
        testValidExpressions(
                "b + a = 2;",
                0.0
        );
    }

    //Po prirazenı do promenne je v last ulozena hodnota teto promenne
    @Test
    public void test32() throws IOException {
        testValidExpressions(
                "b = 2;last;",
                2.0, 2.0
        );
    }

    //Pri definici funkce nesmi existovat jiz takova promenna
    @Test(expected = BadExpressionFormatException.class)
    public void test33() throws IOException {
        testValidExpressions(
                "b = 2;DEF b() 1;b();",
                2.0,2.0
        );
    }

    //Pri definici promenne nesmi existovat jiz takova funkce
    @Test(expected = BadExpressionFormatException.class)
    public void test33_b() throws IOException {
        testValidExpressions(
                "DEF b() 1;b = 2;b();",
                2.0,2.0
        );
    }

    //Pri definici promenne nesmi existovat jiz takova funkce
    @Test(expected = BadExpressionFormatException.class)
    public void test33_c() throws IOException {
        testValidExpressions(
                "DEF b() 1;na +b;",
                2.0,2.0
        );
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test34() throws IOException {
        testValidExpressions(
                "DEF b() 1 1;b();",
                2.0,2.0
        );
    }

    @Test
    public void test35() throws IOException {
        TokenReader reader = createReader(
                "1+1;\n1 1;\nlast;"
        );
        FloatSolver floatExpression = new FloatSolver(reader);
        assertResult(2.0, floatExpression.solveExpression(floatExpression.readExpression()),floatExpression.getPrecision());
        try{
            floatExpression.readExpression();
            Assert.fail();
        } catch (BadExpressionFormatException e){

        }
        assertResult(0.0, floatExpression.solveExpression(floatExpression.readExpression()), floatExpression.getPrecision());
        Assert.assertEquals(null, floatExpression.readExpression());
    }

    @Test
    public void test36() throws IOException {
        testValidExpressions(
                "Ahoj=1;ahoj=2;ahoJ=3;Ahoj;ahoj;ahoJ;",
                1.0, 2.0, 3.0, 1.0, 2.0, 3.0
        );
    }

    @Test
    public void test37() throws IOException {
        testValidExpressions(
                "DEFsqr(a)a*a;\n" +
                        "sqr(5);\n" +
                        "DEFfoo(a,b)2*a+2*b;\n" +
                        "last;\n" +
                        "foo(1,1);",
                25.0,
                0.0,
                4.0
        );
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test38() throws IOException {
        testValidExpressions(
                "DEF sqr();\n 1;\n" +
                        "sqr();\n",
                1.0
        );
    }

    @Test
    public void test38_b() throws IOException {
        testValidExpressions(
                "DEF sqr()\n 1;\n" +
                        "sqr();\n",
                1.0
        );
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test39() throws IOException {
        testValidExpressions(
                "DEF;\n sqr() 1;\n" +
                        "sqr();\n",
                1.0
        );
    }

    @Test
    public void test39_b() throws IOException {
        testValidExpressions(
                "DEF\n sqr() 1;\n" +
                        "sqr();\n",
                1.0
        );
    }

    @Test
    public void test40() throws IOException {
        testValidExpressions(
                "DEF abc() 2;\n" +
                        "abc();\n" +
                        "DEF sqr() abc();\n" +
                        "sqr();\n",
                2.0, 2.0
        );
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test41() throws IOException {
        testValidExpressions(
                "DEF sqr() 1;\n" +
                        "sqr(1);\n",
                1.0
        );
    }

    //Po startu je v last 0
    @Test
    public void test42() throws IOException {
        testValidExpressions(
                "last;\nlast;\nlast;\n",
                0.0, 0.0, 0.0
        );
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test43() throws IOException {
        testValidExpressions(
                "sqr();\n" +
                        "DEF sqr() 1;\n" +
                        "sqr();\n",
                1.0
        );
    }

    @Test
    public void test44() throws IOException {
        testValidExpressions(
                "DEF a() 1;" +
                        "DEF b() 2;" +
                        "a();" +
                        "b();",
                1.0, 2.0
        );
    }

    @Test(expected = BadExpressionFormatException.class)
    public void test45() throws IOException {
        testValidExpressions(
                "DEF last() 1;\n" +
                        "last();\n",
                1.0
        );
    }
    @Test
    public void test46() throws IOException {
        testValidExpressions(
                "DEF foo() 1;\n" +
                        "foo() + 1;\n",
                2.0
        );
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
    public void testSemiColon3() throws IOException {
        testValidExpressions("{7;8}", 8.0);
    }

    @Test
    public void testSemiColon4() throws IOException {
        testValidExpressions("{7;8;}", 8.0);
    }

    @Test
    public void testSemiColon5() throws IOException {
        testValidExpressions("{7;8}{9;10;20;}", 8.0,20.0);
    }


    @Test
    public void testSemiColon6() throws IOException {
        testValidExpressions("{7;{9;10;20}}", 20.0);
    }

    @Test
    public void testSemiColon7() throws IOException {
        testValidExpressions("{7;{9;10}20}", 20.0);
    }


    @Test
    public void testCompoundStatement () throws IOException {
        testValidExpressions("DEF sqr(a) { b = a * a;  b; }\n" +
                             "sqr(5);\n" +
                             "DEF func(a, b, c) {a + b*c}\n" +
                             "DEF funcb(a, b) {a/b}\n" +
                             "funcb(1, func(2, 2, funcb(8, 2)))*10;\n" +
                             "funcb(sqr(last), last);", 25.0, 1.0, 1.0);
    }

    @Test
    public void testGlobalVariable () throws IOException {
        testValidExpressions("a = 10;" +
                             "DEF addToA(b) a + b;\n" +
                             "addToA(0);" +
                             "a = addToA(last); addToA(1);", 10.0, 10.0, 20.0, 21.0);
    }

    @Test
    public void testLocalVariable () throws IOException {
        testValidExpressions("DEF sqr(a) { b = a * a;  b; }\n" +
                "sqr(5);b;\n", 25.0, 0.0);
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
        testValidExpressions("DEF factorial(n) {f = 1; for(i, 1, n ){f = f*(i+1)} f; }" +
                             "factorial(0); factorial(1); factorial(2); factorial(3); factorial(10);",
                             factorial(0), factorial(1), 2.0, 6.0, factorial(10));
    }

    @Test
    public void nestedFunctionCalls () throws IOException {
        testValidExpressions("DEF times(x, y) {x*y}" +
                             "DEF pow(x, n) {p = 1; for(i,0,n){p = x*p}; p;};" +
                             "DEF sqr(a) pow(a, 2);" +
                             "times(2, 5); pow(1, 0); sqr(3); pow(2,10);",
                              10.0, 1.0, 9.0, 1024.0);
    }

    @Test
    public void ifStatementTest() throws IOException {
        testValidExpressions("DEF max(a, b){if (a > b){a;}else{b;};};max(3, 2);" +
                             "DEF sign(a){if(a < 0){-1;}else{if(a == 0){0;}else{1;};};};sign(-10);sign(0);sign(1/999);",
                             3.0, -1.0, 0.0, 1.0);
    }

    @Test
    public void recursionTest () throws IOException {
        testValidExpressions("DEF ceilRec(a, n){if (n > a){n;}else{ceilRec(a, n+1);};}; DEF ceil(a){ceilRec(a, 0);}; DEF floor(a){ceil(a) - 1;};" +
                             "ceil(1/3); floor(20/9);" +
                             "DEF sqrtRec(a, b){c = a/b; d = (b-c)*(b-c); if(d < 1/10000){c;}else{sqrtRec(a, (c+b)/2);};};" +
                             "DEF sqrt(a){sqrtRec(a, a/2);};sqrt(3);floor(last);ceil(last);", 1.0, 2.0, 1.0, 2.0);
    }
}