package io.coursework.parser;

import io.coursework.Data;
import io.coursework.lexer.Token;
import io.coursework.parser.expression.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        ArrayList<Variable> variables = new ArrayList<>();
        while (i < sentences.size()) {
            program.addContent(getContents(sentences.get(i), 0, variables));
            i++;
        }
        writeToFile();
    }

    private Contents getContents(ArrayList<Token> sentence, int k, ArrayList<Variable> variables) {
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
            ArrayList<Contents> body = getBody(k, variables);
            c = new Function(sentence.get(k+1).getValue(), args, body);
        } else if (Objects.equals(sentence.get(k).getValue(), "for")) {
            forCheck(sentence, k);

            ArrayList<Contents> body = getBody(k, variables);

            Expression start = new NumberExpression(0);
            Expression end;
            if (Objects.equals(sentence.get(sentence.size() - 4).getName(), "OPERATORS")) {
                end = new OperationExpression(sentence.get(sentence.size() - 4).getValue(),
                        argsCheck(sentence, sentence.size() - 5, variables), argsCheck(sentence, sentence.size() - 3, variables));
            } else {
                end = argsCheck(sentence, sentence.size() - 3, variables);
            }

            if (Objects.equals(sentence.get(k + 6).getValue(), ",")) {
                start = argsCheck(sentence, k + 5, variables);
            } else if (Objects.equals(sentence.get(k + 8).getValue(), ",")) {
                start = new OperationExpression(sentence.get(k + 6).getValue(),
                        argsCheck(sentence, k + 5, variables), argsCheck(sentence, k + 7, variables));
            }
            c = new For(new Variable(sentence.get(k + 1).getValue()), start, end, body);
        } else if (Objects.equals(sentence.get(k).getName(), "IF")) {
            ifCheck(sentence, k);
            ArrayList<Contents> body = getBody(k, variables);
            if (sentence.size() == k+3) {
                BoolExpression bool;
                if (Objects.equals(sentence.get(k + 1).getName(), "BOOLEAN")) {
                    bool = new BoolExpression(Boolean.parseBoolean(sentence.get(k + 1).getValue()));
                } else {
                    if (variableFind(variables, sentence.get(k + 1).getValue()) == null) {
                        errorMassage(sentence.get(k + 1), "Error: unknown variable");
                    }
                    if (!Objects.equals(variableFind(variables, sentence.get(k + 1).getValue()).getType(), "BOOLEAN")) {
                        errorMassage(sentence.get(k + 1), "Error: variable must be a boolean");
                    }
                    BoolExpression b = (BoolExpression) Objects.requireNonNull(variableFind(variables, sentence.get(k + 1).getValue())).getValue();
                    bool = new BoolExpression(Boolean.parseBoolean(String.valueOf(b.getFlag())));
                }
                c = new If(bool, body);
            } else {
                Expression a;
                Expression b;
                String compares;
                if (Objects.equals(sentence.get(sentence.size() - 3).getName(), "OPERATORS")) {
                    b = new OperationExpression(sentence.get(sentence.size() - 3).getValue(),
                            argsCheck(sentence, sentence.size() - 4, variables), argsCheck(sentence, sentence.size() - 2, variables));
                    compares = sentence.get(sentence.size() - 5).getValue();
                } else {
                    b = argsCheck(sentence, sentence.size() - 2, variables);
                    compares = sentence.get(sentence.size() - 3).getValue();
                }


                if (Objects.equals(sentence.get(k+2).getName(), "OPERATORS")) {
                    a = new OperationExpression(sentence.get(k+2).getValue(),
                            argsCheck(sentence, k+1, variables), argsCheck(sentence, k+3, variables));
                } else {
                    a = argsCheck(sentence, k+1, variables);
                }
                c = new If(a, b, compares, body);
            }
        } else if (Objects.equals(sentence.get(k).getName(), "IDENTIFIER")) {
            assignmentCheck(sentence, k);
            if (Objects.equals(sentence.get(k + 2).getName(), "NUMBER")) {
                c = new Assignment(new Variable(sentence.get(k).getValue(),
                        new NumberExpression(Integer.parseInt(sentence.get(k + 2).getValue()))),
                        new NumberExpression(Integer.parseInt(sentence.get(k + 2).getValue())));
                variables.add(new Variable(sentence.get(k).getValue(),
                        new NumberExpression(Integer.parseInt(sentence.get(k + 2).getValue()))));
            } else {
                c = new Assignment(new Variable(sentence.get(k).getValue(),
                        new BoolExpression(Boolean.parseBoolean(sentence.get(k + 2).getValue()))),
                        new BoolExpression(Boolean.parseBoolean(sentence.get(k + 2).getValue())));
                variables.add(new Variable(sentence.get(k).getValue(),
                        new BoolExpression(Boolean.parseBoolean(sentence.get(k + 2).getValue()))));
            }

        } else if (Objects.equals(sentence.get(k).getName(), "RETURN")) {
            returnCheck(sentence, k);
            c = new Return(new Variable(sentence.get(k + 1).getValue()));
        } else {
            errorMassage(sentence.get(k), "SyntaxError: invalid syntax");
        }
        return c;
    }

    private void functionCheck(ArrayList<Token> tokens) {
        positionCheck(tokens, 1, "IDENTIFIER");
        positionCheck(tokens, 2, "OPEN_PARENTHESES");
        positionCheck(tokens, tokens.size() - 1, "COLON");
        positionCheck(tokens, tokens.size() - 2, "CLOSE_PARENTHESES");
        for (int i = 3; i < tokens.size() - 2; i++) {
            if (!Objects.equals(tokens.get(i).getName(), "IDENTIFIER") && i % 2 == 1) {
                errorMassage(tokens.get(i), "SyntaxError: invalid syntax");
            }
            if (!Objects.equals(tokens.get(i).getValue(), ",") && i % 2 == 0) {
                errorMassage(tokens.get(i), "SyntaxError: invalid syntax");
            }
        }
    }

    private void forCheck(ArrayList<Token> tokens, int k) {
        positionCheck(tokens, k + 1, "IDENTIFIER");
        positionCheck(tokens, k + 2, "IN");
        positionCheck(tokens, k + 3, "RANGE");
        positionCheck(tokens, k + 4, "OPEN_PARENTHESES");
        positionCheck(tokens, tokens.size() - 1, "COLON");
        positionCheck(tokens, tokens.size() - 2, "CLOSE_PARENTHESES");
        if (tokens.size() < k + 8) {
            errorMassage(tokens.get(tokens.size() - 3), "Error: empty range");
        }
        boolean comma = false;
        for (int j = 0; j < ((tokens.size() - 2)-(k + 5));j++) {
            if (j % 2 == 0) {
                if (!Objects.equals(tokens.get(k + 5 + j).getName(), "IDENTIFIER") &&
                        !Objects.equals(tokens.get(k + 5 + j).getName(), "NUMBER")) {
                    errorMassage(tokens.get(k + 5 + j), "SyntaxError: invalid syntax");
                }
            } else {
                if (Objects.equals(tokens.get(k + 5 + j).getValue(), ",")) {
                    if (comma) {
                        errorMassage(tokens.get(k + 5 + j), "SyntaxError: invalid syntax");
                    }
                    comma = true;
                }
                if (!Objects.equals(tokens.get(k + 5 + j).getValue(), ",") &&
                        !Arrays.asList(Data.operators).contains(tokens.get(k + 5 + j).getValue())) {
                    errorMassage(tokens.get(k + 5 + j), "SyntaxError: invalid syntax");
                }
            }
        }
    }

    private void ifCheck(ArrayList<Token> tokens, int k) {
        positionCheck(tokens, tokens.size() - 1, "COLON");
        if (tokens.size() < k + 3) {
            errorMassage(tokens.get(tokens.size()-1), "SyntaxError: invalid syntax");
        }
        if (tokens.size() == k + 3) {
            if (!Objects.equals(tokens.get(k + 1).getName(), "IDENTIFIER") &&
                    !Objects.equals(tokens.get(k + 1).getName(), "BOOLEAN")) {
                errorMassage(tokens.get(k + 1), "SyntaxError: invalid syntax");
            }
        } else {
            boolean compare = false;
            for (int j = 0; j < ((tokens.size() - 1)-(k + 1));j++) {
                if (j % 2 == 0) {
                    if (!Objects.equals(tokens.get(k + 1 + j).getName(), "IDENTIFIER") &&
                            !Objects.equals(tokens.get(k + 1 + j).getName(), "NUMBER")) {
                        errorMassage(tokens.get(k + 1 + j), "SyntaxError: invalid syntax");
                    }
                } else {
                    if (Arrays.asList(Data.compares).contains(tokens.get(k + 1 + j).getValue())) {
                        if (compare) {
                            errorMassage(tokens.get(k + 1 + j), "SyntaxError: invalid syntax");
                        }
                        compare = true;
                    }
                    if (!Arrays.asList(Data.compares).contains(tokens.get(k + 1 + j).getValue()) &&
                            !Arrays.asList(Data.operators).contains(tokens.get(k + 1 + j).getValue())) {
                        errorMassage(tokens.get(k + 1 + j), "SyntaxError: invalid syntax");
                    }
                }
            }
        }
    }

    private void assignmentCheck(ArrayList<Token> tokens, int k) {
        if (tokens.size() != k + 3) {
            errorMassage(tokens.get(k + 3), "SyntaxError: invalid syntax");
        }
        if (!Objects.equals(tokens.get(k + 2).getName(), "NUMBER") && !Objects.equals(tokens.get(k + 2).getName(), "BOOLEAN")) {
            errorMassage(tokens.get(k + 2), "SyntaxError: invalid syntax");
        }
    }

    private void returnCheck(ArrayList<Token> tokens, int k) {
        if (tokens.size() != k + 2) {
            errorMassage(tokens.get(k + 2), "SyntaxError: invalid syntax");
        }
        if (!Objects.equals(tokens.get(k + 1).getName(), "IDENTIFIER")) {
            errorMassage(tokens.get(k + 1), "SyntaxError: invalid syntax");
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

    private Variable variableFind(ArrayList<Variable> variables, String name) {
        for (Variable v : variables) {
            if (Objects.equals(v.getName(), name)) {
                return v;
            }
        }
        return null;
    }

    private Expression argsCheck(ArrayList<Token> sentence, int l, ArrayList<Variable> variables) {
        Expression e;
        if (Objects.equals(sentence.get(l).getName(), "NUMBER")) {
            e = new NumberExpression(Integer.parseInt(sentence.get(l).getValue()));
        } else {
            Variable v = variableFind(variables, sentence.get(l).getValue());
            if (v == null) {
                errorMassage(sentence.get(l), "Error: unknown variable");
            }
            e = new VariableExpression(v);
        }
        return e;
    }

    private ArrayList<Contents> getBody(int k, ArrayList<Variable> variables) {
        ArrayList<Contents> content = new ArrayList<>();
        i++;
        ArrayList<Variable> tempVariables = new ArrayList<>(variables);
        try {
            while (Objects.equals(sentences.get(i).get(k).getValue(), "\t")) {
                content.add(getContents(sentences.get(i), k + 1, tempVariables));
                i++;
            }
        } catch (Exception ignored) {
        }
        i -= 1;
        return content;
    }

    private void positionCheck(ArrayList<Token> tokens, int l, String text) {
        if (!Objects.equals(tokens.get(l).getName(), text)) {
            errorMassage(tokens.get(l), "SyntaxError: invalid syntax");
        }
    }

    public void writeToFile() {
        try (FileWriter file = new FileWriter("AST.txt")) {
            file.write(String.valueOf(program));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}