package io.coursework.parser;

import io.coursework.parser.expression.Expression;

public class Assignment extends Contents {
    private final Variable variable;
    private final Expression expression;

    public Assignment(Variable variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }


    public Variable getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }


    @Override
    public String toString(int l) {
        return "\t".repeat(l) + "Assignment:\n" +
                "\t".repeat(l + 1) + "Variable: " + variable.getName() + "\n" +
                "\t".repeat(l + 1) + "Expression: " + expression + "\n";
    }
}
