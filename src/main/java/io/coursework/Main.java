package io.coursework;


import io.coursework.lexer.Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        /*Scanner scanner = new Scanner(System.in);
        System.out.print("Enter .py file name: ");
        String fileName = scanner.nextLine();*/
        String fileName = "prime_numbers.py";
        String source;
        try {
            source = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Lexer lexer = new Lexer(source.replaceAll(" {4}", "\t"));
        lexer.start();
        lexer.writeToFile();
    }
}
