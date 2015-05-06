package expression;

/**
 * Created by Jan Mares on 31.03.2015
 */
public class VariableAssignment<T> implements IExpression<T> {
    private IExpression<T> assignmentExpression;
    private String name;

    public VariableAssignment(String name, IExpression<T> assignmentExpression) {
        this.assignmentExpression = assignmentExpression;
        this.name = name;
    }

    @Override
    public T solve(IExpressionContext<T> context) {
        T value = assignmentExpression.solve(context);
        context.setVariableValue(name, new ConstantExpression<T>(value), false);
        return value;
    }
}