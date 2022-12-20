package io.coursework.parser.expression;

import io.coursework.parser.Variable;

public class VariableExpression extends Expression {
    private final Variable variable;

    public VariableExpression(Variable variable) {
        this.variable = variable;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return  variable.toString() + " [" + variable.getType() + "]";
    }
}
