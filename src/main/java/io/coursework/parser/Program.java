package io.coursework.parser;

import java.util.ArrayList;

public class Program {
    public ArrayList<Contents> contents = new ArrayList<>();

    public void addContent(Contents content) {
        contents.add(content);
    }

    public ArrayList<Contents> getContents() {
        return contents;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Program:\n");

        for (Contents c : contents) {
            str.append(c.toString(1));
        }
        return String.valueOf(str);
    }
}
