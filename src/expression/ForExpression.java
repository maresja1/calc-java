package expression;

import executing.ExpressionContextWrapper;

import java.math.BigDecimal;

/**
 * Created by Jan Mares on 04.05.2015
 */
public class ForExpression implements IExpression<BigDecimal> {
    String iteratorName;
    IExpression<BigDecimal> initialValue;
    IExpression<BigDecimal> maxValue;
    IExpression<BigDecimal> bodyExpression;

    public ForExpression(String iteratorName, IExpression<BigDecimal> initialValue, IExpression<BigDecimal> maxValue, IExpression<BigDecimal> bodyExpression) {
        this.iteratorName = iteratorName;
        this.initialValue = initialValue;
        this.maxValue = maxValue;
        this.bodyExpression = bodyExpression;
    }

    private boolean isIteratorSmaller(IExpressionContext<BigDecimal> context){
        BigDecimal iterator = context.getVariableValue(iteratorName).solve(context);
        BigDecimal maxValue = this.maxValue.solve(context);
        return iterator.compareTo(maxValue) < 0;
    }

    private void incrementIterator(IExpressionContext<BigDecimal> context){
        BigDecimal iterator = context.getVariableValue(iteratorName).solve(context);
        context.setVariableValue(iteratorName, new ConstantExpression<BigDecimal>(iterator.add(new BigDecimal(1))));
    }

    @Override
    public BigDecimal solve(IExpressionContext<BigDecimal> context) {
        context.setVariableValue(iteratorName,new ConstantExpression<BigDecimal>(initialValue.solve(context)));
        BigDecimal result = new BigDecimal(0);
        while(isIteratorSmaller(context)){
            bodyExpression.solve(context);
            result = result.add(new BigDecimal(1));
            incrementIterator(context);
        }
        return result;
    }
}
