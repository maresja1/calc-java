package expression;

/**
 * Created by Jan Mares on 31.03.2015
 */

public class VariableExpression<T> implements IExpression<T> {
    private String name;

    public VariableExpression(String name) {
        this.name = name;
    }

    @Override
    public T solve(IExpressionContext<T> context) {
        return context.postProcess(context.getVariableValue(name).solve(context));
    }
}