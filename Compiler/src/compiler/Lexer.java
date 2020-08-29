/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 *
 * @author siddharthgupta
 */
public class Lexer {

    String input;
    ArrayList<Token> tokens;
    HashMap variables = new HashMap();
    String curString = "";
    Token token;

    
    public Lexer(String line, HashMap v) throws IOException{
        this.variables = v;
        this.tokens = new ArrayList<Token>();
        boolean quoteOpen = false;
        
        this.tokens = new ArrayList<Token>();
        this.input = line;
        System.out.println(line);
        //System.out.println(this.input);



        for (int i = 0; i < input.length(); i++){
            if (input.charAt(i) == ' '||input.charAt(i) == '\t'){
                if (Token.isKeyword(curString.toUpperCase())){
                    token = new Token(curString.toUpperCase(), "KEYWORD");
                    curString = "";
                    tokens.add(token);
                }else if (quoteOpen == false && curString.length() > 0){
                    this.makeVariable();
                }else if (quoteOpen){
                    curString = curString + " ";
                }
            }else if (input.charAt(i) == '['){
                token = new Token("[", "OPENBRACKET");
                tokens.add(token);    
            }else if (input.charAt(i) == ']'){
                if (curString.length()>0){
                    makeVariable();
                }
                token = new Token("]", "CLOSEBRACKET");
                tokens.add(token);
            }else if (input.charAt(i) == ',' && quoteOpen == false){
                token = new Token(",", "DELIMETER");
                tokens.add(token);
            }
            else if (input.charAt(i) == ';'){
                String s=String.valueOf(input.charAt(i));
                if (curString.length()>0){
                    makeVariable();
                }
                token = new Token(s, "EOL");
                tokens.add(token);
            }
            else if (input.charAt(i) == '+' || input.charAt(i) == '-'|| input.charAt(i) == '*'|| input.charAt(i) == '/'){
                if (curString.length()>0){
                    this.makeVariable();
                }
                String s=String.valueOf(input.charAt(i));  
                token = new Token(s, "OPERATOR");
                tokens.add(token);

            }else if (input.charAt(i) == '.'){
                if (curString.length()>0){
                    this.makeVariable();
                }
                token = new Token(".", "CALLING");
                tokens.add(token);
                i = i+1;
                String method = "";
                while (Character.isLetter(input.charAt(i))){
                    method += input.charAt(i);
                    i = i+1;
                }
                i = i-1;
                token = new Token(method, "METHOD");
                tokens.add(token);
            }
            else if (input.charAt(i) == '\"'){

                if (quoteOpen == true){
                    token = new Token(curString, "STRING");
                    tokens.add(token);
                    curString = "";
                    quoteOpen = false;
                }
                else{
                    quoteOpen = true;
                }

            }else if (input.charAt(i) == '{'){
                if (curString.length()>0){
                    makeVariable();
                }
                token = new Token("{", "OPENBLOCK");
                tokens.add(token);
            }else if (input.charAt(i) == '}'){
                if (curString.length()>0){
                    makeVariable();
                }
                token = new Token("}", "CLOSEBLOCK");
                tokens.add(token);
            }
            else if (input.charAt(i) == '('){
                if (Token.isKeyword(curString.toUpperCase())){
                    token = new Token(curString.toUpperCase(), "KEYWORD");
                    curString = "";
                    tokens.add(token);
                }
                if (curString.length() > 0){
                    this.makeVariable();
                }
                token = new Token("(", "OPENPAR");
                tokens.add(token);
            }
            else if (input.charAt(i) == ')'){
                if (Token.isKeyword(curString.toUpperCase())){
                    token = new Token(curString.toUpperCase(), "KEYWORD");
                    curString = "";
                    tokens.add(token);
                }else if (curString.length() > 0){
                    if (input.charAt(i-1) == '\"'){
                        token = new Token(curString, "STRING");
                        curString = "";
                        tokens.add(token);

                    }else if (quoteOpen == false){
                        this.makeVariable();
                }else{
                        System.out.println("ERROR EXPECTING A QUOTATION MARK FOR A STRING");
                    }
                }
                token = new Token(")", "CLOSEPAR");
                tokens.add(token);
            }else if (input.charAt(i) == '!'){
                if (input.charAt(i+1) == '='){
                    token = new Token("!=", "OPERATOR");
                    tokens.add(token);
                    i = i+1;
                }
            }
            else if (input.charAt(i) == '='){
                if (input.charAt(i+1) == '='){
                    token = new Token("==", "OPERATOR");
                    tokens.add(token);
                    i = i+1;
                }
                else if (input.charAt(i+1) == '>'){
                    token = new Token("=>", "OPERATOR");
                    tokens.add(token);
                    i = i+1;
                }
                else if (input.charAt(i+1) == '<'){
                    token = new Token("=<", "OPERATOR");
                    tokens.add(token);
                    i = i+1;
                }
                else{
                    if (curString.length() > 0){
                        token = new Token(curString,"VAR");
                        tokens.add(token);
                    }
                    token = new Token("=", "ASSIGN");
                    tokens.add(token);
                    curString = "";
                }

            }else if (input.charAt(i) == '>'){
                token = new Token(">", "OPERATOR");
                tokens.add(token);
                curString = "";
            }else if (input.charAt(i) == '<'){
                token = new Token("<", "OPERATOR");
                tokens.add(token);
                curString = "";
            }
            else if (Character.isDigit(input.charAt(i))){
                //System.out.println(input.charAt(i));
                int k = i+1;
                while (Character.isDigit(input.charAt(k))){
                    k = k+1;
                }
                curString = input.substring(i, k);
                long number = Integer.parseInt(curString);
                token = new Token(number, "NUMBER");
                curString = "";
                tokens.add(token);

                i = k-1;

                }

            else{
                curString = curString + input.charAt(i);
            }
            }
        //DEBUG TOOL
//        for (Token token: tokens){
//            System.out.println(token.getID());
//        }

    }
    
    public ArrayList<Token> getTokens(){
        return this.tokens;
        }
    
    public void makeVariable(){
        token = new Token(curString, "VAR");
        tokens.add(token);
        curString = "";
    }
    
}
