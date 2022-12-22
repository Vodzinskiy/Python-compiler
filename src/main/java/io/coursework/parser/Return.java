package io.coursework.parser;

import io.coursework.parser.expression.Expression;

public class Return extends Contents {
    private Expression expression = null;
    private Call call = null;

    public Return(Expression expression) {
        this.expression = expression;
    }

    public Return(Call call) {
        this.call = call;
    }

    public Expression getExpression() {
        return expression;
    }

    public Call getCall() {
        return call;
    }

    @Override
    public String toString(int l) {
        if (call == null) {
            return "\t".repeat(l) + "Return:\n" +
                    "\t".repeat(l + 1) + "Value: " + expression + "\n";
        } else {
            return "\t".repeat(l) + "Return:\n" +
                    call.toString(l + 1) + "\n";
        }
    }
}
