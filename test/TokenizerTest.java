//import org.junit.Test;
//import org.junit.Assert;
//
//import java.io.IOException;
//import java.io.StringReader;
//
///**
// * Created by jan on 12/6/14.
// */
//public class TokenizerTest {
//    private CodEx.TokenReader createReader(String string){
//        return new CodEx.TokenReader(new StringReader(string));
//    }
//
//    private void assertTokenType(CodEx.TokenReader.Token token, CodEx.TokenReader.TokenType tokenType){
//        Assert.assertEquals(tokenType, token.type);
//    }
//
//    private void assertArgsTokenType(CodEx.TokenReader.FuncCallDescription description, CodEx.TokenReader.TokenType[] tokenTypes){
//        Assert.assertEquals(description.args.size(), tokenTypes.length);
//        int i = 0;
//        for(CodEx.TokenReader.Token token : description.args){
//            assertTokenType(token,tokenTypes[i]);
//            ++i;
//        }
//    }
//
//    private void assertTokensInStream(CodEx.TokenReader tokenReader, CodEx.TokenReader.TokenType[] tokenTypes) throws IOException {
//        for(CodEx.TokenReader.TokenType type : tokenTypes){
//            assertTokenType(tokenReader.readToken(),type);
//        }
//    }
//
//    @Test
//    public void testFunc1() throws IOException {
//        CodEx.TokenReader reader = createReader("DEF func(a,0.131e9,c,26)");
//        assertTokenType(reader.readToken(), CodEx.TokenReader.TokenType.def);
//        Assert.assertFalse(reader.isEndOfLine());
//        assertTokenType(reader.readToken(), CodEx.TokenReader.TokenType.funCall);
//        Assert.assertFalse(reader.isEndOfLine());
//        Assert.assertEquals(null,reader.readToken());
//    }
//
//    @Test
//    public void testFunc2() throws IOException {
//        CodEx.TokenReader reader = createReader("DEF func(a,0.131e9,c,26)");
//        assertTokenType(reader.readToken(), CodEx.TokenReader.TokenType.def);
//        Assert.assertFalse(reader.isEndOfLine());
//        assertArgsTokenType(
//                ((CodEx.TokenReader.FuncCallDescription)reader.readToken().value),
//                new CodEx.TokenReader.TokenType[]{
//                        CodEx.TokenReader.TokenType.identifier,
//                        CodEx.TokenReader.TokenType.number,
//                        CodEx.TokenReader.TokenType.identifier,
//                        CodEx.TokenReader.TokenType.number
//                }
//        );
//        Assert.assertFalse(reader.isEndOfLine());
//        Assert.assertEquals(null,reader.readToken());
//    }
//
//    @Test
//    public void testFunc3() throws IOException {
//        CodEx.TokenReader reader = createReader("func(a,0.131e9,c,26)\nfunc(a,0.131e9,c,26)");
//        assertArgsTokenType(
//                ((CodEx.TokenReader.FuncCallDescription) reader.readToken().value),
//                new CodEx.TokenReader.TokenType[]{
//                        CodEx.TokenReader.TokenType.identifier,
//                        CodEx.TokenReader.TokenType.number,
//                        CodEx.TokenReader.TokenType.identifier,
//                        CodEx.TokenReader.TokenType.number
//                }
//        );
//        Assert.assertTrue(reader.isEndOfLine());
//        assertArgsTokenType(((CodEx.TokenReader.FuncCallDescription)reader.readToken().value), new CodEx.TokenReader.TokenType[]{CodEx.TokenReader.TokenType.identifier, CodEx.TokenReader.TokenType.number, CodEx.TokenReader.TokenType.identifier, CodEx.TokenReader.TokenType.number});
//        Assert.assertEquals(null,reader.readToken());
//    }
//
//    @Test
//    public void testFunc4() throws IOException {
//        CodEx.TokenReader reader = createReader("3 + 2 + 4");
//        assertTokensInStream(reader,new CodEx.TokenReader.TokenType[]{CodEx.TokenReader.TokenType.number, CodEx.TokenReader.TokenType.plus, CodEx.TokenReader.TokenType.number, CodEx.TokenReader.TokenType.plus, CodEx.TokenReader.TokenType.number});
//        Assert.assertEquals(null,reader.readToken());
//    }
//
//    @Test
//    public void testFunc5() throws IOException {
//        CodEx.TokenReader reader = createReader("3 + 2 + 4");
//        CodEx.FloatExpression floatExpression = new CodEx.FloatExpression(reader);
//        Assert.assertEquals(9.0, floatExpression.solveExpression(floatExpression.readExpression()));
//    }
//
//    @Test
//    public void testFunc6() throws IOException {
//        CodEx.TokenReader reader = createReader("3 - 2 + 4");
//        CodEx.FloatExpression floatExpression = new CodEx.FloatExpression(reader);
//        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
//    }
//
//    @Test
//    public void testFunc7() throws IOException {
//        CodEx.TokenReader reader = createReader("3 - 2 + 4\nlast");
//        CodEx.FloatExpression floatExpression = new CodEx.FloatExpression(reader);
//        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
//        Assert.assertEquals(5.0, floatExpression.solveExpression(floatExpression.readExpression()));
//    }
//
//    @Test
//    public void testFunc8() throws IOException {
//        CodEx.TokenReader reader = createReader("3 - 2 + 4 * 2\nlast");
//        CodEx.FloatExpression floatExpression = new CodEx.FloatExpression(reader);
//        Assert.assertEquals(9.0, floatExpression.solveExpression(floatExpression.readExpression()));
//        Assert.assertEquals(9.0, floatExpression.solveExpression(floatExpression.readExpression()));
//    }
//
//}
