package executing;

import exception.BadExpressionFormatException;
import expression.*;
import metadata.FunctionDefinition;
import parsing.TokenReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Jan Mares on 31.03.2015
 */

public class FloatSolver implements IExpressionContext<BigDecimal> {
    private boolean extendedExpression = true;
    private BigDecimal last = BigDecimal.ZERO;
    private int precision = 20;
    private ExpressionContextWrapper<BigDecimal> contextWrapper;
    private TokenReader reader;
    private boolean inFuncDef = false;
    private ArrayList<String> funcArgs = new ArrayList<String>();
    private boolean exprEndMatched = false;

    @Override
    public boolean hasVariable(String varName) {
        return false;
    }

    @Override
    public IExpression<BigDecimal> getVariableValue(String varName) {
        if (varName.equals("last")) {
            return new ConstantExpression<BigDecimal>(last);
        } else {
            return new ConstantExpression<BigDecimal>(BigDecimal.ZERO);
        }
    }

    @Override
    public void setVariableValue(String varName, IExpression<BigDecimal> expression) {
        throw new BadExpressionFormatException();
    }

    @Override
    public BigDecimal postProcess(BigDecimal result) {
        return result.setScale(precision,BigDecimal.ROUND_HALF_UP);
    }


    private static class AddExpressionFloat extends ATwoOperandExpression<BigDecimal> {
        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            return operand1.add(operand2);
        }

        @Override
        public String getName() {
            return "+";
        }
    }

    private static class SubExpressionFloat extends ATwoOperandExpression<BigDecimal> {
        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            return operand1.subtract(operand2);
        }

        @Override
        public String getName() {
            return "-";
        }
    }

    private static class MulExpressionFloat extends ATwoOperandExpression<BigDecimal> {
        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            return operand1.multiply(operand2);
        }

        @Override
        public String getName() {
            return "*";
        }
    }

    private static class DivExpressionFloat extends ATwoOperandExpression<BigDecimal> {
        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            if(operand2.compareTo(BigDecimal.ZERO) == 0){
                throw new BadExpressionFormatException("Division by zero");
            }
            return operand1.divide(operand2, BigDecimal.ROUND_HALF_UP);
        }

        @Override
        public String getName() {
            return "/";
        }
    }

    private static class ExpressionEndWrap{
        private IExpression<BigDecimal> expression;
        private boolean isEndMatched;

        public ExpressionEndWrap(IExpression<BigDecimal> expression, boolean isEndMatched) {
            this.expression = expression;
            this.isEndMatched = isEndMatched;
        }

        public IExpression<BigDecimal> getExpression() {
            return expression;
        }

        public boolean isEndMatched() {
            return isEndMatched;
        }
    }

//        S -> def id(VarList) Afork ; Afork |
//        Afork -> id A; | E;
//        A -> = E | + TEa | - TEa | * TF | / TF
//        E -> Es | { EsList }
//        EsList -> Es, EsList
//        Es -> TEa
//        Ea -> + TEa | /\
//        T -> FTa
//        Ta -> * TF | / TF | /\
//        F -> (E) | num | id | id (Arglist)
//        Arglist -> (E ArglistC)
//        ArglistC -> ,E ArglistC | /\
//        VarList -> (id VarListC)
//        VarListC -> ,id VarListC | /\


    public FloatSolver(TokenReader reader) {
        this.reader = reader;
        contextWrapper = new ExpressionContextWrapper<BigDecimal>(FloatSolver.this);
    }

    public IExpression<BigDecimal> readExpression() throws IOException {
        try{
            exprEndMatched = false;
            IExpression<BigDecimal> expression = matchS();
            if(!hasFileEnd() && !exprEndMatched){
                matchExprEnd();
            }
            return expression;
        } catch (BadExpressionFormatException e){
            last = BigDecimal.ZERO;
            reader.skipRestToDelimiter();
            throw e;
        }
    }

    public BigDecimal solveExpression(IExpression<BigDecimal> expression) {
        last = expression.solve(contextWrapper);
        return last;
    }

    private IExpression<BigDecimal> matchS() throws IOException {
        while (true) {
            if (hasFileEnd()) {
                return null;
            } else if (hasExprEnd()) {
                matchExprEnd();
            } else {
                break;
            }
        }
        if(hasToken(TokenReader.TokenType.precisionKey)){
            matchTerm(TokenReader.TokenType.precisionKey);
            TokenReader.Token number = matchTerm(TokenReader.TokenType.number);
            matchExprEnd();
            try{
                precision = Integer.parseInt(number.value);
            } catch (NumberFormatException e){
                throw new BadExpressionFormatException("Bad precision argument");
            }
            return matchS();
        } else if (extendedExpression && hasMatchToken(TokenReader.TokenType.def)) {
            last = BigDecimal.ZERO;
            FunctionDefinition<BigDecimal> functionDefinition = matchFN();
            contextWrapper.registerFunction(functionDefinition);
            return matchS();
        } else {
            ExpressionEndWrap a = matchA();
            exprEndMatched = a.isEndMatched();
            return a.getExpression();
        }
    }

    private ExpressionEndWrap matchAfork() {
        if(hasMatchToken(TokenReader.TokenType.forKey)) {
            matchTerm(TokenReader.TokenType.lBracket);
            String iteratorName = matchTerm(TokenReader.TokenType.identifier).value;
            matchTerm(TokenReader.TokenType.argSeparator);
            IExpression<BigDecimal> initExpression = matchE();
            matchTerm(TokenReader.TokenType.argSeparator);
            IExpression<BigDecimal> maxExpression = matchE();
            matchTerm(TokenReader.TokenType.rBracket);
            ExpressionEndWrap body = matchA();
            if(!body.isEndMatched()){
                matchExprEnd();
            }
            return new ExpressionEndWrap(new ForExpression(iteratorName,initExpression,maxExpression,body.getExpression()),true);
        } else if(hasMatchToken(TokenReader.TokenType.ifKey)){
            matchTerm(TokenReader.TokenType.lBracket);
            IExpression<BigDecimal> condExpression = matchE();
            matchTerm(TokenReader.TokenType.rBracket);
            ExpressionEndWrap ifExpressionEndWrap = matchA();
            if(!ifExpressionEndWrap.isEndMatched()){
                matchExprEnd();
            }
            ExpressionEndWrap elseExpressionEndWrap = null;
            if(hasMatchToken(TokenReader.TokenType.elseKey)){
                elseExpressionEndWrap = matchA();
                if(!elseExpressionEndWrap.isEndMatched()){
                    matchExprEnd();
                }
            }
            return new ExpressionEndWrap(new IfExpression(condExpression,ifExpressionEndWrap.getExpression(),elseExpressionEndWrap != null ? elseExpressionEndWrap.getExpression() : null),true);
        } else if (hasToken(TokenReader.TokenType.identifier) && extendedExpression) {
            return new ExpressionEndWrap(matchAs(matchTerm(TokenReader.TokenType.identifier)),hasMatchExprEnd());
        } else {
            return new ExpressionEndWrap(matchE(),hasMatchExprEnd());
        }
    }

    private IExpression<BigDecimal> matchAs(TokenReader.Token identifier){
        ATwoOperandExpression<BigDecimal> leftNew;
        if (hasMatchToken(TokenReader.TokenType.equals)) {
            return new VariableAssignment<BigDecimal>(identifier.value, matchE());
        } else {
            IExpression<BigDecimal> left;
            if(hasToken(TokenReader.TokenType.lBracket)) {
                left = matchFuncCallRest(identifier);
            } else {
                left = new VariableExpression<BigDecimal>(identifier.value);
            }
            if (hasMatchToken(TokenReader.TokenType.plus)) {
                leftNew = new AddExpressionFloat();
                leftNew.setOperand(0, left);
                leftNew.setOperand(1, matchT());
                return matchEa(leftNew);

            } else if (hasMatchToken(TokenReader.TokenType.minus)) {
                leftNew = new SubExpressionFloat();
                leftNew.setOperand(0, left);
                leftNew.setOperand(1, matchT());
                return matchEa(leftNew);

            } else if (hasMatchToken(TokenReader.TokenType.asterisk)) {
                leftNew = new MulExpressionFloat();
                leftNew.setOperand(0, left);
                leftNew.setOperand(1, matchF());
                return matchTa(leftNew);

            } else if (hasMatchToken(TokenReader.TokenType.slash)) {
                leftNew = new DivExpressionFloat();
                leftNew.setOperand(0, left);
                leftNew.setOperand(1, matchF());
                return matchTa(leftNew);

            } else if(hasExprEnd()){
                return left;
            }
        }

        throw new BadExpressionFormatException(reader.printBuffer());
    }

    private ExpressionEndWrap matchA(){
        if(hasMatchToken(TokenReader.TokenType.lBrace)){
            CompoundExpression<BigDecimal> compoundExpression = new CompoundExpression<BigDecimal>();
            ExpressionEndWrap a = matchA();
            compoundExpression.add(a.getExpression());
            while (true) {
                if (hasMatchToken(TokenReader.TokenType.rBrace)) {
                    hasMatchExprEnd();
                    return new ExpressionEndWrap(compoundExpression,true);
                } else if (a.isEndMatched()) {
                    a = matchA();
                    compoundExpression.add(a.getExpression());
                } else {
                    throw new BadExpressionFormatException(reader.printBuffer());
                }
            }
        } else {
            return matchAfork();
        }
    }

    private IExpression<BigDecimal> matchE() {
        IExpression<BigDecimal> left = matchT();
        return matchEa(left);
    }

    private IExpression<BigDecimal> matchEa(IExpression<BigDecimal> left) {
        ATwoOperandExpression<BigDecimal> leftNew;
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

    private IExpression<BigDecimal> matchT() {
        IExpression<BigDecimal> left = matchF();
        return matchTa(left);
    }

    private IExpression<BigDecimal> matchTa(IExpression<BigDecimal> left) {
        ATwoOperandExpression<BigDecimal> leftNew;
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


    private IExpression<BigDecimal> matchF() {
        IExpression<BigDecimal> res;
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
//                if(inFuncDef && !funcArgs.contains(token.value)){
//                    throw new BadExpressionFormatException("Can't use variables in function.");
//                }
                return new VariableExpression<BigDecimal>(token.value);
            }
        } else if (hasToken(TokenReader.TokenType.number)) {
            token = matchTerm(TokenReader.TokenType.number);
            return new ConstantExpression<BigDecimal>(new BigDecimal(token.value).setScale(precision,BigDecimal.ROUND_HALF_UP));
        }
        throw new BadExpressionFormatException("Unexpected token type - " + reader.printBuffer());
    }

    private IExpression<BigDecimal> matchFuncCallRest(TokenReader.Token token) {
        matchTerm(TokenReader.TokenType.lBracket);
        ArrayList<IExpression<BigDecimal>> expressions = new ArrayList<IExpression<BigDecimal>>();
        if(!hasToken(TokenReader.TokenType.rBracket)) {
            expressions.add(matchE());
            matchExprListC(expressions);
        }
        matchTerm(TokenReader.TokenType.rBracket);
        FunctionCallExpression<BigDecimal> functionCallExpression = new FunctionCallExpression<BigDecimal>(contextWrapper.getFunction(token.value));
        if(functionCallExpression.getArity()!=expressions.size()){
            throw new BadExpressionFormatException("Bad number of arguments.");
        }
        int i=0;
        for(IExpression<BigDecimal> expression : expressions){
            functionCallExpression.setOperand(i, expression);
            ++i;
        }
        return functionCallExpression;
    }


    private FunctionDefinition<BigDecimal> matchFN() {
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
            ExpressionEndWrap a = matchA();
            FunctionDefinition<BigDecimal> functionDefinition = new FunctionDefinition<BigDecimal>(tokenIdentifier.value, arguments, a.getExpression());
            if(!a.isEndMatched()){
                matchExprEnd();
            }
            return functionDefinition;
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

    private void matchExprListC(ArrayList<IExpression<BigDecimal>> arguments){
        if(hasToken(TokenReader.TokenType.argSeparator)){
            matchTerm(TokenReader.TokenType.argSeparator);
            arguments.add(matchE());
//            arguments.add(matchArg());
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


    private TokenReader.Token matchTerm(TokenReader.TokenType type) {
        return reader.matchToken(type);
    }

    private void matchExprEnd() {
        try {
            reader.skipDelimiter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasExprEnd() {
        return reader.hasDelimiter();
    }

    private boolean hasMatchExprEnd(){
        if(hasExprEnd()){
            matchExprEnd();
            return true;
        }
        return false;
    }

    private boolean hasToken(TokenReader.TokenType tokenType) {
        return reader.hasToken(tokenType);
    }

    private boolean hasMatchToken(TokenReader.TokenType tokenType) {
        if(hasToken(tokenType)){
            matchTerm(tokenType);
            return true;
        }
        return false;
    }

    private boolean hasFileEnd(){
        return reader.hasFileEnd();
    }

    public int getPrecision() {
        return precision;
    }
}