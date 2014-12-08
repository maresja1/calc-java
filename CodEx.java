/* Pouzijte implicitni nepojmenovany balicek, tj. nepouzijte "package" */

import java.io.*;
import java.io.BufferedReader;
import java.lang.Character;
import java.lang.StringBuilder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DU 2. !!! Nemente jmeno tridy !!!
 *
 * @author Jan Mares
 */
public class CodEx {
    public static class TokenReader{

        private HashMap<TokenType, Pattern> tokenHashMap;
        private HashMap<TokenType, Pattern> funcArgsHashMap;
        private Pattern newLinePattern;
        private Pattern delimiterPattern;
        private BufferedReader in;
        private String[] buffer;
        private int posInBuffer = 0;
        private int posInWord = 0;
        private boolean EOF = false;

        /**
         * Creates a buffering character-input stream that uses a default-sized
         * input buffer.
         *
         * @param in A Reader
         */
        public TokenReader(Reader in) {
            this.in = new BufferedReader(in);
            tokenHashMap = new HashMap<TokenType, Pattern>();
            funcArgsHashMap = new HashMap<TokenType, Pattern>();
            tokenHashMap.put(TokenType.asterisk, Pattern.compile("\\*"));
            tokenHashMap.put(TokenType.slash, Pattern.compile("/"));
            tokenHashMap.put(TokenType.plus, Pattern.compile("\\+"));
            tokenHashMap.put(TokenType.minus, Pattern.compile("-"));
            tokenHashMap.put(TokenType.equals, Pattern.compile("="));
            tokenHashMap.put(TokenType.def, Pattern.compile("DEF"));
            tokenHashMap.put(TokenType.lBracket, Pattern.compile("\\("));
            tokenHashMap.put(TokenType.rBracket, Pattern.compile("\\)"));
            tokenHashMap.put(TokenType.funCall, Pattern.compile("([a-z]+)\\(([^\\)]*)\\)"));
            funcArgsHashMap.put(TokenType.number, Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?"));
            funcArgsHashMap.put(TokenType.varIdentifier, Pattern.compile("[a-z]+"));
            newLinePattern = Pattern.compile("(\\r)?\\n");
            delimiterPattern = Pattern.compile(" +");
            tokenHashMap.putAll(funcArgsHashMap);
            try {
                fillBuffer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * TOKENS:
         */
        enum TokenType {
            lBracket,
            rBracket,
            equals,
            plus,
            minus,
            asterisk,
            slash,
            def,
            funCall,
            varIdentifier,
            number
        }

        public static class Token {
            TokenType type;
            Object value;
        }

        public static class FuncCallDescription {
            String funcName;
            List<Token> args;
        }

        private void fillBuffer() throws IOException {
            String line = in.readLine();
            if(line == null){
                EOF = true;
            } else {
                buffer = delimiterPattern.split(line);
                posInBuffer = 0;
                posInWord = 0;
            }
        }

        private String getActualWord(){
            if(posInBuffer < buffer.length && posInWord == buffer[posInBuffer].length()){
                posInWord = 0;
                ++posInBuffer;
            }
            if(posInBuffer >= buffer.length){
                return null;
            }
            return buffer[posInBuffer];
        }

        public boolean hasToken(TokenType tokenType){
            Pattern tokenPattern = tokenHashMap.get(tokenType);
            return hasNext(tokenPattern);
        }

        private boolean hasNext(Pattern tokenPattern) {
            String actualWord = getActualWord();
            if(actualWord == null){
                return false;
            }
            Matcher matcher = tokenPattern.matcher(actualWord);
            matcher.region(posInWord,matcher.regionEnd());
            return matcher.lookingAt();
        }

        private String matchNext(Pattern tokenPattern){
            String actualWord = getActualWord();
            if(actualWord == null){
                throw new BadExpressionFormatException("Unexpected token");
            }
            Matcher matcher = tokenPattern.matcher(actualWord);
            matcher.region(posInWord,matcher.regionEnd());
            if(matcher.lookingAt()){
                String result = actualWord.substring(posInWord,matcher.end());
                posInWord = matcher.end();
                return result;
            } else {
                throw new BadExpressionFormatException("Unexpected token");
            }
        }

        public boolean hasNewLine(){
            getActualWord();
            return posInBuffer == buffer.length;
        }

        public void skipRestOfLine(){
            try {
                fillBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void skipNewLine(){
            getActualWord();
            if(posInBuffer == buffer.length){
                try {
                    fillBuffer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new BadExpressionFormatException("Expected line end");
            }
        }

        public Token matchToken(TokenType tokenType){
            Pattern tokenPattern = tokenHashMap.get(tokenType);
            String next = matchNext(tokenPattern);
            Token token = new Token();
            if(tokenType == TokenType.funCall){
                FuncCallDescription funcCallDescription = new FuncCallDescription();
                //TODO: this
//                funcCallDescription.funcName = matcher.group(1);
//                String[] splitString = matcher.group(2).split(",");
//                funcCallDescription.args = new ArrayList<Token>();
//                for (String str : splitString) {
//                    funcCallDescription.args.add(parseToken(str, funcArgsHashMap));
//                }
                token.value = funcCallDescription;
            } else {
                token.type = tokenType;
                token.value = next;
            }
            return token;
        }

        public boolean hasFileEnd(){
            return EOF;
        }
    }

    /**
     * Represents general expression of generic type.
     *
     * @param <T> type of expression
     */
    public interface IExpression<T> {
        /**
         * Returns the value of the expression
         *
         * @param context
         * @return value of the expression
         */
        public T solve(IExpressionContext<T> context);
    }

    /**
     * Represents constant expression of generic type.
     *
     * @param <T> type of expression
     */
    private static class ConstantExpression<T> implements IExpression<T> {
        private T constant;

        private ConstantExpression(T constant) {
            this.constant = constant;
        }

        @Override
        public T solve(IExpressionContext<T> context) {
            return constant;
        }
    }

    private static class VariableExpression<T> implements IExpression<T> {
        private String name;

        public VariableExpression(String name) {
            this.name = name;
        }

        @Override
        public T solve(IExpressionContext<T> context) {
            return context.getVariableValue(name).solve(context);
        }
    }

    private interface IOperandExpression<T> extends IExpression<T> {
        int getArity();

        void setOperand(int index, IExpression<T> operand);

        String getName();
    }

    public static class BadExpressionFormatException extends RuntimeException {
        /**
         * Constructs a new runtime exception with {@code null} as its
         * detail message.  The cause is not initialized, and may subsequently be
         * initialized by a call to {@link #initCause}.
         */
        public BadExpressionFormatException() {
            super();
        }

        /**
         * Constructs a new runtime exception with the specified detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public BadExpressionFormatException(String message) {
            super(message);
        }

        /**
         * Constructs a new runtime exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this runtime exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A <tt>null</tt> value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public BadExpressionFormatException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs a new runtime exception with the specified cause and a
         * detail message of <tt>(cause==null ? null : cause.toString())</tt>
         * (which typically contains the class and detail message of
         * <tt>cause</tt>).  This constructor is useful for runtime exceptions
         * that are little more than wrappers for other throwables.
         *
         * @param cause the cause (which is saved for later retrieval by the
         *              {@link #getCause()} method).  (A <tt>null</tt> value is
         *              permitted, and indicates that the cause is nonexistent or
         *              unknown.)
         * @since 1.4
         */
        public BadExpressionFormatException(Throwable cause) {
            super(cause);
        }

        /**
         * Constructs a new runtime exception with the specified detail
         * message, cause, suppression enabled or disabled, and writable
         * stack trace enabled or disabled.
         *
         * @param message            the detail message.
         * @param cause              the cause.  (A {@code null} value is permitted,
         *                           and indicates that the cause is nonexistent or unknown.)
         * @param enableSuppression  whether or not suppression is enabled
         *                           or disabled
         * @param writableStackTrace whether or not the stack trace should
         *                           be writable
         * @since 1.7
         */
        protected BadExpressionFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    /**
     * Expression of generic type with two operands.
     *
     * @param <T> type of expression
     */
    private static abstract class ATwoOperandExpression<T> implements IOperandExpression<T> {
        protected IExpression<T>[] operands;

        protected ATwoOperandExpression() {
            operands = (IExpression<T>[]) new IExpression[2];
        }

        @Override
        public T solve(IExpressionContext<T> context) {

            return doOperation(operands[0].solve(context), operands[1].solve(context));
        }

        @Override
        public void setOperand(int index, IExpression<T> operand) {
            assert index < 2;
            operands[index] = operand;
        }

        @Override
        public int getArity() {
            return 2;
        }

        protected abstract T doOperation(T operand1, T operand2);


    }

    private static class FunctionDefinition<T> {
        private String[] arguments;
        private IExpression<T> functionExpression;
        private String funcName;

        public FunctionDefinition(String funcName, String[] arguments, IExpression<T> functionExpression) {
            this.arguments = arguments;
            this.functionExpression = functionExpression;
            this.funcName = funcName;
        }

        public IExpression<T> getFunctionExpression() {
            return functionExpression;
        }

        public String getArgumentName(int index) {
            return arguments[index];
        }

        public int getArgumentCount() {
            return arguments.length;
        }

        public int getArgsIndex(String argName) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i].equals(argName)) {
                    return i;
                }
            }
            return -1;
        }

        public String getFuncName() {
            return funcName;
        }
    }


    private static class FunctionCallExpression<T> implements IOperandExpression<T> {
        FunctionDefinition<T> functionDefinition;
        IExpression<T>[] operands;
        ExpressionContextWrapper<T> expressionContextWrapper;

        public FunctionCallExpression(FunctionDefinition<T> functionDefinition) {
            this.functionDefinition = functionDefinition;
            operands = (IExpression<T>[]) new IExpression[functionDefinition.getArgumentCount()];
        }

        @Override
        public int getArity() {
            return functionDefinition.getArgumentCount();
        }

        @Override
        public void setOperand(int index, IExpression<T> operand) {
            operands[index] = operand;
        }

        @Override
        public String getName() {
            return functionDefinition.getFuncName();
        }

        @Override
        public T solve(IExpressionContext<T> context) {
            ExpressionContextWrapper<T> expressionContextWrapper = new ExpressionContextWrapper<T>(context);
            for (int i = 0; i < functionDefinition.getArgumentCount(); i++) {
                expressionContextWrapper.setVariableValue(functionDefinition.getArgumentName(i), operands[i]);
            }
            return functionDefinition.functionExpression.solve(expressionContextWrapper);
        }
    }

    private static class VariableAssignment<T> implements IExpression<T> {
        private IExpression<T> assignmentExpression;
        private String name;

        public VariableAssignment(String name, IExpression<T> assignmentExpression) {
            this.assignmentExpression = assignmentExpression;
            this.name = name;
        }

        @Override
        public T solve(IExpressionContext<T> context) {
            T value = assignmentExpression.solve(context);
            context.setVariableValue(name, new ConstantExpression<T>(value));
            return value;
        }
    }

    public interface IExpressionContext<T> {
        public IExpression<T> getVariableValue(String varName);

        public void setVariableValue(String varName, IExpression<T> expression);
    }

    public static class ExpressionContextWrapper<T> implements IExpressionContext<T> {
        private IExpressionContext<T> parent;
        private HashMap<String, IExpression<T>> variablesValues = new HashMap<String, IExpression<T>>();

        public ExpressionContextWrapper(IExpressionContext<T> parent) {
            assert parent != null;
            this.parent = parent;
        }

        @Override
        public IExpression<T> getVariableValue(String varName) {
            if (variablesValues.containsKey(varName)) {
                return variablesValues.get(varName);
            }
            return parent.getVariableValue(varName);
        }

        @Override
        public void setVariableValue(String varName, IExpression<T> expression) {
            variablesValues.put(varName, expression);
        }
    }

    public static class FloatExpression implements IExpressionContext<Double> {
        private boolean extendedExpression = false;
        private double last = 0;
        private HashMap<String, FunctionDefinition<Double>> functionsDefinition = new HashMap<String, FunctionDefinition<Double>>();
        private ExpressionContextWrapper<Double> contextWrapper;

        TokenReader reader;

        @Override
        public IExpression<Double> getVariableValue(String varName) {
            if (varName.equals("last")) {
                return new ConstantExpression<Double>(last);
            } else {
                return new ConstantExpression<Double>(0.0);
            }
        }

        @Override
        public void setVariableValue(String varName, IExpression<Double> expression) {
            throw new BadExpressionFormatException();
        }


        private static class AddExpressionFloat extends ATwoOperandExpression<Double> {
            @Override
            protected Double doOperation(Double operand1, Double operand2) {
                return operand1 + operand2;
            }

            @Override
            public String getName() {
                return "+";
            }
        }

        private static class SubExpressionFloat extends ATwoOperandExpression<Double> {
            @Override
            protected Double doOperation(Double operand1, Double operand2) {
                return operand1 - operand2;
            }

            @Override
            public String getName() {
                return "-";
            }
        }

        private static class MulExpressionFloat extends ATwoOperandExpression<Double> {
            @Override
            protected Double doOperation(Double operand1, Double operand2) {
                return operand1 * operand2;
            }

            @Override
            public String getName() {
                return "*";
            }
        }

        private static class DivExpressionFloat extends ATwoOperandExpression<Double> {
            @Override
            protected Double doOperation(Double operand1, Double operand2) {
                if(operand2 == 0){
                    throw new BadExpressionFormatException("Division by zero");
                }
                return operand1 / operand2;
            }

            @Override
            public String getName() {
                return "/";
            }
        }

//        S -> def FN \n E | id A | E
//        A -> = E | + HEa | - HEa | * TF | / TF
//        FN -> funcCall E
//        E -> HEa
//        Ea -> + TEa | /\
//        T -> DTa
//        Ta -> * TF | / TF | /\
//        F -> (E) | num | id | funcCall


        public FloatExpression(TokenReader reader) {
            this.reader = reader;
            contextWrapper = new ExpressionContextWrapper<Double>(FloatExpression.this);
        }

        public IExpression<Double> readExpression() throws IOException {
            try{
                IExpression<Double> expression = matchS();
                if(!hasFileEnd()){
                    matchNewLine();
                }
                return expression;
            } catch (BadExpressionFormatException e){
                reader.skipRestOfLine();
                throw e;
            }
        }

        public Double solveExpression(IExpression<Double> expression) {
            last = expression.solve(contextWrapper);
            return last;
        }

        private IExpression<Double> matchS() {
            if (hasFileEnd()) {
                return null;
            }else if(hasLineEnd()) {
                matchNewLine();
                return matchS();
            } else if (hasToken(TokenReader.TokenType.def) && extendedExpression) {
                matchTerm(TokenReader.TokenType.def);
                FunctionDefinition<Double> functionDefinition = matchFN();
                functionsDefinition.put(functionDefinition.getFuncName(), functionDefinition);
                matchNewLine();
                return matchE();
            } else if (hasToken(TokenReader.TokenType.varIdentifier) && extendedExpression) {
                return matchA(matchTerm(TokenReader.TokenType.varIdentifier));
            } else {
                return matchE();
            }
        }

        private IExpression<Double> matchA(TokenReader.Token identifier) {
            ATwoOperandExpression<Double> leftNew;
            if (hasLineEnd()) {
                return new VariableExpression<Double>((String) identifier.value);
            }
            if (hasToken(TokenReader.TokenType.equals)) {
                matchTerm(TokenReader.TokenType.equals);
                return new VariableAssignment<Double>((String) identifier.value, matchE());
            } else {
                IExpression<Double> left = new VariableExpression<Double>((String) identifier.value);
                if (hasToken(TokenReader.TokenType.plus)) {
                    matchTerm(TokenReader.TokenType.plus);
                    leftNew = new AddExpressionFloat();
                    leftNew.setOperand(0, left);
                    leftNew.setOperand(1, matchT());
                    return matchEa(leftNew);

                } else if (hasToken(TokenReader.TokenType.minus)) {
                    matchTerm(TokenReader.TokenType.minus);
                    leftNew = new SubExpressionFloat();
                    leftNew.setOperand(0, left);
                    leftNew.setOperand(1, matchT());
                    return matchEa(leftNew);

                } else if (hasToken(TokenReader.TokenType.asterisk)) {
                    matchTerm(TokenReader.TokenType.asterisk);
                    leftNew = new MulExpressionFloat();
                    leftNew.setOperand(0, left);
                    leftNew.setOperand(1, matchF());
                    return matchTa(leftNew);

                } else if (hasToken(TokenReader.TokenType.slash)) {
                    matchTerm(TokenReader.TokenType.slash);
                    leftNew = new DivExpressionFloat();
                    leftNew.setOperand(0, left);
                    leftNew.setOperand(1, matchF());
                    return matchTa(leftNew);

                }
            }
            throw new BadExpressionFormatException();
        }

        private IExpression<Double> matchE() {
            IExpression<Double> left = matchT();
            return matchEa(left);
        }

        private IExpression<Double> matchEa(IExpression<Double> left) {
            ATwoOperandExpression<Double> leftNew;
            if (hasToken(TokenReader.TokenType.plus)) {
                matchTerm(TokenReader.TokenType.plus);
                leftNew = new AddExpressionFloat();
                leftNew.setOperand(0, left);
                leftNew.setOperand(1, matchT());
                return matchEa(leftNew);
            } else if (hasToken(TokenReader.TokenType.minus)) {
                matchTerm(TokenReader.TokenType.minus);
                leftNew = new SubExpressionFloat();
                leftNew.setOperand(0, left);
                leftNew.setOperand(1, matchT());
                return matchEa(leftNew);
            }
            return left;
        }

        private IExpression<Double> matchT() {
            IExpression<Double> left = matchF();
            return matchTa(left);
        }

        private IExpression<Double> matchTa(IExpression<Double> left) {
            ATwoOperandExpression<Double> leftNew;
            if (hasToken(TokenReader.TokenType.asterisk)) {
                matchTerm(TokenReader.TokenType.asterisk);
                leftNew = new MulExpressionFloat();
                leftNew.setOperand(0, left);
                leftNew.setOperand(1, matchF());
                return matchTa(leftNew);
            } else if (hasToken(TokenReader.TokenType.slash)) {
                matchTerm(TokenReader.TokenType.slash);
                leftNew = new DivExpressionFloat();
                leftNew.setOperand(0, left);
                leftNew.setOperand(1, matchF());
                return matchTa(leftNew);
            }
            return left;
        }


        private IExpression<Double> matchF() {
            IExpression<Double> res;
            TokenReader.Token token;
            if (hasToken(TokenReader.TokenType.lBracket)) {
                matchTerm(TokenReader.TokenType.lBracket);
                res = matchE();
                matchTerm(TokenReader.TokenType.rBracket);
                return res;
            } else if (hasToken(TokenReader.TokenType.varIdentifier) && extendedExpression) {
                token = matchTerm(TokenReader.TokenType.varIdentifier);
                return new VariableExpression<Double>((String) token.value);
            } else if (hasToken(TokenReader.TokenType.number)) {
                token = matchTerm(TokenReader.TokenType.number);
                return new ConstantExpression<Double>(Double.parseDouble((String) token.value));
            } else if (hasToken(TokenReader.TokenType.funCall) && extendedExpression) {
                token = matchTerm(TokenReader.TokenType.funCall);
                TokenReader.FuncCallDescription funcCallDescription = (TokenReader.FuncCallDescription) token.value;
                if (!functionsDefinition.containsKey(funcCallDescription.funcName)) {
                    throw new BadExpressionFormatException("Unknown function:" + funcCallDescription.funcName);
                }
                return new FunctionCallExpression<Double>(functionsDefinition.get(funcCallDescription.funcName));
            }
            throw new BadExpressionFormatException("Unexpected token type.");
        }


        private FunctionDefinition<Double> matchFN() {
            TokenReader.Token token = matchTerm(TokenReader.TokenType.funCall);
            TokenReader.FuncCallDescription callDescription = (TokenReader.FuncCallDescription) token.value;
            ArrayList<String> arguments = new ArrayList<String>(callDescription.args.size());
            for (TokenReader.Token argToken : callDescription.args) {
                if (argToken.type != TokenReader.TokenType.varIdentifier) {
                    throw new BadExpressionFormatException("Expected parameter name.");
                }
                arguments.add((String) argToken.value);
            }
            return new FunctionDefinition<Double>(callDescription.funcName, (String[]) arguments.toArray(), matchE());
        }


        private TokenReader.Token matchTerm(TokenReader.TokenType type) {
            return reader.matchToken(type);
        }

        private void matchNewLine() {
            reader.skipNewLine();
        }

        private boolean hasLineEnd() {
            return reader.hasNewLine();
        }

        private boolean hasToken(TokenReader.TokenType tokenType) {
            return reader.hasToken(tokenType);
        }

        private boolean hasFileEnd(){
            return reader.hasFileEnd();
        }
    }

    public static class Calc {
        public static boolean debugMode = false;

        public static void compute(InputStream in, PrintStream out) {
            TokenReader tokenReader = new TokenReader(new InputStreamReader(in));
            FloatExpression parser = new FloatExpression(tokenReader);
            for (; ; ) {
                try{
                    IExpression<Double> expression = parser.readExpression();
                    if(expression == null){
                        break;
                    }
                    out.printf("%.5f\n", parser.solveExpression(expression));
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
    }

    public static void main(String[] argv) {
        Calc.compute(System.in, System.out);
    }
}

