package metadata;

import expression.IExpression;
import parsing.TokenReader;

import java.util.ArrayList;

/**
 * Created by Jan Mares on 31.03.2015
 */

public class FunctionDefinition<T> {
    private ArrayList<TokenReader.Token> arguments;
    private IExpression<T> functionExpression;
    private String funcName;


    public FunctionDefinition(String funcName, ArrayList<TokenReader.Token> arguments) {
        this.arguments = arguments;
        this.funcName = funcName;
    }

    public void setFunctionExpression(IExpression<T> functionExpression) {
        this.functionExpression = functionExpression;
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