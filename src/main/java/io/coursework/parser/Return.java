package io.coursework.parser;

public class Return extends Contents {
    private final Variable variable;

    public Return(Variable variable) {
        this.variable = variable;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString(int l) {
        return "\t".repeat(l) + "Return:\n" +
                "\t".repeat(l + 1) + "Variable: " + variable.getName() + "\n";
    }
}
