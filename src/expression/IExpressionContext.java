package expression;

/**
 * Created by Jan Mares on 31.03.2015
 */
public interface IExpressionContext<T> {
    public boolean hasVariable(String varName);

    public IExpression<T> getVariableValue(String varName);

    public void setVariableValue(String varName, IExpression<T> expression);

    public T postProcess(T result);
}