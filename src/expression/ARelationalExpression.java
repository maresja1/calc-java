package expression;

import exception.BadExpressionFormatException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

/**
 * Created by Jan Mares on 06.05.2015
 */
public abstract class ARelationalExpression extends ATwoOperandExpression<BigDecimal>{
    @Override
    public String getName() {
        return "rel";
    }

    private static class LeqOperator extends ARelationalExpression{

        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            return operand1.compareTo(operand2) == 1 ? BigDecimal.ZERO : BigDecimal.ONE;
        }
    }

    private static class GeqOperator extends ARelationalExpression{

        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            return operand1.compareTo(operand2) == -1 ? BigDecimal.ZERO : BigDecimal.ONE;
        }
    }

    private static class LeOperator extends ARelationalExpression{

        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            return operand1.compareTo(operand2) == -1 ? BigDecimal.ONE : BigDecimal.ZERO;
        }
    }

    private static class GeOperator extends ARelationalExpression{

        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            return operand1.compareTo(operand2) == 1 ? BigDecimal.ONE : BigDecimal.ZERO;
        }
    }

    private static class EqOperator extends ARelationalExpression{

        @Override
        protected BigDecimal doOperation(BigDecimal operand1, BigDecimal operand2) {
            return operand1.compareTo(operand2) == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
        }
    }

    public static ARelationalExpression createOperator(String operator, IExpression<BigDecimal> operand1, IExpression<BigDecimal> operand2){
        ARelationalExpression relationalExpression;
        if(operator.equals("<")){
            relationalExpression = new LeOperator();
        } else if(operator.equals(">")){
            relationalExpression = new GeOperator();
        } else if(operator.equals("<=")){
            relationalExpression = new LeqOperator();
        } else if(operator.equals(">=")){
            relationalExpression = new GeqOperator();
        } else if(operator.equals("==")){
            relationalExpression = new EqOperator();
        } else {
            throw new BadExpressionFormatException("Unknown relational operator:" + operator);
        }
        relationalExpression.setOperand(0,operand1);
        relationalExpression.setOperand(1,operand2);
        return relationalExpression;
    }
}
