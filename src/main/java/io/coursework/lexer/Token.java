package io.coursework.lexer;

public class Token {
    private final String name;
    private final String value;
    private final Position position;

    public Token(String name, String value, Position position) {
        this.name = name;
        this.value = value;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Position getPosition() {
        return position;
    }
}
