package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

enum States {
    START,
    SYM_CROSS,
    SYM_DASH,
    SYM_ASTER,
    SYM_SLASH,
    SYM_CARET,
    SYM_GT,
    SYM_LT,
    ESCAPE,
    NUMLITERAL, BIN, OCT, HEX,
    STRLIT,
    IDENT,
    // reserved words
    RES_C, RES_D, RES_F, RES_I, RES_M, RES_P, RES_R, RES_T, RES_U, RES_W,
    RELLOGIC, // relational and logical
}

public class HandLexer {
    int numErrs = 0;
    String errorsStr = "";

    // err handling
    private void error(int line, String message) {
        String errString = "[line " + line + "] Error: " + message;
        System.out.println(errString);
        errorsStr += errString + "\n";
        numErrs++;
        System.exit(1);
        //return errString;
    }

    // the scanning part
    private String codeString = "";
    private String output = "";
    private String neatOutput = "";
    private String subst = "";
    // private String tempStr = "";
    private int line = 1;
    private int curr;
    private States currState = States.START;

    private char advance() { // advance to the next character
        return codeString.charAt(curr++);
    }

    private char backtrack() {
        return codeString.charAt(--curr);
    }

    private boolean endOfCode() {
        return curr >= codeString.length();
    }

    public void Analyze(File in) throws IOException {

        // store source code
        FileInputStream fis = new FileInputStream(in);
        Scanner sc = new Scanner(fis);

        while (sc.hasNextLine()) {
            codeString += sc.nextLine() + "\n";
        }
        sc.close();
        // System.out.println(codeString);

        // setting up the tokenslist and tokenSet
        TokenList tokenList = new TokenList();
        TokenSet tokenSet = new TokenSet();

        // getting the tokens :3
        char c;
        while (!endOfCode()) {
            c = advance();
            // System.out.println("at position " + curr + " on line " + line + " is the
            // character " + c);
            switch (currState) {
                case START:
                    switch (c) {
                        // single character cases
                        case ',':
                            // create new token; add to list
                            tokenList.addToken(TokenType.COMMA, String.valueOf(c), line);
                            // token name -> output
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            break;
                        case ';':
                            tokenList.addToken(TokenType.SEMICOLON, String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            break;
                        case '(':
                            tokenList.addToken(TokenType.OPENPAR, String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            break;
                        case ')':
                            tokenList.addToken(TokenType.CLOSEPAR, String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            break;
                        case '{':
                            tokenList.addToken(TokenType.OPENBR, String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            break;
                        case '}':
                            tokenList.addToken(TokenType.CLOSEBR, String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            break;
                        case '!':
                            tokenList.addToken(TokenType.INVERT, String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            break;

                        case '=':
                            tokenList.addToken(TokenType.ASSIGN, String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            break;

                        // newlines
                        case '\n':
                            line++;
                            neatOutput += "\n";
                            break;

                        // spaces
                        case '\t':
                            neatOutput += "\t";
                            break;
                        case ' ':
                            break;

                        // double character cases
                        case '+':
                            currState = States.SYM_CROSS;
                            break;

                        case '-':
                            currState = States.SYM_DASH;
                            break;

                        case '*':
                            currState = States.SYM_ASTER;
                            break;

                        case '/':
                            currState = States.SYM_SLASH;
                            break;

                        case '^':
                            currState = States.SYM_CARET;
                            break;

                        case '>':
                            currState = States.SYM_GT;
                            break;

                        case '<':
                            currState = States.SYM_LT;
                            break;

                        // logical/relational operators (excluding invert "!")
                        case '.':
                            currState = States.RELLOGIC;
                            subst = "";
                            subst += String.valueOf(c);
                            break;

                        // numerical literals
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            currState = States.NUMLITERAL;
                            subst = "";
                            subst += String.valueOf(c);
                            break;

                        default:
                            System.out.println("UNKNOWN SYMBOL \"" + c + "\" at: Line: " + line + ", Column: " + curr);
                            System.exit(0);
                            break;

                        // string literal
                        case '"':
                            currState = States.STRLIT;
                            subst = "";
                            break;

                        // identifiers and keywords
                        // regular identifiers
                        case '_':
                        case 'a':
                        case 'b':
                        case 'e':
                        case 'g':
                        case 'h':
                        case 'j':
                        case 'k':
                        case 'l':
                        case 'n':
                        case 'o':
                        case 'q':
                        case 's':
                        case 'v':
                        case 'x':
                        case 'y':
                        case 'z':
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'S':
                        case 'T':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.IDENT;
                            break;

                        // possible keywords
                        case 'c':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_C;
                            break;
                        case 'd':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_D;
                            break;
                        case 'f':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_F;
                            break;
                        case 'i':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_I;
                            break;
                        case 'm':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_M;
                            break;
                        case 'p':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_P;
                            break;
                        case 'r':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_R;
                            break;
                        case 't':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_T;
                            break;
                        case 'u':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_U;
                            break;
                        case 'w':
                            subst = "";
                            subst += String.valueOf(c);
                            currState = States.RES_W;
                            break;

                    } // end char switch START
                    break;

                // double character cases
                case SYM_CROSS:
                    switch (c) {
                        case '=':
                            tokenList.addToken(TokenType.ADDASSIGN, "+" + String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                        default:
                            c = backtrack();
                            tokenList.addToken(TokenType.ADD, String.valueOf(codeString.charAt(curr - 1)), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                    } // end char switch SYM_CROSS
                    break;

                case SYM_DASH:
                    switch (c) {
                        case '=':
                            tokenList.addToken(TokenType.SUBASSIGN, "-" + String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                        case '>':
                            tokenList.addToken(TokenType.RIGHTARROW, "-" + String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                        default:
                            c = backtrack();
                            tokenList.addToken(TokenType.SUB, String.valueOf(codeString.charAt(curr - 1)), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                    } // end char switch SYM_DASH
                    break;

                case SYM_ASTER:
                    switch (c) {
                        case '=':
                            tokenList.addToken(TokenType.MULASSIGN, "*" + String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                        default:
                            c = backtrack();
                            tokenList.addToken(TokenType.MUL, String.valueOf(codeString.charAt(curr - 1)), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                    } // end char switch SYM_ASTER
                    break;

                case SYM_SLASH:
                    switch (c) {
                        case '=':
                            tokenList.addToken(TokenType.DIVASSIGN, "/" + String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                        case '/': // inline comment found
                            // purge until newline found
                            while (c != '\n') {
                                c = advance();
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        case '*': // multiline comment found
                            // purge until end of comment found
                            while (!(c == '*' && advance() == '/')) {
                                c = advance();
                                if (c == '\n') {
                                    line++;
                                }
                            }
                            currState = States.START;
                            break;

                        default:
                            c = backtrack();
                            tokenList.addToken(TokenType.DIV, String.valueOf(codeString.charAt(curr - 1)), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                    } // end char switch SYM_SLASH
                    break;

                case SYM_CARET:
                    switch (c) {
                        case '=':
                            tokenList.addToken(TokenType.EXPASSIGN, "^" + String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                        default:
                            c = backtrack();
                            tokenList.addToken(TokenType.EXP, String.valueOf(codeString.charAt(curr - 1)), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                    } // end char switch SYM_CARET
                    break;

                case SYM_GT:
                    switch (c) {
                        case '=':
                            tokenList.addToken(TokenType.GTE, ">" + String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                        default:
                            c = backtrack();
                            tokenList.addToken(TokenType.GT, String.valueOf(codeString.charAt(curr - 1)), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                    } // end char switch SYM_GT
                    break;

                case SYM_LT:
                    switch (c) {
                        case '=':
                            tokenList.addToken(TokenType.LTE, "<" + String.valueOf(c), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                        default:
                            c = backtrack();
                            tokenList.addToken(TokenType.LT, String.valueOf(codeString.charAt(curr - 1)), line);
                            output += tokenList.getLatestToken().getType().name() + "\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.START;
                            break;
                    } // end char switch SYM_LT
                    break;
                // relational/logical operators
                case RELLOGIC:
                    subst += String.valueOf(c);
                    if (c == '.') { // end of rellogics operator found
                        switch (subst) {
                            case ".is.":
                                tokenList.addToken(TokenType.IS, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                break;
                            case ".not.":
                                tokenList.addToken(TokenType.NOT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                break;
                            case ".and.":
                                tokenList.addToken(TokenType.AND, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                break;
                            case ".or.":
                                tokenList.addToken(TokenType.OR, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                break;

                            default:
                                error(line, subst + " is not a valid relational/logical operator!");
                                neatOutput += "RELOGIC_ERR ";
                                break;
                        }
                        subst = "";
                        currState = States.START;
                    } // end if . found

                    if (c == '\n') { // newline
                        error(line, "relational/logical operator closure not found!");
                        neatOutput += "RELOGIC_CLOSURE_ERR ";
                        line++;
                        subst = "";
                        currState = States.START;
                    }
                    break;

                // numeric literals
                case NUMLITERAL:
                    if (c >= '0' && c <= '9') {
                        subst += String.valueOf(c);
                    }
                    switch (c) {
                        case 'b': // binary
                            subst += String.valueOf(c);
                            currState = States.BIN;
                            break;
                        case 'c': // octal
                            subst += String.valueOf(c);
                            currState = States.OCT;
                            break;
                        case 'x': // hexadecimal
                            subst += String.valueOf(c);
                            currState = States.HEX;
                            break;

                        // integer
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            break;

                        // symbols
                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                            tokenList.addToken(TokenType.NUMLIT, subst, line);
                            output += tokenList.getLatestToken().getType().name() + "("
                                    + tokenList.getLatestToken().getValue() + ")\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + "("
                                    + tokenList.getLatestToken().getValue() + ") ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        case '.':
                            // purge
                            subst += String.valueOf(c);
                            c = advance();
                            while (c >= '0' && c <= '9') {
                                subst += String.valueOf(c);
                                c = advance();
                            }
                            if (c == 'i' || c == 'n' || c == 'a' || c == 'o') { // check if relogic operator
                                tokenList.addToken(TokenType.NUMLIT, subst, line);
                                tokenSet.addToken(TokenType.NUMLIT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                                c = backtrack();
                                c = backtrack();
                                subst = "";
                                subst += String.valueOf(c);
                                c = advance();
                                currState = States.RELLOGIC;
                                break;
                            }
                            error(line, "\"" + subst + "\" decimals/floating points not supported!");
                            output += "ERROR_line-" + line + "_NUMLIT\n";
                            neatOutput += "NUMLIT_FLOAT_ERR ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            if (c == '\n' || c == ' ' || c == '\t' || c == '\r') {
                                tokenList.addToken(TokenType.NUMLIT, subst, line);
                                tokenSet.addToken(TokenType.NUMLIT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                subst += String.valueOf(c);
                                while (c != '\n' && c != ' ' && c != '\t' && c != '\r') {
                                    c = advance();
                                    if (c != '\n' && c != ' ' && c != '\t' && c != '\r') {
                                        subst += String.valueOf(c);
                                    }
                                }
                                error(line, subst + " invalid number literal!");
                                neatOutput += "NUMLIT_ERR ";
                                currState = States.START;
                            }
                            break;
                    }
                    break;

                case BIN:
                    if (c == '0' || c == '1') {
                        subst += String.valueOf(c);
                    }
                    switch (c) {
                        case '0':
                        case '1':
                            break;

                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            // purge
                            subst += String.valueOf(c);
                            c = advance();
                            while (c >= '0' && c <= '9') {
                                subst += String.valueOf(c);
                                c = advance();
                            }
                            error(line, "\"" + subst + "\" not a valid binary value!");
                            output += "ERROR_line-" + line + "_NUMLIT\n";
                            neatOutput += "NUMLIT_BIN_ERR ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        // symbols
                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                            tokenList.addToken(TokenType.NUMLIT, subst, line);
                            output += tokenList.getLatestToken().getType().name() + "("
                                    + tokenList.getLatestToken().getValue() + ")\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + "("
                                    + tokenList.getLatestToken().getValue() + ") ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        case '.':
                            subst += String.valueOf(c);
                            // purge
                            c = advance();
                            while (c >= '0' && c <= '9') {
                                subst += String.valueOf(c);
                                c = advance();
                            }
                            if (c == 'i' || c == 'n' || c == 'a' || c == 'o') { // check if relogic operator
                                tokenList.addToken(TokenType.NUMLIT, subst, line);
                                tokenSet.addToken(TokenType.NUMLIT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                c = backtrack();
                                c = backtrack();
                                subst = "";
                                subst += String.valueOf(c);
                                c = advance();
                                currState = States.RELLOGIC;
                                break;
                            }
                            error(line, "\"" + subst + "\" decimals/floating points not supported!");
                            output += "ERROR_line-" + line + "_NUMLIT\n";
                            neatOutput += "NUMLIT_FLOAT_ERR ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            if (subst.equals("0b")) { // check if a value was recorded
                                error(line, subst + " invalid binary literal!");
                                neatOutput += "NUMLIT_BIN_ERR ";
                                currState = States.START;
                                break;
                            }
                            if (c == '\n' || c == ' ' || c == '\t' || c == '\r') {
                                tokenList.addToken(TokenType.NUMLIT, subst, line);
                                tokenSet.addToken(TokenType.NUMLIT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                subst += String.valueOf(c);
                                while (!(c == '\n' || c == ' ' || c == '\t' || c == '\r')) {
                                    c = advance();
                                    if (c != '\n' && c != ' ' && c != '\t' && c != '\r') {
                                        subst += String.valueOf(c);
                                    }
                                }
                                error(line, subst + " invalid binary literal!");
                                neatOutput += "NUMLIT_BIN_ERR";
                                currState = States.START;
                            }
                            break;
                    }
                    break;

                case OCT:
                    if (c >= '0' && c <= '7') {
                        subst += String.valueOf(c);
                    }
                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                            break;

                        case '8':
                        case '9':
                            // purge
                            subst += String.valueOf(c);
                            c = advance();
                            while (c >= '0' && c <= '9') {
                                subst += String.valueOf(c);
                                c = advance();
                            }
                            error(line, "\"" + subst + "\" not a valid octal value!");
                            output += "ERROR_line-" + line + "_NUMLIT\n";
                            neatOutput += "NUMLIT_OCT_ERR ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        // symbols
                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                            tokenList.addToken(TokenType.NUMLIT, subst, line);
                            output += tokenList.getLatestToken().getType().name() + "("
                                    + tokenList.getLatestToken().getValue() + ")\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + "("
                                    + tokenList.getLatestToken().getValue() + ") ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        case '.':
                            subst += String.valueOf(c);
                            // purge
                            c = advance();
                            while (c >= '0' && c <= '9') {
                                subst += String.valueOf(c);
                                c = advance();
                            }
                            if (c == 'i' || c == 'n' || c == 'a' || c == 'o') { // check if relogic operator
                                tokenList.addToken(TokenType.NUMLIT, subst, line);
                                tokenSet.addToken(TokenType.NUMLIT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                                c = backtrack();
                                c = backtrack();
                                subst = "";
                                subst += String.valueOf(c);
                                c = advance();
                                currState = States.RELLOGIC;
                                break;
                            }
                            error(line, "\"" + subst + "\" decimals/floating points not supported!");
                            output += "ERROR_line-" + line + "_NUMLIT\n";
                            neatOutput += "NUMLIT_FLOAT_ERR ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            if (subst.equals("0c")) { // check if a value was recorded
                                error(line, subst + " invalid octal literal!");
                                neatOutput += "NUMLIT_OCT_ERR ";
                                currState = States.START;
                                break;
                            }
                            if (c == '\n' || c == ' ' || c == '\t' || c == '\r') {
                                tokenList.addToken(TokenType.NUMLIT, subst, line);
                                tokenSet.addToken(TokenType.NUMLIT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                subst += String.valueOf(c);
                                while (!(c == '\n' || c == ' ' || c == '\t' || c == '\r')) {
                                    c = advance();
                                    if (c != '\n' && c != ' ' && c != '\t' && c != '\r') {
                                        subst += String.valueOf(c);
                                    }
                                }
                                error(line, subst + " invalid octal literal!");
                                neatOutput += "NUMLIT_OCT_ERR ";
                                currState = States.START;
                            }
                            break;
                    }
                    break;

                case HEX:
                    if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
                        subst += String.valueOf(c);
                    }
                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            break;

                        // symbols
                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                            tokenList.addToken(TokenType.NUMLIT, subst, line);
                            output += tokenList.getLatestToken().getType().name() + "("
                                    + tokenList.getLatestToken().getValue() + ")\n";
                            neatOutput += tokenList.getLatestToken().getType().name() + "("
                                    + tokenList.getLatestToken().getValue() + ") ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        case '.':
                            subst += String.valueOf(c);
                            // purge
                            c = advance();
                            while ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
                                subst += String.valueOf(c);
                                c = advance();
                            }
                            if (c == 'i' || c == 'n' || c == 'a' || c == 'o') { // check if relogic operator
                                tokenList.addToken(TokenType.NUMLIT, subst, line);
                                tokenSet.addToken(TokenType.NUMLIT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                                c = backtrack();
                                c = backtrack();
                                subst = "";
                                subst += String.valueOf(c);
                                c = advance();
                                currState = States.RELLOGIC;
                                break;
                            }
                            subst = subst.toUpperCase();
                            error(line, "\"" + subst + "\" decimals/floating points not supported!");
                            output += "ERROR_line-" + line + "_NUMLIT\n";
                            neatOutput += "NUMLIT_FLOAT_ERR ";
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            if (subst.equals("0x")) { // check if a value was recorded
                                error(line, subst + " invalid hexadecimal literal!");
                                neatOutput += "NUMLIT_HEX_ERR ";
                                currState = States.START;
                                break;
                            }
                            if (c == '\n' || c == ' ' || c == '\t' || c == '\r') {
                                subst = subst.toUpperCase();
                                tokenList.addToken(TokenType.NUMLIT, subst, line);
                                tokenSet.addToken(TokenType.NUMLIT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                subst += String.valueOf(c);
                                while (!(c == '\n' || c == ' ' || c == '\t' || c == '\r')) {
                                    c = advance();
                                    if (c != '\n' && c != ' ' && c != '\t' && c != '\r') {
                                        subst += String.valueOf(c);
                                    }
                                }
                                error(line, subst + " invalid hexadecimal literal!");
                                neatOutput += "NUMLIT_HEX_ERR ";
                                currState = States.START;
                            }
                            break;
                    }
                    break;

                // string literals
                case STRLIT:
                    if (c != '\\' && c != '\"') {
                        subst += String.valueOf(c);
                    }

                    if (c == '\\') { // escape sequence found
                        // tempStr = "";
                        currState = States.ESCAPE;
                        break;
                    }

                    if (c == '\"') {
                        tokenList.addToken(TokenType.STRLIT, subst, line);
                        tokenSet.addToken(TokenType.STRLIT, subst, line);

                        output += tokenList.getLatestToken().getType().name() + "("
                                + tokenList.getLatestToken().sanitizedValue() + ")\n";
                        neatOutput += tokenList.getLatestToken().getType().name() + "("
                                + tokenList.getLatestToken().sanitizedValue() + ") ";

                        // To get unsanitized output, uncomment this
                        // output += tokenList.getLatestToken().getType().name() + "("
                        // + tokenList.getLatestToken().getValue() + ")\n";
                        currState = States.START;
                        break;
                    }

                    if (endOfCode()) {
                        error(line, "Unterminated string literal!");
                        neatOutput += "STRLIT_CLOSURE_ERR ";
                    }
                    break;
                case ESCAPE:
                    switch (c) { // java handles unescaping
                        case 'n':
                            // tokenList.addToken(TokenType.NEWLINE, "\\n", line);
                            subst += "\n";
                            // output += tokenList.getLatestToken().getType().name() + "\n";
                            // neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.STRLIT;
                            break;

                        case 't':
                            // tokenList.addToken(TokenType.HORZTAB, "\\t", line);
                            subst += "\t";
                            // output += tokenList.getLatestToken().getType().name() + "\n";
                            // neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.STRLIT;
                            break;

                        case 'r':
                            // tokenList.addToken(TokenType.CARGRET, "\\r", line);
                            subst += "\r";
                            // output += tokenList.getLatestToken().getType().name() + "\n";
                            // neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.STRLIT;
                            break;

                        case 'b':
                            // tokenList.addToken(TokenType.BACKSPC, "\\t", line);
                            subst += "\b";
                            // output += tokenList.getLatestToken().getType().name() + "\n";
                            // neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.STRLIT;
                            break;

                        case '\\':
                            // tokenList.addToken(TokenType.BACKSLSH, "\\t", line);
                            subst += "\\";
                            // output += tokenList.getLatestToken().getType().name() + "\n";
                            // neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.STRLIT;
                            break;

                        case '\'':
                            // tokenList.addToken(TokenType.SINGQUOT, "\'", line);
                            subst += "\'";
                            // output += tokenList.getLatestToken().getType().name() + "\n";
                            // neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.STRLIT;
                            break;

                        case '\"':
                            // tokenList.addToken(TokenType.DOUBQUOT, "\"", line);
                            subst += "\"";
                            // output += tokenList.getLatestToken().getType().name() + "\n";
                            // neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.STRLIT;
                            break;

                        case 'f':
                            // tokenList.addToken(TokenType.FORMFEED, "\\f", line);
                            subst += "\f";
                            // output += tokenList.getLatestToken().getType().name() + "\n";
                            // neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            currState = States.STRLIT;
                            break;
                    }
                    break;

                // identifiers and keywords
                case IDENT:
                    if (c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                        subst += String.valueOf(c);
                    } else {
                        tokenList.addToken(TokenType.IDENT, subst, line);
                        tokenSet.addToken(TokenType.IDENT, subst, line);
                        output += tokenList.getLatestToken().getType().name() + "("
                                + tokenList.getLatestToken().getValue() + ")\n";
                        neatOutput += tokenList.getLatestToken().getType().name() + "("
                                + tokenList.getLatestToken().getValue() + ") ";
                        c = backtrack();
                        currState = States.START;
                    }
                    break;

                case RES_C:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("catalyze")) {
                                tokenList.addToken(TokenType.CATALYZE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("catalyze")) {
                                tokenList.addToken(TokenType.CATALYZE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_D:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("decompose")) {
                                tokenList.addToken(TokenType.DECOMPOSE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else if (subst.equals("distill")) {
                                tokenList.addToken(TokenType.DISTILL, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("decompose")) {
                                tokenList.addToken(TokenType.DECOMPOSE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else if (subst.equals("distill")) {
                                tokenList.addToken(TokenType.DISTILL, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_F:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("funnel")) {
                                tokenList.addToken(TokenType.FUNNEL, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else if (subst.equals("filter")) {
                                tokenList.addToken(TokenType.FILTER, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else if (subst.equals("ferment")) {
                                tokenList.addToken(TokenType.FERMENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else if (subst.equals("false")) {
                                tokenList.addToken(TokenType.FALSE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("funnel")) {
                                tokenList.addToken(TokenType.FUNNEL, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else if (subst.equals("filter")) {
                                tokenList.addToken(TokenType.FILTER, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else if (subst.equals("ferment")) {
                                tokenList.addToken(TokenType.FERMENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else if (subst.equals("false")) {
                                tokenList.addToken(TokenType.FALSE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_I:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("inert")) {
                                tokenList.addToken(TokenType.INERT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else if (subst.equals("input")) {
                                tokenList.addToken(TokenType.INPUT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("inert")) {
                                tokenList.addToken(TokenType.INERT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else if (subst.equals("input")) {
                                tokenList.addToken(TokenType.INPUT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_M:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("mole32")) {
                                tokenList.addToken(TokenType.MOLE32, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else if (subst.equals("mole64")) {
                                tokenList.addToken(TokenType.MOLE64, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("mole32")) {
                                tokenList.addToken(TokenType.MOLE32, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else if (subst.equals("mole64")) {
                                tokenList.addToken(TokenType.INPUT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.MOLE64, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_P:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("print")) {
                                tokenList.addToken(TokenType.PRINT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else if (subst.equals("println")) {
                                tokenList.addToken(TokenType.PRINTLN, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else if (subst.equals("printerr")) {
                                tokenList.addToken(TokenType.PRINTERR, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("print")) {
                                tokenList.addToken(TokenType.PRINT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else if (subst.equals("println")) {
                                tokenList.addToken(TokenType.PRINTLN, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else if (subst.equals("printerr")) {
                                tokenList.addToken(TokenType.PRINTERR, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_R:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("reactive")) {
                                tokenList.addToken(TokenType.REACTIVE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("reactive")) {
                                tokenList.addToken(TokenType.REACTIVE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_T:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("true")) {
                                tokenList.addToken(TokenType.TRUE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("true")) {
                                tokenList.addToken(TokenType.TRUE, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_U:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("until")) {
                                tokenList.addToken(TokenType.UNTIL, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name();
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("until")) {
                                tokenList.addToken(TokenType.UNTIL, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name();
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

                case RES_W:
                    switch (c) {
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                            if (subst.equals("when")) {
                                tokenList.addToken(TokenType.WHEN, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                                c = backtrack();
                                currState = States.START;
                            } else {
                                c = backtrack();
                                currState = States.IDENT;
                            }
                            break;

                        case ',':
                        case ';':
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case '!':
                        case '=':
                        case '+':
                        case '-':
                        case '*':
                        case '/':
                        case '^':
                        case '>':
                        case '<':
                        case '.':
                            if (subst.equals("when")) {
                                tokenList.addToken(TokenType.WHEN, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + " ";
                            } else {
                                tokenList.addToken(TokenType.IDENT, subst, line);
                                output += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ")\n";
                                neatOutput += tokenList.getLatestToken().getType().name() + "("
                                        + tokenList.getLatestToken().getValue() + ") ";
                            }
                            c = backtrack();
                            currState = States.START;
                            break;

                        default:
                            subst += String.valueOf(c);
                            break;
                    }
                    break;

            } // end case state switch
        } // end scanner while
          // end of code found!
        tokenList.addToken(TokenType.EOF, null, line);
        output += tokenList.getLatestToken().getType().name();
        neatOutput += tokenList.getLatestToken().getType().name();

        System.out.println("Note all values are sanitized (escape sequences are printed as is)");
        System.out.println("TokenList:");
        tokenList.printTokens();
        System.out.println();

        System.out.println("Identifiers / Numerical Literals:");
        tokenSet.printTokens();
        System.out.println();

        System.out.println("Output:");
        output += "\n\n" + numErrs + " ERRORS FOUND!\n" + errorsStr;
        System.out.println(output);

        System.out.println("Neat Output:");
        neatOutput += "\n\n" + numErrs + " ERRORS FOUND!\n" + errorsStr;
        System.out.println(neatOutput);

        // write output file
        try {
            // writing to output file
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // write neatoutput file
        try {
            // writing to output file
            BufferedWriter writer = new BufferedWriter(new FileWriter("neatoutput.txt"));
            writer.write(neatOutput);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }



}
