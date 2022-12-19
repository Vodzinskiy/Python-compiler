package io.coursework.parser;

import io.coursework.parser.expression.Expression;

public class Variable {
    private String name;
    private String type;

    private Expression value;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Variable(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
}
