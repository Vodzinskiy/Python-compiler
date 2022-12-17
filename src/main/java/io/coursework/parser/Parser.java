package io.coursework.parser;

import io.coursework.lexer.Token;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    private final ArrayList<Token> tokenList;
    private final String code;
    private final ArrayList<ArrayList<Token>> sentences = new ArrayList<>();
    private String[] stringSentences;

    private final Program program = new Program();
    int i = 0;

    public Parser(ArrayList<Token> tokenList, String code) {
        this.tokenList = tokenList;
        this.code = code;
    }

    public void start() {
        intoSentence();
        intoStringSentences(code);
        while (i < sentences.size()) {
            program.addContent(getContents(sentences.get(i), 0));
            i++;
        }
        System.out.println(program);

    }

    private Contents getContents(ArrayList<Token> sentence, int k) {
        Contents c = null;
        if (Objects.equals(sentence.get(k).getValue(), "def")) {
            functionCheck(sentence);
            int j = 3;
            ArrayList<Variable> args = new ArrayList<>();
            while (!Objects.equals(sentence.get(j).getValue(), ")")) {
                if (!Objects.equals(sentence.get(j).getValue(), ",")) {
                    args.add(new Variable(sentence.get(j).getValue()));
                }
                j++;
            }
            ArrayList<Contents> content = new ArrayList<>();
            i++;
            try {
                while (Objects.equals(sentences.get(i).get(k).getValue(), "\t")) {
                    content.add(getContents(sentences.get(i), k+1));
                    i++;
                }
            } catch (Exception ignored) {
            }

            c = new Function(sentence.get(1).getValue(), args, content);
        }

        if (Objects.equals(sentence.get(k).getName(), "IDENTIFIER") && Objects.equals(sentence.get(k+1).getValue(), "=")) {
            assignmentCheck(sentence, k);

            c = new Assignment(new Variable(sentence.get(k).getValue()), Integer.parseInt(sentence.get(k + 2).getValue()));
        }
        return c;
    }

    private void functionCheck(ArrayList<Token> tokens) {
        if (!Objects.equals(tokens.get(1).getName(), "IDENTIFIER")) {
            errorMassage(tokens.get(1), "SyntaxError: invalid syntax");
        }
        if (!Objects.equals(tokens.get(2).getValue(), "(")) {
            errorMassage(tokens.get(2), "SyntaxError: invalid syntax");
        }
        if (!Objects.equals(tokens.get(tokens.size() - 1).getValue(), ":")) {
            errorMassage(tokens.get(tokens.size() - 1), "SyntaxError: invalid syntax");
        }
        if (!Objects.equals(tokens.get(tokens.size() - 2).getValue(), ")")) {
            errorMassage(tokens.get(tokens.size() - 2), "SyntaxError: invalid syntax");
        }
        for (int i = 3; i < tokens.size() - 2; i++) {
            if (!Objects.equals(tokens.get(i).getName(), "IDENTIFIER") && i % 2 == 1) {
                errorMassage(tokens.get(i), "SyntaxError: invalid syntax");
            }
            if (!Objects.equals(tokens.get(i).getValue(), ",") && i % 2 == 0) {
                errorMassage(tokens.get(i), "SyntaxError: invalid syntax");
            }
        }
    }

    private void assignmentCheck(ArrayList<Token> tokens, int k) {
        if (tokens.size() != k+3) {
            errorMassage(tokens.get(k+3), "SyntaxError: invalid syntax");
        }
        if (!tokens.get(k+2).getValue().matches("-?\\d+(\\.\\d+)?")) {
            errorMassage(tokens.get(k+2), "SyntaxError: invalid syntax");
        }

    }

    private void errorMassage(Token token, String text) {
        System.out.println("line " + token.getPosition().getLine());
        System.out.println(stringSentences[token.getPosition().getLine() - 1]);
        System.out.println(" ".repeat(token.getPosition().getSymbol() - 1) + "^".repeat(token.getValue().length()));
        System.out.println(text);

        System.exit(1);
    }

    private void intoSentence() {
        ArrayList<Token> temp = new ArrayList<>();
        for (Token token : tokenList) {
            if (!Objects.equals(token.getValue(), "\n")) {
                temp.add(token);
            } else {
                if (temp.size() > 0) {
                    sentences.add(temp);
                }
                temp = new ArrayList<>();
            }
        }
        if (!temp.isEmpty()) {
            sentences.add(temp);
        }
    }

    public void intoStringSentences(String code) {
        stringSentences = code.split("\n");
    }
}
