package io.coursework.parser.expression;

public class BoolExpression extends Expression {
    private final boolean flag;

    public BoolExpression(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }

    @Override
    public String getValue() {
        return String.valueOf(flag);
    }

    @Override
    public String getType(){
        return "BOOLEAN";
    }
    @Override
    public String toString() {
        return String.valueOf(flag);
    }
}
