package expression;

/**
 * Created by Jan Mares on 31.03.2015
 */

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