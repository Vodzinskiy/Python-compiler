package io.coursework.parser;


import java.util.ArrayList;
import java.util.Arrays;

public class Function implements Contents {
    private String name;
    private ArrayList<Variable> args;
    private ArrayList<Contents> body;


    public Function(String name, ArrayList<Variable> args, ArrayList<Contents> body) {
        this.name = name;
        this.args = args;
        this.body = body;
    }


    public String getName() {
        return name;
    }

    public ArrayList<Variable> getArgs() {
        return args;
    }

    public ArrayList<Contents> getBody() {
        return body;
    }

    private String argsToString(int l) {
        if (args.isEmpty()) {
            return "";
        } else {
            StringBuilder str = new StringBuilder();
            for (Variable a : args) {
                str.append("\t".repeat(l + 2)).append("Argument:\n");
                str.append("\t".repeat(l + 3)).append("name: ").append(a.getName()).append("\n");
            }
            return String.valueOf(str);
        }
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
        return "\t".repeat(l) + "Function " + name + ":\n" +
                "\t".repeat(l + 1) + "Args:\n" +
                argsToString(l) +
                "\t".repeat(l + 1) + "Body:\n" +
                bodyToString(l);
    }
}

