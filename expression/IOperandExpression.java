package expression;

/**
 * Created by Jan Mares on 31.03.2015
 */
public interface IOperandExpression<T> extends IExpression<T> {
    int getArity();

    void setOperand(int index, IExpression<T> operand);

    String getName();
}