package expression;

/**
 * Created by Jan Mares on 31.03.2015
 */

/**
 * Expression of generic type with two operators.
 *
 * @param <T> type of expression
 */
public abstract class ATwoOperandExpression<T> implements IOperandExpression<T> {
    protected IExpression<T>[] operands;

    public ATwoOperandExpression() {
        operands = (IExpression<T>[]) new IExpression[2];
    }

    public ATwoOperandExpression(IExpression<T> operand1, IExpression<T> operand2) {
        operands = (IExpression<T>[]) new IExpression[2];
        operands[0] = operand1;
        operands[1] = operand2;
    }

    @Override
    public T solve(IExpressionContext<T> context) {

        return context.postProcess(doOperation(operands[0].solve(context), operands[1].solve(context)));
    }

    @Override
    public void setOperand(int index, IExpression<T> operand) {
        assert index < 2;
        operands[index] = operand;
    }

    @Override
    public int getArity() {
        return 2;
    }

    protected abstract T doOperation(T operand1, T operand2);
}