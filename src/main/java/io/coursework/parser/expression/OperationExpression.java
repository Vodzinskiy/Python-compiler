package io.coursework.parser.expression;

public class OperationExpression extends Expression {
    private final String operator;
    private final Expression a;
    private final Expression b;

    public OperationExpression(String operator, Expression a, Expression b) {
        this.operator = operator;
        this.a = a;
        this.b = b;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getA() {
        return a;
    }

    public Expression getB() {
        return b;
    }

    @Override
    public String getType(){
        return "INTEGER";
    }

    @Override
    public String toString() {
        return a + " " + operator + " " + b;
    }
}
