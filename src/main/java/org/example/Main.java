package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        ArrayList<String> inputList = new ArrayList<>();
        //program();
        HandLexer hl = new HandLexer();
        hl.Analyze(new File("input.txt"));
        try (BufferedReader br = new BufferedReader(new FileReader("output.txt"))) {
            String line;
            while ((line = br.readLine()) != null) { // Read each line from the file into a String
                inputList.add(line.trim());

            }
        } catch (IOException e) {
            System.err.println("Failed: output.txt not found");
            return;
        }
        Parser parser = new Parser(inputList);
        ParseTree parseTree = parser.parse();
        parseTree.print();
        SyntaxTree syntaxTree = new SyntaxTree();
        syntaxTree.buildAST(parseTree);
        Interpreter i = new Interpreter();
        i.interpret(syntaxTree.getRoot());

        System.out.println("\n\nBuild complete.");
    }
}