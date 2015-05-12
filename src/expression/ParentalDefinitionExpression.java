package expression;

/**
 * Created by Jan Mares on 31.03.2015
 */

/**
 * Represents constant expression of generic type.
 *
 * @param <T> type of expression
 */
public class ParentalDefinitionExpression<T> implements IExpression<T> {
    private String varName;
    public ParentalDefinitionExpression(String varName) {
        this.varName = varName;
    }

    @Override
    public T solve(IExpressionContext<T> context) {
        context.defineParentVariable(varName);
        return null;
    }
}