package expression;

import java.math.BigDecimal;

/**
 * Created by Jan Mares on 06.05.2015
 */
public class IfExpression implements IExpression<BigDecimal> {
    IExpression<BigDecimal> condExpression;
    IExpression<BigDecimal> ifBody;
    IExpression<BigDecimal> elseBody;

    public IfExpression(IExpression<BigDecimal> condExpression, IExpression<BigDecimal> ifBody, IExpression<BigDecimal> elseBody) {
        this.condExpression = condExpression;
        this.ifBody = ifBody;
        this.elseBody = elseBody;
    }

    @Override
    public BigDecimal solve(IExpressionContext<BigDecimal> context) {
        BigDecimal decimal = condExpression.solve(context);
        if(decimal.compareTo(BigDecimal.ONE) != 0){
            return ifBody.solve(context);
        } else if(elseBody != null) {
            return elseBody.solve(context);
        }
        return BigDecimal.ZERO;
    }
}
