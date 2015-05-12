package expression;

/**
 * Created by Jan Mares on 31.03.2015
 */

/**
 * Represents constant expression of generic type.
 *
 * @param <T> type of expression
 */
public class ConstantExpression<T> implements IExpression<T> {
    private T constant;

    public ConstantExpression(T constant) {
        this.constant = constant;
    }

    @Override
    public T solve(IExpressionContext<T> context) {
        return constant;
    }

    public T getConstant() {
        return constant;
    }
}