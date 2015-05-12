package expression;

import executing.ExpressionContextWrapper;
import metadata.FunctionDefinition;

/**
 * Created by Jan Mares on 31.03.2015
 */

public class FunctionCallExpression<T> implements IOperandExpression<T> {
    FunctionDefinition<T> functionDefinition;
    IExpression<T>[] operands;

    public FunctionCallExpression(FunctionDefinition<T> functionDefinition) {
        this.functionDefinition = functionDefinition;
        operands = (IExpression<T>[]) new IExpression[functionDefinition.getArgumentCount()];
    }

    @Override
    public void setOperand(int index, IExpression<T> operand) {
        operands[index] = operand;
    }

    @Override
    public int getArity() {
        return functionDefinition.getArgumentCount();
    }

    @Override
    public String getName() {
        return functionDefinition.getFuncName();
    }

    @Override
    public T solve(IExpressionContext<T> context) {
        ExpressionContextWrapper<T> expressionContextWrapper = new ExpressionContextWrapper<T>(context);
        for (int i = 0; i < functionDefinition.getArgumentCount(); i++) {
            expressionContextWrapper.setVariableValue(functionDefinition.getArgumentName(i), new ConstantExpression<T>(operands[i].solve(context)));
        }
        return context.postProcess(functionDefinition.getFunctionExpression().solve(expressionContextWrapper));
    }
}