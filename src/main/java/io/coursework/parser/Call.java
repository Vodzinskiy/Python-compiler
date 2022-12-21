package io.coursework.parser;

import io.coursework.parser.expression.Expression;

import java.util.ArrayList;

public class Call extends Contents {
    private final String name;
    private final ArrayList<Expression> args;

    public Call(String name, ArrayList<Expression> args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Expression> getArgs() {
        return args;
    }

    private String argsToString(int l) {
        if (args.isEmpty()) {
            return "";
        } else {
            StringBuilder str = new StringBuilder();
            for (Expression a : args) {
                str.append("\t".repeat(l + 2)).append("argument: ").append(a).append("\n");
            }
            return String.valueOf(str);
        }
    }

    @Override
    public String toString(int l) {
        return "\t".repeat(l) + "Call " + name + " :\n" +
                "\t".repeat(l + 1) + "Args: \n" + argsToString(l) + "\n";
    }
}
