package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> inputList = new ArrayList<>();
        //program();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/rules.txt"))) {
            String line;
            while ((line = br.readLine()) != null) { // Read each line from the file into a String
                if (line.startsWith("STRLIT")) { //check for STRLIT
                    inputList.add("STRLIT");
                    continue;
                }
                if (line.startsWith("IDENT")) { //check for IDENT
                    inputList.add("IDENT");
                    continue;
                } else if (line.startsWith("ERROR")){ //check for ERROR
                    System.out.println("Error from the Scanning phase detected.");
                    System.out.println("Terminating.");
                    System.exit(0);
                }
                if (line.startsWith("EOF")) { //check for EOF
                    inputList.add("EOF");
                    break;
                }
                inputList.add(line); //normal token
            }
        } catch (IOException e) {
            System.err.println("Failed: File named rules.txt not found");
            return;
        }
        Parser parser = new Parser(inputList);
        //parser.program();
    }
}