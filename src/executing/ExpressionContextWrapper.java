package executing;

import exception.BadExpressionFormatException;
import expression.IExpression;
import expression.IExpressionContext;
import metadata.FunctionDefinition;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Jan Mares on 31.03.2015
 */

public class ExpressionContextWrapper<T> implements IExpressionContext<T> {
    private IExpressionContext<T> parent;
    private HashMap<String, IExpression<T>> variablesValues = new HashMap<String, IExpression<T>>();
    private HashMap<String, FunctionDefinition<T>> functionsDefinition = new HashMap<String, FunctionDefinition<T>>();
    private HashSet<String> parentVariables = new HashSet<String>();

    public ExpressionContextWrapper(IExpressionContext<T> parent) {
        this.parent = parent;
        parentVariables.add("last");
    }

    @Override
    public IExpression<T> getVariableValue(String varName) {
        if(functionsDefinition.containsKey(varName)){
            throw new BadExpressionFormatException("Function with same name already exists.");
        }

        if (parent != null && parentVariables.contains(varName)) {
            return parent.getVariableValue(varName);
        } else if(variablesValues.containsKey(varName)){
            return variablesValues.get(varName);
        }
        throw new BadExpressionFormatException("Unknown variable - " + varName);
    }

    @Override
    public void setVariableValue(String varName, IExpression<T> expression) {
        if(parent != null && parentVariables.contains(varName)){
            parent.setVariableValue(varName,expression);
        } else {
            if (functionsDefinition.containsKey(varName)) {
                throw new BadExpressionFormatException("Function with same name already exists.");
            } else {
                variablesValues.put(varName,expression);
            }
        }
    }

    @Override
    public void defineParentVariable(String varName){
        parentVariables.add(varName);
    }

    @Override
    public T postProcess(T result) {
        return parent.postProcess(result);
    }

    @Override
    public boolean hasVariable(String varName){
        return variablesValues.containsKey(varName) || parent.hasVariable(varName);
    }

    @Override
    public void registerFunction(FunctionDefinition<T> functionDefinition){
        if(hasVariable(functionDefinition.getFuncName()) || functionDefinition.getFuncName().equals("last")){
            throw new BadExpressionFormatException("Variable with same name already exists.");
        } else{
            functionsDefinition.put(functionDefinition.getFuncName(),functionDefinition);
        }
    }

    @Override
    public FunctionDefinition<T> getFunction(String value) {
        if(!functionsDefinition.containsKey(value)){
            return parent.getFunction(value);
        }
        return functionsDefinition.get(value);
    }
}