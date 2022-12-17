package io.coursework.lexer;

public class Position {
    private final int line;
    private final int symbol;

    public Position(int line, int symbol) {
        this.line = line;
        this.symbol = symbol;
    }

    public int getLine() {
        return line;
    }

    public int getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return line + ":" + symbol;
    }
}

