package io.coursework.parser;

import io.coursework.parser.expression.Expression;

import java.util.ArrayList;

public class For extends Contents {

    private final Variable i;
    private final Expression start;
    private final Expression end;
    private final ArrayList<Contents> body;

    public For(Variable i, Expression start, Expression end, ArrayList<Contents> body) {
        this.i = i;
        this.start = start;
        this.end = end;
        this.body = body;
    }

    public Variable getI() {
        return i;
    }

    public ArrayList<Contents> getBody() {
        return body;
    }

    public Expression getStart() {
        return start;
    }

    public Expression getEnd() {
        return end;
    }

    private String bodyToString(int l) {
        StringBuilder str = new StringBuilder();
        for (Contents c : body) {
            str.append(c.toString(l + 2));
        }
        return String.valueOf(str);
    }

    @Override
    public String toString(int l) {
        return "\t".repeat(l) + "For:\n" +
                "\t".repeat(l + 1) + "Range:\n" +
                "\t".repeat(l + 2) + "Start: " + start + "\n" +
                "\t".repeat(l + 2) + "End: " + end + "\n" +
                "\t".repeat(l + 1) + "Body:\n" +
                bodyToString(l);
    }
}
