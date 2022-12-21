package io.coursework.parser;

public class Print extends Contents {
    private String text = null;
    private Variable variable = null;
    private Call call = null;

    public Print(Call call) {
        this.call = call;
    }

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

    public Call getCall() {
        return call;
    }

    @Override
    public String toString(int l) {
        if (variable != null) {
            return "\t".repeat(l) + "Print:\n" +
                    "\t".repeat(l + 1) + "Expression:\n" +
                    "\t".repeat(l + 2) + "Variable: \"" + variable.getName() + "\"\n";
        }
        if (call != null) {
            return "\t".repeat(l) + "Print:\n" +
                    "\t".repeat(l + 1) + "Expression:\n" +
                    "\t".repeat(l + 2) + "Function Call: " + call.getName() + "\n";
        } else {
            return "\t".repeat(l) + "Print:\n" +
                    "\t".repeat(l + 1) + "Expression:\n" +
                    "\t".repeat(l + 2) + "Value: " + text + "\n";
        }
    }
}
