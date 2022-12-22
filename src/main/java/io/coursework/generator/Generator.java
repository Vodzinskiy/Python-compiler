package io.coursework.generator;

import io.coursework.parser.*;
import io.coursework.parser.expression.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Generator {
    private final Program program;

    StringBuilder asm = new StringBuilder();

    ArrayList<String> variablesName = new ArrayList<>();

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
        bodyCreate(contents, 1);

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
                bodyCreate(((Function) c).getBody(), 1);
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
            }
        }
    }

    private void bodyCreate(ArrayList<Contents> contents, int k) {
        for (Contents c : contents) {
            if (c.getClass() == Assignment.class) {
                if (((Assignment) c).getExpression().getClass() != OperationExpression.class) {
                    if (((Assignment) c).getExpression().getClass() == NumberExpression.class) {
                        asm.append("\t".repeat(k)).append("mov ").append(((Assignment) c).getVariable().getName()).append(", ")
                                .append(((NumberExpression) (((Assignment) c).getExpression())).getNum()).append("\n");
                    } else if (((Assignment) c).getExpression().getClass() == BoolExpression.class) {
                        asm.append("\t".repeat(k)).append("mov ").append(((Assignment) c).getVariable().getName()).append(", \"")
                                .append(((BoolExpression) (((Assignment) c).getExpression())).getFlag()).append("\"\n");
                    } else {
                        asm.append("\t".repeat(k)).append("mov ").append(((Assignment) c).getVariable().getName()).append(", ")
                                .append(((VariableExpression) (((Assignment) c).getExpression())).getVariable().getName()).append("\n");
                    }
                } else {
                    switch (((OperationExpression) ((Assignment) c).getExpression()).getOperator()){
                        case ("+") -> operation("add", ((OperationExpression) ((Assignment) c).getExpression()), ((Assignment) c).getVariable().getName(), k);
                        case ("-") -> operation("sub", ((OperationExpression) ((Assignment) c).getExpression()), ((Assignment) c).getVariable().getName(), k);
                        case ("*") -> operation("mul", ((OperationExpression) ((Assignment) c).getExpression()), ((Assignment) c).getVariable().getName(), k);
                        case ("/") -> operation("div", ((OperationExpression) ((Assignment) c).getExpression()), ((Assignment) c).getVariable().getName(), k);
                        case ("%") -> operation("rem", ((OperationExpression) ((Assignment) c).getExpression()), ((Assignment) c).getVariable().getName(), k);
                    }
                }
            } else if (c.getClass() == Print.class) {
                String temp = "";
                if (((Print) c).getText() != null) {
                    temp = ((Print) c).getText();
                } else if (((Print) c).getVariable() != null) {
                    temp = ((Print) c).getVariable().getName();
                } else {
                    ArrayList<Expression> args = ((Print) c).getCall().getArgs();
                    for (int i = args.size()-1; i >= 0; i--) {
                        asm.append("\t".repeat(k)).append("push ").append(args.get(i)).append("\n");
                    }
                    asm.append("\t".repeat(k)).append("call ").append(((Print) c).getCall().getName()).append("\n");
                    temp = "eax";
                }
                asm.append("\t".repeat(k)).append("fn MessageBox, 0, str$(").append(temp).append("),\"КР-02-Java-IO-04-Vodzinskiy\",MB_OK\n");

            } else if (c.getClass() == Return.class) {
                if (((Return) c).getExpression() != null) {
                    asm.append("\t".repeat(k)).append("mov eax, ").append(((Return) c).getExpression().getValue()).append("\n");
                } else {
                    ArrayList<Expression> args = ((Return) c).getCall().getArgs();
                    for (int i = args.size()-1; i >= 0; i--) {
                        asm.append("\t".repeat(k)).append("push ").append(args.get(i)).append("\n");
                    }
                    asm.append("\t".repeat(k)).append("call ").append(((Return) c).getCall().getName()).append("\n");
                }
            } else if (c.getClass() == Call.class) {
                ArrayList<Expression> args = ((Call) c).getArgs();
                for (int i = args.size()-1; i >= 0; i--) {
                    asm.append("\t".repeat(k)).append("push ").append(args.get(i)).append("\n");
                }
                asm.append("\t".repeat(k)).append("call ").append(((Call) c).getName()).append("\n");
            }
        }
    }

    private void operation(String s, OperationExpression o, String name, int k) {

        boolean f = false;
        try {
            if (Objects.equals(((VariableExpression) o.getA()).getVariable().getName(), name)) {
                f = true;
            }
        } catch (Exception ignored) {
        }
        if (f) {
            if (Objects.equals(s, "mul")) {
                asm.append("\t".repeat(k)).append(String.format("mov eax, %s \n", name));
                asm.append("\t".repeat(k)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(k)).append("mul ebx\n");
                asm.append("\t".repeat(k)).append(String.format("mov %s, eax \n", name));
            } else if (Objects.equals(s, "div")) {
                asm.append("\t".repeat(k)).append(String.format("mov eax, %s \n", name));
                asm.append("\t".repeat(k)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(k)).append("xor edx, edx\n");
                asm.append("\t".repeat(k)).append("div ebx\n");
                asm.append("\t".repeat(k)).append(String.format("mov %s, eax \n", name));
            } else if (Objects.equals(s, "rem")) {
                asm.append("\t".repeat(k)).append(String.format("mov eax, %s \n", name));
                asm.append("\t".repeat(k)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(k)).append("xor edx, edx\n");
                asm.append("\t".repeat(k)).append("div ebx\n");
                asm.append("\t".repeat(k)).append(String.format("mov %s, edx \n", name));
            } else {
                asm.append("\t".repeat(k)).append(String.format("%s %s, %s \n", s, name, o.getB().getValue()));
            }
        } else {
            if (Objects.equals(s, "mul")) {
                asm.append("\t".repeat(k)).append(String.format("mov eax, %s \n", o.getA().getValue()));
                asm.append("\t".repeat(k)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(k)).append("mul ebx\n");
                asm.append("\t".repeat(k)).append(String.format("mov %s, eax \n", name));
            } else if (Objects.equals(s, "div")) {
                asm.append("\t".repeat(k)).append(String.format("mov eax, %s \n", o.getA().getValue()));
                asm.append("\t".repeat(k)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(k)).append("xor edx, edx\n");
                asm.append("\t".repeat(k)).append("div ebx\n");
                asm.append("\t".repeat(k)).append(String.format("mov %s, eax \n", name));
            } else if (Objects.equals(s, "rem")) {
                asm.append("\t".repeat(k)).append(String.format("mov eax, %s \n", o.getA().getValue()));
                asm.append("\t".repeat(k)).append(String.format("mov ebx, %s \n", o.getB().getValue()));
                asm.append("\t".repeat(k)).append("xor edx, edx\n");
                asm.append("\t".repeat(k)).append("div ebx\n");
                asm.append("\t".repeat(k)).append(String.format("mov %s, edx \n", name));
            } else {
                asm.append("\t".repeat(k)).append(String.format("mov eax, %s \n", o.getA().getValue()));
                asm.append("\t".repeat(k)).append(String.format("%s eax, %s \n", s, o.getB().getValue()));
                asm.append("\t".repeat(k)).append(String.format("mov %s, eax \n", name));
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