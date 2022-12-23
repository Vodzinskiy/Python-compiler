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
                                
                include     \\masm32\\include\\masm32rt.inc
                                
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
        asm.append("\tinvoke ExitProcess, 0\n");
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
                asm.append("\tret\n");
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
                asm.append("\tlocal ").append(((Assignment) c).getVariable().getName()).append(": dword\n");
                variablesName.add(((Assignment) c).getVariable().getName());
            } else if (c.getClass() == For.class) {
                asm.append("\tlocal ").append(((For) c).getI().getName()).append(": dword\n");
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
                        asm.append("\tmov ").append(((Assignment) c).getVariable().getName()).append(", ")
                                .append(((NumberExpression) (((Assignment) c).getExpression())).getNum()).append("\n");
                    } else if (((Assignment) c).getExpression().getClass() == BoolExpression.class) {
                        if (((BoolExpression) (((Assignment) c).getExpression())).getFlag()) {
                            asm.append("\tmov ").append(((Assignment) c).getVariable().getName()).append(", 1\n");
                        } else {
                            asm.append("\tmov ").append(((Assignment) c).getVariable().getName()).append(", 0\n");
                        }
                    } else {
                        asm.append("\tmov eax, ").append(((VariableExpression) (((Assignment) c).getExpression())).getVariable().getName()).append("\n");
                        asm.append("\tmov ").append(((Assignment) c).getVariable().getName()).append(", eax").append("\n");
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
                        asm.append("\tpush ").append(args.get(i)).append("\n");
                    }
                    asm.append("\tcall ").append(((Print) c).getCall().getName()).append("\n");
                    temp = "eax";
                }
                asm.append("\tfn MessageBox, 0, str$(").append(temp).append("),\"КР-02-Java-IO-04-Vodzinskiy\",MB_OK\n");

            } else if (c.getClass() == Return.class) {
                if (((Return) c).getExpression() != null) {
                    asm.append("\tmov eax, ").append(((Return) c).getExpression().getValue()).append("\n");
                } else {
                    ArrayList<Expression> args = ((Return) c).getCall().getArgs();
                    for (int i = args.size() - 1; i >= 0; i--) {
                        asm.append("\tpush ").append(args.get(i)).append("\n");
                    }
                    asm.append("\tcall ").append(((Return) c).getCall().getName()).append("\n");
                }
            } else if (c.getClass() == Call.class) {
                ArrayList<Expression> args = ((Call) c).getArgs();
                for (int i = args.size() - 1; i >= 0; i--) {
                    asm.append("\tpush ").append(args.get(i)).append("\n");
                }
                asm.append("\tcall ").append(((Call) c).getName()).append("\n");
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
                    asm.append(String.format("\tmov %s, %s\n", ((For) c).getI().getName(), start));
                } else {
                    asm.append("\tmov eax, ").append(start).append("\n");
                    asm.append("\tmov ").append(((For) c).getI().getName()).append(", eax").append("\n");
                }

                loopCount++;
                String name = "loop" + loopCount;
                asm.append("\t").append(name).append(":\n");
                if (!Pattern.matches("[0-9]*", end)) {
                    asm.append("\tmov ecx, ").append(end).append("\n");
                    asm.append(String.format("\tcmp %s, ecx\n", ((For) c).getI().getName()));
                } else {
                    asm.append(String.format("\tcmp %s, %s\n", ((For) c).getI().getName(), end));
                }
                asm.append(String.format("\tje %s_end\n", name));
                bodyCreate(((For) c).getBody());
                asm.append("\tinc ").append(((For) c).getI().getName()).append("\n");
                asm.append("\tjmp ").append(name).append("\n");
                asm.append("\t").append(name).append("_end:\n");
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
                        asm.append("\tmov ebx, ").append(right).append("\n");
                        asm.append(String.format("\t.if %s %s %s\n",left, ((If) c).getCompares() , "ebx"));
                    } else {
                        asm.append(String.format("\t.if %s %s %s\n",left, ((If) c).getCompares() , right));
                    }
                } else {
                    asm.append(String.format("\t.if %s == 1\n", ((If) c).getBool().getValue()));
                }
                bodyCreate(((If) c).getBody());
                asm.append("\t.endif\n");
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
                asm.append(String.format("\tmov eax, %s \n", name));
                asm.append(String.format("\tmov ebx, %s \n", o.getB().getValue()));
                asm.append("\tmul ebx\n");
                asm.append(String.format("\tmov %s, eax \n", name));
            } else if (Objects.equals(s, "div")) {
                asm.append(String.format("\tmov eax, %s \n", name));
                asm.append(String.format("\tmov ebx, %s \n", o.getB().getValue()));
                asm.append("\txor edx, edx\n");
                asm.append("\tdiv ebx\n");
                asm.append(String.format("\tmov %s, eax \n", name));
            } else if (Objects.equals(s, "rem")) {
                asm.append(String.format("\tmov eax, %s \n", name));
                asm.append(String.format("\tmov ebx, %s \n", o.getB().getValue()));
                asm.append("\txor edx, edx\n");
                asm.append("\tdiv ebx\n");
                asm.append(String.format("\tmov %s, edx \n", name));
            } else {
                asm.append(String.format("\tmov eax, %s \n", name));
                asm.append(String.format("\t%s eax, %s \n", s, o.getB().getValue()));
                asm.append(String.format("\tmov %s, eax \n", name));
            }
        } else {
            if (Objects.equals(s, "mul")) {
                asm.append(String.format("\tmov eax, %s \n", o.getA().getValue()));
                asm.append(String.format("\tmov ebx, %s \n", o.getB().getValue()));
                asm.append("\tmul ebx\n");
                asm.append(String.format("\tmov %s, eax \n", name));
            } else if (Objects.equals(s, "div")) {
                asm.append(String.format("\tmov eax, %s \n", o.getA().getValue()));
                asm.append(String.format("\tmov ebx, %s \n", o.getB().getValue()));
                asm.append("\txor edx, edx\n");
                asm.append("\tdiv ebx\n");
                asm.append(String.format("\tmov %s, eax \n", name));
            } else if (Objects.equals(s, "rem")) {
                asm.append(String.format("\tmov eax, %s \n", o.getA().getValue()));
                asm.append(String.format("\tmov ebx, %s \n", o.getB().getValue()));
                asm.append("\txor edx, edx\n");
                asm.append("\tdiv ebx\n");
                asm.append(String.format("\tmov %s, edx \n", name));
            } else {
                asm.append(String.format("\tmov eax, %s \n", o.getA().getValue()));
                asm.append(String.format("\t%s eax, %s \n", s, o.getB().getValue()));
                asm.append(String.format("\tmov %s, eax \n", name));
            }
        }
    }

    private void createAsmFile() {
        try (FileWriter file = new FileWriter("КР-02-Java-IO-04-Vodzinskiy.asm")) {
            file.write(String.valueOf(asm));
            System.out.println("\nSuccess, target code saved in КР-02-Java-IO-04-Vodzinskiy.asm\n");
            System.out.println("Generated code:\n" + asm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}