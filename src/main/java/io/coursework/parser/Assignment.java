package io.coursework.parser;

public class Assignment implements Contents {
    private final Variable variable;
    private final int constant;

    public Assignment(Variable variable, int constant) {
        this.variable = variable;
        this.constant = constant;
    }


    public Variable getVariable() {
        return variable;
    }

    public int getConstant() {
        return constant;
    }


    @Override
    public String toString(int l) {
        return "\t".repeat(l) + "Assignment:\n" +
                "\t".repeat(l + 1) + "Variable: " + variable.getName() + "\n" +
                "\t".repeat(l + 1) + "Expression: " + constant;
    }
}
