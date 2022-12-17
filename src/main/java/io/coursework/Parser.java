package io.coursework;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    private final ArrayList<String> lexemes;
    private final ArrayList<ArrayList<String>> sentences = new ArrayList<>();

    public Parser(ArrayList<String> lexemes) {
        this.lexemes = lexemes;
    }

    public void start() {
        intoSentence();
        for (ArrayList<String> sentence: sentences) {

        }
    }

    private void intoSentence() {
        ArrayList<String> temp = new ArrayList<>();
        for (String lex: lexemes) {
            if (!Objects.equals(lex, "\n")) {
                temp.add(lex);
            } else {
                if (temp.size()>0) {
                    sentences.add(temp);
                }
                temp = new ArrayList<>();
            }
        }
    }
}
