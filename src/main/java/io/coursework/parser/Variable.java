package io.coursework.parser;

public class Variable {
    private String name;
    private String type;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
