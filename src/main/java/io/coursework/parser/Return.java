package io.coursework.parser;

public class Return extends Contents {
    private final Exception exception;

    public Return(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String toString(int l) {
        return "\t".repeat(l) + "Return:\n" +
                "\t".repeat(l + 1) + "Variable: " + exception + "\n";
    }
}
