package io.coursework.generator;

import io.coursework.parser.*;
import io.coursework.parser.expression.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class Generator {
    private final Program program;

    StringBuilder asm = new StringBuilder();

    ArrayList<String> variablesName = new ArrayList<>();
    private int loopCount = 0;

    public Generator(Program program) {
        this.program = program;
    }

    public void start() {
        asm.append("""
                .386
                .model flat, stdcall
                option casemap:none
                                
                include     C:\\\\masm32\\include\\windows.inc
                include     C:\\\\masm32\\include\\kernel32.inc
                include     C:\\\\masm32\\include\\user32.inc
                include     C:\\\\masm32\\include\\masm32rt.inc
                includelib  C:\\\\masm32\\lib\\user32.lib
                includelib  C:\\\\masm32\\lib\\kernel32.lib
                                
                """);
        dataCreate();

        asm.append(".code\n");
        functionCreate();
        asm.append("start:\n");
        ArrayList<Contents> contents = new ArrayList<>();
        for (Contents c : program.getContents()) {
            if (c.getClass() != Function.class) {
                contents.add(c);
            }
        }
        bodyCreate(contents);

        asm.append("end start\n");
        createAsmFile();
    }

    private void functionCreate() {
        ArrayList<Contents> contents = program.getContents();
        for (Contents c : contents) {
            if (c.getClass() == Function.class) {
                asm.append(((Function) c).getName()).append(" proc ");
                for (int i = 0; i < ((Function) c).getArgs().size(); i++) {
                    asm.append(((Function) c).getArgs().get(i).getName()).append(":dword");
                    if (i != ((Function) c).getArgs().size() - 1) {
                        asm.append(", ");
                    }
                }
                asm.append("\n");
                variableCreate(((Function) c).getBody());
                bodyCreate(((Function) c).getBody());
                asm.append("\t".repeat(1)).append("ret\n");
                asm.append(((Function) c).getName()).append(" endp\n\n");
            }
        }
    }

    private void dataCreate() {
        ArrayList<Contents> contents = program.getContents();
        ArrayList<Assignment> asg = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for (Contents c : contents) {
            if (c.getClass() == Assignment.class && !names.contains(((Assignment) c).getVariable().getName())) {
                names.add(((Assignment) c).getVariable().getName());
                asg.add(((Assignment) c));
            }
        }
        if (asg.size() != 0) {
            asm.append(".data\n");
        }
        for (Assignment a : asg) {
            asm.append("\t").append(a.getVariable().getName()).append(" dword ?\n");
        }
        asm.append("\n");
    }

    private void variableCreate(ArrayList<Contents> contents) {
        for (Contents c : contents) {
            if (c.getClass() == Assignment.class && !variablesName.contains(((Assignment) c).getVariable().getName())) {
                asm.append("\t").append("local ").append(((Assignment) c).getVariable().getName()).append(": dword\n");
                variablesName.add(((Assignment) c).getVariable().getName());
            } else if (c.getClass() == For.class) {
                asm.append("\t").append("local ").append(((For) c).getI().getName()).append(": dword\n");
                variableCreate(((For) c).getBody());
            } else if (c.getClass() == If.class) {
                variableCreate(((If) c).getBody());
            }
        }
    }

    private void bodyCreate(ArrayList<Contents> contents) {
        for (Contents c : contents) {
            if (c.getClass() == Assignment.class) {
                if (((Assignment) c).getExpression().getClass() != OperationExpression.class) {
                    if (((Assignment) c).getExpression().getClass() == NumberExpression.class) {
                        asm.append("\t".repeat(1)).append("mov ").append(((Assignment) c).getVariable().getName()).append(", ")
                                .append(((NumberExpression) (((Assignment) c).getExpression())).getNum()).append("\n");
                    } else if (((Assignment) c).getExpression().getClass() == BoolExpression.class) {
                        if (((BoolExpression) (((Assignment) c).getExpression())).getFlag()) {
                            asm.append("\t".repeat(1)).append("mov ").append(((Assignment) c).getVariable().getName()).append(", 1\n");
                        } else {
                            asm.append("\t".repeat(1)).append("mov ").append(((Assignment) c).getVariable().getName()).append(", 0\n");
                        }
                    } else {
                        asm.append("\t".repeat(1)).append("mov eax, ").append(((VariableExpression) (((Assignment) c).getExpression())).getVariable().getName()).append("\n");
                        asm.append("\t".repeat(1)).append("mov ").append(((Assignment) c).getVariable().getName()).append(", eax").append("\n");
                    }
                } else {
                    mathOperation(((OperationExpression) ((Assignment) c).getExpression()), ((Assignment) c).getVariable().getName());
                }
            } else if (c.getClass() == Print.class) {
                String temp;
                if (((Print) c).getText() != null) {
                    temp = ((Print) c).getText();
                } else if (((Print) c).getVariable() != null) {
                    temp = ((Print) c).getVariable().getName();
                } else {
                    ArrayList<Expression> args = ((Print) c).getCall().getArgs();
                    for (int i = args.size() - 1; i >= 0; i--) {
                        asm.append("\t".repeat(1)).append("push ").append(args.get(i)).append("\n");
                    }
                    asm.append("\t".repeat(1)).append("call ").append(((Print) c).getCall().getName()).append("\n");
                    temp = "eax";
                }
                asm.append("\t".repeat(1)).append("fn MessageBox, 0, str$(").append(temp).append("),\"КР-02-Java-IO-04-Vodzinskiy\",MB_OK\n");

            } else if (c.getClass() == Return.class) {
                if (((Return) c).getExpression() != null) {
                    asm.append("\t".repeat(1)).append("mov eax, ").append(((Return) c).getExpression().getValue()).append("\n");
                } else {
                    ArrayList<Expression> args = ((Return) c).getCall().getArgs();
                    for (int i = args.size() - 1; i >= 0; i--) {
                        asm.append("\t".repeat(1)).append("push ").append(args.get(i)).append("\n");
                    }
                    asm.append("\t".repeat(1)).append("call ").append(((Return) c).getCall().getName()).append("\n");
                }
            } else if (c.getClass() == Call.class) {
                ArrayList<Expression> args = ((Call) c).getArgs();
                for (int i = args.size() - 1; i >= 0; i--) {
                    asm.append("\t".repeat(1)).append("push ").append(args.get(i)).append("\n");
                }
                asm.append("\t".repeat(1)).append("call ").append(((Call) c).getName()).append("\n");
            } else if (c.getClass() == For.class) {
                String start;
                String end;
                if (((For) c).getStart().getClass() == OperationExpression.class) {
                    mathOperation(((OperationExpression) ((For) c).getStart()), ((OperationExpression) ((For) c).getStart()).getA().getValue());
                    start = ((OperationExpression) ((For) c).getStart()).getA().getValue();
                } else {
                    start = ((For) c).getStart().getValue();
                }
                if (((For) c).getEnd().getClass() == OperationExpression.class) {
                    mathOperation(((OperationExpression) ((For) c).getEnd()), ((OperationExpression) ((For) c).getEnd()).getA().getValue());
                    end = ((OperationExpression) ((For) c).getEnd()).getA().getValue();
                } else {
                    end = ((For) c).getEnd().getValue();
                }
                if (Pattern.matches("[0-9]*", start)) {
                    asm.append("\t".repeat(1)).append(String.format("mov %s, %s\n", ((For) c).getI().getName(), start));
                } else {
                    asm.append("\t".repeat(1)).append("mov eax, ").append(start).append("\n");
                    asm.append("\t".repeat(1)).append("mov ").append(((For) c).getI().getName()).append(", eax").append("\n");
                }

                loopCount++;
                String name = "loop" + loopCount;
                asm.append("\t".repeat(1)).append(name).append(":\n");
                if (!Pattern.matches("[0-9]*", end)) {
                    asm.append("\t".repeat(1)).append("mov ecx, ").append(end).append("\n");
                    asm.append("\t".repeat(1)).append(String.format("cmp %s, ecx\n", ((For) c).getI().getName()));
                } else {
                    asm.append("\t".repeat(1)).append(String.format("cmp %s, %s\n", ((For) c).getI().getName(), end));
                }
                asm.append("\t".repeat(1)).append(String.format("je %s_end\n", name));
                bodyCreate(((For) c).getBody());
                asm.append("\t".repeat(1)).append("inc ").append(((For) c).getI().getName()).append("\n");
                asm.append("\t".repeat(1)).append("jmp ").append(name).append("\n");
                asm.append("\t".repeat(1)).append(name).append("_end:\n");
            } else if (c.getClass() == If.class) {
                if (((If) c).getBool() == null) {
                    String left;
                    String right;
                    if (((If) c).getA().getClass() == OperationExpression.class) {
                        mathOperation(((OperationExpression) ((If) c).getA()), "ecx");
                        left = "ecx";
                    } else {
                        left = ((If) c).getA().getValue();
                    }
                    if (((If) c).getB().getClass() == OperationExpression.class) {
                        mathOperation(((OperationExpression) ((If) c).getB()), "ecx");
                        right = "ecx";
                    } else {
                        right = ((If) c).getB().getValue();
                    }
                    if (!Pattern.matches("[0-9]*", right)) {
                        asm.append("\t".repeat(1)).append("mov ebx, ").append(right).append("\n");
                        asm.append("\t".repeat(1)).append(String.format(".if %s %s %s\n",left, ((If) c).getCompares() , "ebx"));
                    } else {
                        asm.append("\t".repeat(1)).append(String.format(".if %s %s %s\n",left, ((If) c).getCompares() , right));
                    }
                } else {
                    asm.append("\t".repeat(1)).append(String.format(".if %s == 1\n", ((If) c).getBool().getValue()));
                }
                bodyCreate(((If) c).getBody());
                asm.append("\t".repeat(1)).append(".endif\n");
            }
        }
    }

    private void mathOperation(OperationExpression o, String name) {
        switch (o.getOperator()) {
            case ("+") -> operation("add", o, name);
            case ("-") -> operation("sub", o, name);
            case ("*") -> operation("mul", o, name);
            case ("/") -> operation("div", o, name);
            case ("%") -> operation("rem", o, name);
        }
    }

    private void operation(String s, OperationExpression o, String name) {

        boolean f = false;
        try {
            if (Objects.equals(((VariableExpression) o.getA()).getVariable().getName(), name)) {
                f = true;
            }
        } catch (Exception ignored) {
        }
        if (f) {
            if (Objects.equals(s, "mul")) {
                asm.append("\t".repeat(1)).append(String.format("mov eax, %s \n", name));
                asm.append("\t".repeat(1)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(1)).append("mul ebx\n");
                asm.append("\t".repeat(1)).append(String.format("mov %s, eax \n", name));
            } else if (Objects.equals(s, "div")) {
                asm.append("\t".repeat(1)).append(String.format("mov eax, %s \n", name));
                asm.append("\t".repeat(1)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(1)).append("xor edx, edx\n");
                asm.append("\t".repeat(1)).append("div ebx\n");
                asm.append("\t".repeat(1)).append(String.format("mov %s, eax \n", name));
            } else if (Objects.equals(s, "rem")) {
                asm.append("\t".repeat(1)).append(String.format("mov eax, %s \n", name));
                asm.append("\t".repeat(1)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(1)).append("xor edx, edx\n");
                asm.append("\t".repeat(1)).append("div ebx\n");
                asm.append("\t".repeat(1)).append(String.format("mov %s, edx \n", name));
            } else {
                asm.append("\t".repeat(1)).append(String.format("mov eax, %s \n", name));
                asm.append("\t".repeat(1)).append(String.format("%s eax, %s \n", s, o.getB().getValue()));
                asm.append("\t".repeat(1)).append(String.format("mov %s, eax \n", name));
            }
        } else {
            if (Objects.equals(s, "mul")) {
                asm.append("\t".repeat(1)).append(String.format("mov eax, %s \n", o.getA().getValue()));
                asm.append("\t".repeat(1)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(1)).append("mul ebx\n");
                asm.append("\t".repeat(1)).append(String.format("mov %s, eax \n", name));
            } else if (Objects.equals(s, "div")) {
                asm.append("\t".repeat(1)).append(String.format("mov eax, %s \n", o.getA().getValue()));
                asm.append("\t".repeat(1)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(1)).append("xor edx, edx\n");
                asm.append("\t".repeat(1)).append("div ebx\n");
                asm.append("\t".repeat(1)).append(String.format("mov %s, eax \n", name));
            } else if (Objects.equals(s, "rem")) {
                asm.append("\t".repeat(1)).append(String.format("mov eax, %s \n", o.getA().getValue()));
                asm.append("\t".repeat(1)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(1)).append("xor edx, edx\n");
                asm.append("\t".repeat(1)).append("div ebx\n");
                asm.append("\t".repeat(1)).append(String.format("mov %s, edx \n", name));
            } else {
                asm.append("\t".repeat(1)).append(String.format("mov eax, %s \n", o.getA().getValue()));
                asm.append("\t".repeat(1)).append(String.format("%s eax, %s \n", s, o.getB().getValue()));
                asm.append("\t".repeat(1)).append(String.format("mov %s, eax \n", name));
            }
        }
    }

    private void createAsmFile() {
        try (FileWriter file = new FileWriter("КР-02-Java-IO-04-Vodzinskiy.asm")) {
            file.write(String.valueOf(asm));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}