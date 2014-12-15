/* Pouzijte implicitni nepojmenovany balicek, tj. nepouzijte "package" */

import java.io.*;
import java.io.BufferedReader;
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

        private final Pattern newLinePattern;
        private HashMap<TokenType, Pattern> tokenHashMap;
        private HashMap<TokenType, Pattern> funcArgsHashMap;
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
            tokenHashMap.put(TokenType.asterisk, Pattern.compile("*",Pattern.LITERAL));
            tokenHashMap.put(TokenType.slash, Pattern.compile("/",Pattern.LITERAL));
            tokenHashMap.put(TokenType.plus, Pattern.compile("+",Pattern.LITERAL));
            tokenHashMap.put(TokenType.minus, Pattern.compile("-",Pattern.LITERAL));
            tokenHashMap.put(TokenType.equals, Pattern.compile("=",Pattern.LITERAL));
            tokenHashMap.put(TokenType.def, Pattern.compile("DEF",Pattern.LITERAL));
            tokenHashMap.put(TokenType.lBracket, Pattern.compile("(",Pattern.LITERAL));
            tokenHashMap.put(TokenType.rBracket, Pattern.compile(")",Pattern.LITERAL));
            tokenHashMap.put(TokenType.argSeparator, Pattern.compile(",",Pattern.LITERAL));
            funcArgsHashMap.put(TokenType.number, Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?"));
            funcArgsHashMap.put(TokenType.identifier, Pattern.compile("[a-zA-Z]+"));
            newLinePattern = Pattern.compile("(\\r)?\\n");
            delimiterPattern = Pattern.compile("\\s+");
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
            identifier,
            number,
            argSeparator
        }

        public static class Token {
            TokenType type;
            String value;
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

        private Matcher matchNext(Pattern tokenPattern){
            String actualWord = getActualWord();
            if(actualWord == null){
                throw new BadExpressionFormatException("Unexpected token");
            }
            Matcher matcher = tokenPattern.matcher(actualWord);
            matcher.region(posInWord,matcher.regionEnd());
            if(matcher.lookingAt()){
                return matcher;
            } else {
                throw new BadExpressionFormatException("Unexpected token");
            }
        }

        public boolean hasNewLine(){
            getActualWord();
            return posInBuffer == buffer.length;
        }

        public void skipRestOfLine() throws IOException {
            fillBuffer();
        }
        public void skipNewLine() throws IOException {
            getActualWord();
            if(posInBuffer == buffer.length){
                fillBuffer();
            } else {
                throw new BadExpressionFormatException("Expected line end");
            }
        }

        public Token matchToken(TokenType tokenType){
            Pattern tokenPattern = tokenHashMap.get(tokenType);
            Matcher matcher = matchNext(tokenPattern);
            String next = getActualWord().substring(posInWord, matcher.end());
            posInWord = matcher.end();

            Token token = new Token();
            token.type = tokenType;
            token.value = next;
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
         * @param context context of expression execution
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
        private ArrayList<TokenReader.Token> arguments;
        private IExpression<T> functionExpression;
        private String funcName;


        public FunctionDefinition(String funcName, ArrayList<TokenReader.Token> arguments, IExpression<T> functionExpression) {
            this.arguments = arguments;
            this.functionExpression = functionExpression;
            this.funcName = funcName;
        }

        public IExpression<T> getFunctionExpression() {
            return functionExpression;
        }

        public String getArgumentName(int index) {
            return arguments.get(index).value;
        }

        public int getArgumentCount() {
            return arguments.size();
        }

        public String getFuncName() {
            return funcName;
        }
    }


    private static class FunctionCallExpression<T> implements IOperandExpression<T> {
        FunctionDefinition<T> functionDefinition;
        IExpression<T>[] operands;

        public FunctionCallExpression(FunctionDefinition<T> functionDefinition) {
            this.functionDefinition = functionDefinition;
            operands = (IExpression<T>[]) new IExpression[functionDefinition.getArgumentCount()];
        }

        @Override
        public void setOperand(int index, IExpression<T> operand) {
            operands[index] = operand;
        }

        @Override
        public int getArity() {
            return functionDefinition.getArgumentCount();
        }

        @Override
        public String getName() {
            return functionDefinition.getFuncName();
        }

        @Override
        public T solve(IExpressionContext<T> context) {
            ExpressionContextWrapper<T> expressionContextWrapper = new ExpressionContextWrapper<T>(null);
            for (int i = 0; i < functionDefinition.getArgumentCount(); i++) {
                expressionContextWrapper.setVariableValue(functionDefinition.getArgumentName(i), new ConstantExpression<T>(operands[i].solve(context)));
            }
            return functionDefinition.getFunctionExpression().solve(expressionContextWrapper);
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
        private HashMap<String, FunctionDefinition<T>> functionsDefinition = new HashMap<String, FunctionDefinition<T>>();

        public ExpressionContextWrapper(IExpressionContext<T> parent) {
            this.parent = parent;
        }

        @Override
        public IExpression<T> getVariableValue(String varName) {
            if(functionsDefinition.containsKey(varName)){
                throw new BadExpressionFormatException("Function with same name already exists.");
            }

            if (variablesValues.containsKey(varName)) {
                return variablesValues.get(varName);
            } else if(parent != null) {
                return parent.getVariableValue(varName);
            }
            throw new BadExpressionFormatException("Unknown variable");
        }

        @Override
        public void setVariableValue(String varName, IExpression<T> expression) {
            if(!varName.equals("last")){
                if(functionsDefinition.containsKey(varName)){
                    throw new BadExpressionFormatException("Function with same name already exists.");
                }
                variablesValues.put(varName, expression);
            }
        }

        public boolean hasVariable(String varName){
            return variablesValues.containsKey(varName);
        }

        public void registerFunction(FunctionDefinition<T> functionDefinition){
            if(hasVariable(functionDefinition.getFuncName()) || functionDefinition.getFuncName().equals("last")){
                throw new BadExpressionFormatException("Variable with same name already exists.");
            } else{
                functionsDefinition.put(functionDefinition.getFuncName(),functionDefinition);
            }
        }

        public FunctionDefinition<T> getFunction(String value) {
            if(!functionsDefinition.containsKey(value)){
                throw new BadExpressionFormatException("Function by this name does not exist.");
            }
            return functionsDefinition.get(value);
        }
    }

    public static class FloatExpression implements IExpressionContext<Double> {
        private boolean extendedExpression = true;
        private double last = 0;
        private ExpressionContextWrapper<Double> contextWrapper;
        private TokenReader reader;
        private boolean inFuncDef = false;
        private ArrayList<String> funcArgs = new ArrayList<String>();

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

//        S -> def id(VarList) E \n E | id A | E
//        A -> = E | + HEa | - HEa | * TF | / TF
//        E -> HEa
//        Ea -> + TEa | /\
//        T -> DTa
//        Ta -> * TF | / TF | /\
//        F -> (E) | num | id | id (Arglist)
//        Arglist -> (E ArglistC)
//        ArglistC -> ,E ArglistC | /\
//        VarList -> (id VarListC)
//        VarListC -> ,id VarListC | /\


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
                last = 0;
                reader.skipRestOfLine();
                throw e;
            }
        }

        public Double solveExpression(IExpression<Double> expression) {
            last = expression.solve(contextWrapper);
            return last;
        }

        private IExpression<Double> matchS() throws IOException {
            if (hasFileEnd()) {
                return null;
            }else if(hasLineEnd()) {
                matchNewLine();
                return matchS();
            } else if (hasToken(TokenReader.TokenType.def) && extendedExpression) {
                last = 0;
                matchTerm(TokenReader.TokenType.def);
                FunctionDefinition<Double> functionDefinition = matchFN();
                contextWrapper.registerFunction(functionDefinition);
                matchNewLine();
                return matchS();
            } else if (hasToken(TokenReader.TokenType.identifier) && extendedExpression) {
                return matchA(matchTerm(TokenReader.TokenType.identifier));
            } else {
                return matchE();
            }
        }

        private IExpression<Double> matchA(TokenReader.Token identifier) {
            ATwoOperandExpression<Double> leftNew;
            if (hasLineEnd()) {
                return new VariableExpression<Double>(identifier.value);
            }
            if (hasToken(TokenReader.TokenType.equals)) {
                matchTerm(TokenReader.TokenType.equals);
                return new VariableAssignment<Double>(identifier.value, matchE());
            } else if(hasToken(TokenReader.TokenType.lBracket)) {
                return matchFuncCallRest(identifier);
            } else {
                IExpression<Double> left = new VariableExpression<Double>(identifier.value);
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
            } else if (hasToken(TokenReader.TokenType.identifier) && extendedExpression ) {
                token = matchTerm(TokenReader.TokenType.identifier);
                if(hasToken(TokenReader.TokenType.lBracket) && !inFuncDef){
                    return matchFuncCallRest(token);
                } else {
                    if(inFuncDef && !funcArgs.contains(token.value)){
                        throw new BadExpressionFormatException("Can't use variables in function.");
                    }
                    return new VariableExpression<Double>(token.value);
                }
            } else if (hasToken(TokenReader.TokenType.number)) {
                token = matchTerm(TokenReader.TokenType.number);
                return new ConstantExpression<Double>(Double.parseDouble(token.value));
            }
            throw new BadExpressionFormatException("Unexpected token type.");
        }

        private IExpression<Double> matchFuncCallRest(TokenReader.Token token) {
            matchTerm(TokenReader.TokenType.lBracket);
            ArrayList<IExpression<Double>> expressions = new ArrayList<IExpression<Double>>();
            if(!hasToken(TokenReader.TokenType.rBracket)) {
                expressions.add(matchArg());
                matchExprListC(expressions);
            }
            matchTerm(TokenReader.TokenType.rBracket);
            FunctionCallExpression<Double> functionCallExpression = new FunctionCallExpression<Double>(contextWrapper.getFunction(token.value));
            if(functionCallExpression.getArity()!=expressions.size()){
                throw new BadExpressionFormatException("Bad number of arguments.");
            }
            int i=0;
            for(IExpression<Double> expression : expressions){
                functionCallExpression.setOperand(i, expression);
                ++i;
            }
            return functionCallExpression;
        }


        private FunctionDefinition<Double> matchFN() {
            inFuncDef = true;
            try {
                TokenReader.Token tokenIdentifier = matchTerm(TokenReader.TokenType.identifier);
                matchTerm(TokenReader.TokenType.lBracket);
                ArrayList<TokenReader.Token> arguments = new ArrayList<TokenReader.Token>();
                if (!hasToken(TokenReader.TokenType.rBracket)) {
                    TokenReader.Token tokenFirstArg = matchTerm(TokenReader.TokenType.identifier);
                    checkArgName(tokenFirstArg);
                    arguments.add(tokenFirstArg);
                    matchVarListC(arguments);
                }
                for(TokenReader.Token token : arguments){
                    funcArgs.add(token.value);
                }
                matchTerm(TokenReader.TokenType.rBracket);
                return new FunctionDefinition<Double>(tokenIdentifier.value, arguments, matchE());
            } finally {
                inFuncDef = false;
                funcArgs.clear();
            }
        }

        private void checkArgName(TokenReader.Token tokenFirstArg) {
            if(tokenFirstArg.value.length()!=1){
                throw new BadExpressionFormatException("Name of argument can be just one character.");
            }
        }

        private void matchExprListC(ArrayList<IExpression<Double>> arguments){
            if(hasToken(TokenReader.TokenType.argSeparator)){
                matchTerm(TokenReader.TokenType.argSeparator);
                // arguments.add(matchE());
                arguments.add(matchArg());
                matchExprListC(arguments);
            }
        }

        private void matchVarListC(ArrayList<TokenReader.Token> arguments){
            if(hasToken(TokenReader.TokenType.argSeparator)){
                matchTerm(TokenReader.TokenType.argSeparator);
                TokenReader.Token arg = matchTerm(TokenReader.TokenType.identifier);
                checkArgName(arg);
                arguments.add(arg);
                matchVarListC(arguments);
            }
        }

        private IExpression<Double> matchArg(){
            if (hasToken(TokenReader.TokenType.identifier) && extendedExpression ) {
                TokenReader.Token token = matchTerm(TokenReader.TokenType.identifier);
                return new VariableExpression<Double>(token.value);
            } else if (hasToken(TokenReader.TokenType.number)) {
                TokenReader.Token token = matchTerm(TokenReader.TokenType.number);
                return new ConstantExpression<Double>(Double.parseDouble(token.value));
            }
            throw new BadExpressionFormatException("Expected valid argument expression.");
        }


        private TokenReader.Token matchTerm(TokenReader.TokenType type) {
            return reader.matchToken(type);
        }

        private void matchNewLine() throws IOException {
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

