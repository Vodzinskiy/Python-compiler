package io.coursework.parser;

public class Print extends Contents {
    private String text;
    private Variable variable = null;

    public Print(Variable variable) {
        this.variable = variable;
    }

    public Print(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Variable getVariable() {
        return variable;
    }

    @Override
    public String toString(int l) {
        if (variable == null) {
            return "\t".repeat(l) + "Print:\n" +
                    "\t".repeat(l + 1) + "Expression:\n" +
                    "\t".repeat(l + 2) + "Value: \"" + text + "\"\n";
        } else {
            return "\t".repeat(l) + "Print:\n" +
                    "\t".repeat(l + 1) + "Expression:\n" +
                    "\t".repeat(l + 2) + "Variable: " + text + "\n";
        }
    }
}
