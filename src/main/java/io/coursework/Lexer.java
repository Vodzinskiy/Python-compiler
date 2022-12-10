package io.coursework;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Lexer {
    private final String code;
    private final ArrayList<Token> tokenList = new ArrayList<>();
    private final ArrayList<String> lexemes = new ArrayList<>();


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
        for (String lexeme : lexemes) {
            switch (lexeme) {
                case "def" -> tokenList.add(new Token("FUNCTION", lexeme));
                case "(" -> tokenList.add(new Token("OPEN_PARENTHESES", lexeme));
                case ")" -> tokenList.add(new Token("CLOSE_PARENTHESES", lexeme));
                case ":" -> tokenList.add(new Token("COLON", lexeme));
                case "," -> tokenList.add(new Token("COMMA", lexeme));
                case "in" -> tokenList.add(new Token("IN", lexeme));
                case "range" -> tokenList.add(new Token("RANGE", lexeme));
                case "if" -> tokenList.add(new Token("IF", lexeme));
                case "return" -> tokenList.add(new Token("RETURN", lexeme));
                case "\t" -> tokenList.add(new Token("TAB", lexeme));
                case "\n" -> tokenList.add(new Token("NEW_LINE", lexeme));
                default -> {
                    if (Arrays.asList(assignement).contains(lexeme)) {
                        tokenList.add(new Token("ASSIGNMENT", lexeme));
                    } else if (Arrays.asList(cycleoperators).contains(lexeme)) {
                        tokenList.add(new Token("CYCLE", lexeme));
                    } else if (Arrays.asList(bool).contains(lexeme)) {
                        tokenList.add(new Token("BOOLEAN", lexeme));
                    } else if (Arrays.asList(operators).contains(lexeme)) {
                        tokenList.add(new Token("OPERATORS", lexeme));
                    } else if (Arrays.asList(compares).contains(lexeme)) {
                        tokenList.add(new Token("COMPARATORS", lexeme));
                    } else if (Arrays.asList(logical).contains(lexeme)) {
                        tokenList.add(new Token("LOGIC", lexeme));
                    } else if (isNumber(lexeme)) {
                        tokenList.add(new Token("NUMBER", lexeme));
                    } else if (isName(lexeme)) {
                        tokenList.add(new Token("IDENTIFIER", lexeme));
                    } else {
                        System.out.println("Error -> " + lexeme + ";");
                    }
                }
            }
        }
    }

    /**
     * converting the code into a token list
     */
    public void intoLexemes() {
        char[] charArray = code.toCharArray();
        StringBuilder tempLexeme = new StringBuilder();

        for (char ch : charArray) {
            if (ch == ' ') {
                if (!tempLexeme.toString().equals("")) {
                    lexemes.add(tempLexeme.toString());
                    tempLexeme = new StringBuilder();
                }
                continue;
            } else if (ch == '\r') {
                continue;
            } else if (Arrays.asList(separator).contains(String.valueOf(ch))) {
                if (!tempLexeme.toString().equals("")) {
                    lexemes.add(tempLexeme.toString());
                    tempLexeme = new StringBuilder();
                }
                lexemes.add(String.valueOf(ch));
                continue;
            }
            tempLexeme.append(ch);
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
        try (FileWriter file = new FileWriter("TokenList.txt")) {
            for (Token token : tokenList) {
                file.write(token.getName() + " => " + token.getValue()
                        .replaceAll("\n", "\\\\n")
                        .replaceAll("\t", "\\\\t") + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}