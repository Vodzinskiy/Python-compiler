package io.coursework.parser.expression;

public class NumberExpression extends Expression {
    private final int num;

    public NumberExpression(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String getValue() {
        return String.valueOf(num);
    }

    @Override
    public String getType(){
        return "INTEGER";
    }

    @Override
    public String toString() {
        return String.valueOf(num);
    }
}
