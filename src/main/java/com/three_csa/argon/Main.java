package com.three_csa.argon;

import com.three_csa.argon.SemAnalyzer.SyntaxTree;
import com.three_csa.argon.SyntaxAnalyzer.ParseTree;
import com.three_csa.argon.SyntaxAnalyzer.Parser;
import com.three_csa.argon.Tokenizer.Lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        File in = null;
        if(args.length > 0){
            in = new File(args[0]);
        }
        if(!in.exists()){
            System.out.println("Input file not found");
            System.exit(1);
        }
        ArrayList<String> inputList = new ArrayList<>();
        Lexer hl = new Lexer();
        hl.Analyze(in);
        try (BufferedReader br = new BufferedReader(new FileReader("output.argonobj"))) {
            String line;
            while ((line = br.readLine()) != null) { // Read each line from the file into a String
                inputList.add(line.trim());

            }
        } catch (IOException e) {
            System.err.println("Failed: output.argonobj not found");
            return;
        }
        Parser parser = new Parser(inputList);
        ParseTree parseTree = parser.parse();
        parseTree.print();
        SyntaxTree syntaxTree = new SyntaxTree();
        syntaxTree.buildAST(parseTree);
        Interpreter i = new Interpreter(syntaxTree.getVariables());
        i.interpret(syntaxTree.getRoot());
        System.out.println("\n\nBuild complete.");
    }
}