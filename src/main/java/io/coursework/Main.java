package io.coursework;


import io.coursework.lexer.Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        start();
    }

    public static void start() {
        Scanner scanner = new Scanner(System.in);
        String source = null;
        while (source == null) {
            System.out.print("Enter .py file name or path: ");
            String fileName = scanner.nextLine();
            try {
                source = new String(Files.readAllBytes(Paths.get(fileName)));
            } catch (IOException e) {
                System.out.println("No such file found");
            }
        }
        Lexer lexer = new Lexer(source.replaceAll(" {4}", "\t"));
        lexer.start();
    }
}
