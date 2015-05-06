package expression;

import metadata.FunctionDefinition;

/**
 * Created by Jan Mares on 31.03.2015
 */
public interface IExpressionContext<T> {
    public boolean hasVariable(String varName);

    public IExpression<T> getVariableValue(String varName);

    public void setVariableValue(String varName, IExpression<T> expression, boolean override);

    public T postProcess(T result);

    void registerFunction(FunctionDefinition<T> functionDefinition);

    FunctionDefinition<T> getFunction(String value);
}