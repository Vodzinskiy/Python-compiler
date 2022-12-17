package io.coursework.lexer;


import io.coursework.Parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Lexer {
    private final String code;
    private boolean errors = false;
    private final ArrayList<Token> tokenList = new ArrayList<>();
    private final ArrayList<String> lexemes = new ArrayList<>();
    private final ArrayList<Position> positions = new ArrayList<>();

    private String[] senteces;

    private final String[] cycleoperators = {"for", "while"};
    private final String[] operators = {"+", "-", "%", "*"};
    private final String[] assignement = {"=", "+=", "-=", "=-", "=+"};
    private final String[] bool = {"True", "False"};
    private final String[] logical = {"and", "or"};
    private final String[] compares = {"<", ">", "==", "!=", "<=", ">="};
    private final String[] separator = {",", "\n", "\t", "(", ")", ":"};

    public Lexer(String code) {
        this.code = code;
    }

    /**
     * start of lexical analysis
     */
    public void start() {
        intoLexemes();
        intoSentences(code);
        for (int i = 0; i < lexemes.size(); i++) {
            switch (lexemes.get(i)) {
                case "def" -> tokenList.add(new Token("FUNCTION", lexemes.get(i), positions.get(i)));
                case "(" -> tokenList.add(new Token("OPEN_PARENTHESES", lexemes.get(i), positions.get(i)));
                case ")" -> tokenList.add(new Token("CLOSE_PARENTHESES", lexemes.get(i), positions.get(i)));
                case ":" -> tokenList.add(new Token("COLON", lexemes.get(i), positions.get(i)));
                case "," -> tokenList.add(new Token("COMMA", lexemes.get(i), positions.get(i)));
                case "in" -> tokenList.add(new Token("IN", lexemes.get(i), positions.get(i)));
                case "range" -> tokenList.add(new Token("RANGE", lexemes.get(i), positions.get(i)));
                case "if" -> tokenList.add(new Token("IF", lexemes.get(i), positions.get(i)));
                case "return" -> tokenList.add(new Token("RETURN", lexemes.get(i), positions.get(i)));
                case "print" -> tokenList.add(new Token("PRINT", lexemes.get(i), positions.get(i)));
                case "\t" -> tokenList.add(new Token("TAB", lexemes.get(i), positions.get(i)));
                case "\n" -> tokenList.add(new Token("NEW_LINE", lexemes.get(i), positions.get(i)));
                default -> {
                    if (Arrays.asList(assignement).contains(lexemes.get(i))) {
                        tokenList.add(new Token("ASSIGNMENT", lexemes.get(i), positions.get(i)));
                    } else if (Arrays.asList(cycleoperators).contains(lexemes.get(i))) {
                        tokenList.add(new Token("CYCLE", lexemes.get(i), positions.get(i)));
                    } else if (Arrays.asList(bool).contains(lexemes.get(i))) {
                        tokenList.add(new Token("BOOLEAN", lexemes.get(i), positions.get(i)));
                    } else if (Arrays.asList(operators).contains(lexemes.get(i))) {
                        tokenList.add(new Token("OPERATORS", lexemes.get(i), positions.get(i)));
                    } else if (Arrays.asList(compares).contains(lexemes.get(i))) {
                        tokenList.add(new Token("COMPARATORS", lexemes.get(i), positions.get(i)));
                    } else if (Arrays.asList(logical).contains(lexemes.get(i))) {
                        tokenList.add(new Token("LOGIC", lexemes.get(i), positions.get(i)));
                    } else if (isNumber(lexemes.get(i))) {
                        tokenList.add(new Token("NUMBER", lexemes.get(i), positions.get(i)));
                    } else if (isName(lexemes.get(i))) {
                        tokenList.add(new Token("IDENTIFIER", lexemes.get(i), positions.get(i)));
                    } else {
                        errors = true;
                        System.out.println("\nline " + positions.get(i).getLine());
                        System.out.println(senteces[positions.get(i).getLine()-1]);
                        System.out.println(" ".repeat(positions.get(i).getSymbol()-1) + "^".repeat(lexemes.get(i).length()));
                        System.out.println("Error: invalid name or symbol");
                    }
                }
            }
        }
        Parser parser = new Parser(lexemes);
        parser.start();
    }

    /**
     * converting the code into a sentences
     */
    public void intoSentences(String code) {
        senteces = code.split("\n");
    }

    /**
     * converting the code into a token list
     */
    public void intoLexemes() {
        char[] charArray = code.toCharArray();
        StringBuilder tempLexeme = new StringBuilder();
        boolean comment = false;
        int lineCount = 1;
        int symbolCount = 1;
        for (char c : charArray) {
            if (c == '#') {
                comment = true;
            }
            if (c == '\n' && comment) {
                comment = false;
                continue;
            }

            if (!comment) {
                if (c == ' ') {
                    if (!tempLexeme.toString().equals("")) {
                        lexemes.add(tempLexeme.toString());
                        positions.add(new Position(lineCount, symbolCount - tempLexeme.length()));
                        tempLexeme = new StringBuilder();
                    }
                    symbolCount += 1;
                    continue;
                }
                if (c == '\r') {
                    continue;
                }
                if (Arrays.asList(separator).contains(String.valueOf(c))) {
                    if (!tempLexeme.toString().equals("")) {
                        lexemes.add(tempLexeme.toString());
                        positions.add(new Position(lineCount, symbolCount - tempLexeme.length()));
                        tempLexeme = new StringBuilder();
                    }
                    lexemes.add(String.valueOf(c));
                    positions.add(new Position(lineCount, symbolCount));
                    if (c == '\n') {
                        lineCount += 1;
                        symbolCount = 0;
                    }
                    if (c == '\t') {
                        symbolCount += 4;
                    } else {
                        symbolCount += 1;
                    }
                    continue;
                }
                tempLexeme.append(c);
                if (c == '\n') {
                    lineCount += 1;
                    symbolCount = 0;
                }
                if (c == '\t') {
                    symbolCount += 4;
                } else {
                    symbolCount += 1;
                }
            }
        }
        if (!tempLexeme.isEmpty()) {
            lexemes.add(tempLexeme.toString());
        }
    }

    /**
     * check whether the string is a number
     */
    public boolean isNumber(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * check whether the string is a name
     */
    public boolean isName(String str) {
        return str.matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$");
    }

    /**
     * writing a list of tokens to a file
     */
    public void writeToFile() {
        if (!errors) {
            try (FileWriter file = new FileWriter("TokenList.txt")) {
                for (Token token : tokenList) {
                    file.write(String.format("%-20s <- %-20s %s", token.getValue()
                            .replaceAll("\n", "\\\\n")
                            .replaceAll("\t", "\\\\t"), token.getName(),"on " + token.getPosition()) + "\n");

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}