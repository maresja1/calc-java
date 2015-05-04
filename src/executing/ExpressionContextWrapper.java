package executing;

import exception.BadExpressionFormatException;
import expression.IExpression;
import expression.IExpressionContext;
import metadata.FunctionDefinition;

import java.util.HashMap;

/**
 * Created by Jan Mares on 31.03.2015
 */

public class ExpressionContextWrapper<T> implements IExpressionContext<T> {
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
            if(!hasVariable(varName)) {
                if (functionsDefinition.containsKey(varName)) {
                    throw new BadExpressionFormatException("Function with same name already exists.");
                }
                variablesValues.put(varName, expression);
            } else {
                if(!variablesValues.containsKey(varName)){
                    parent.setVariableValue(varName,expression);
                } else {
                    variablesValues.put(varName,expression);
                }
            }
        }
    }

    @Override
    public T postProcess(T result) {
        return parent.postProcess(result);
    }

    @Override
    public boolean hasVariable(String varName){
        return variablesValues.containsKey(varName) || parent.hasVariable(varName);
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