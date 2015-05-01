package expression;

import java.util.ArrayList;

/**
 * Created by Jan Mares on 01.05.2015
 */
public class CompoundExpression<T> implements IOperandExpression<T> {
    ArrayList<IExpression<T>> expressions = new ArrayList<IExpression<T>>();

    public void add(IExpression<T> expression){
        expressions.add(expression);
    }

    @Override
    public int getArity() {
        return expressions.size();
    }

    @Override
    public void setOperand(int index, IExpression<T> operand) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public T solve(IExpressionContext<T> context) {
        if(expressions.isEmpty()){
            throw new RuntimeException("Empty compound expression.");
        }
        T last = null;
        for (IExpression<T> expression : expressions) {
            last = context.postProcess(expression.solve(context));
        }
        return last;
    }
}
