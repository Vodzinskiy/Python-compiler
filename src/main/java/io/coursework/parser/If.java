package io.coursework.parser;

import io.coursework.parser.expression.BoolExpression;
import io.coursework.parser.expression.Expression;

import java.util.ArrayList;

public class If extends Contents{
    private BoolExpression bool = null;
    private Expression a;
    private Expression b;
    private String compares;
    private final ArrayList<Contents> body;

    public If(Expression a, Expression b, String compares, ArrayList<Contents> body) {
        this.a = a;
        this.b = b;
        this.compares = compares;
        this.body = body;
    }

    public If(BoolExpression bool, ArrayList<Contents> body) {
        this.bool = bool;
        this.body = body;
    }

    public BoolExpression getBool() {
        return bool;
    }

    public Expression getA() {
        return a;
    }

    public Expression getB() {
        return b;
    }

    public String getCompares() {
        return compares;
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
        StringBuilder str = new StringBuilder("\t".repeat(l) + "If:\n");
        str.append("\t".repeat(l + 1)).append("Condition:\n");
        if (bool!=null) {
            str.append("\t".repeat(l + 2)).append("Boolean variable: ").append(bool).append("\n");
        } else {
            str.append("\t".repeat(l + 2)).append("Comparison: ").append("\n");
            str.append("\t".repeat(l + 3)).append("Sign: ").append(compares).append("\n");
            str.append("\t".repeat(l + 3)).append("Left operand: ").append(a).append("\n");
            str.append("\t".repeat(l + 3)).append("Right operand: ").append(b).append("\n");
        }
        str.append("\t".repeat(l + 1)).append("Body:\n").append(bodyToString(l));
        return String.valueOf(str);
    }
}