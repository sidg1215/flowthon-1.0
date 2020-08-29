/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author siddharthgupta
 */
public class Parser {//class for the parsing component of the language
    ArrayList<Token> tokens;
    Stack<Token> bracketChecker = new Stack<Token>();
    Token curToken;
    int i;
    HashMap variables = new HashMap();
    
    public Parser(ArrayList<Token> t, HashMap v) throws CloneNotSupportedException{//initializes Pasrser object
        this.tokens = t;
        this.variables = v;
        curToken = t.get(0);
        i = 0; //this is the index of the current token
        for (Token token: tokens){
//            System.out.println(token.getID());
            if (token.kind.equals("OPENPAR")){
                bracketChecker.push(token);
                
            }
            if (token.kind.equals("CLOSEPAR")){
                if (bracketChecker.empty()){
                    System.out.println("ERROR: TOO MANY CLOSING PARANTHESES");
                    System.exit(0);
                }
                bracketChecker.pop();
            }
            
        }
        if (bracketChecker.empty() == false){
                    System.out.println("ERROR: TOO MANY OPENING PARANTHESES");
                    System.exit(0);
                }
        while (i<tokens.size()){
            statement();
            i = i+1;
            if (i == tokens.size()){
                break;
            }
            curToken = tokens.get(i);
        }
    }
    
    
    
    
    
    //GRAMMAR RULES:
    //STATEMENT ----> PRINT(EXPRESSION) | MAKE (VAR)(=)(EXPRESSION) | IF (EXPRESSION) | WHILE (EXPRESSION)
    //EXPRESSION ----> PRODUCT[(+|-|>=|<=|or|and|==)EXPRESSION]
    //PRODUCT ----> (NUMBER|STRING|VARIABLE)[(*|/)][EXPRESSION|PRODUCT]
    
    
    
    
    public void statement() throws CloneNotSupportedException{ //this is the first piece of the grammar that initiates the recursive structure of the context-free grammar commented above
        if (curToken.name.equals("PRINT")){
            nextToken();
            Token output = expression();
            if (output.kind.equals("VAR")){
                if (variables.containsKey(output.name)){
                    while (variables.containsKey(output.name)){
                        output = (Token) variables.get(output.name);                        
                    }
                }else{
                    System.out.println("VARIABLE " + output.name + " DOES NOT EXIST");
                    System.exit(0);
                }
            }
            if (output.kind.equals("ARRAY")){
                if (output.arrayToString.length() == 1){
                    System.out.println("[]");
                }else{
                    System.out.println(output.arrayToString.substring(0, output.arrayToString.length()-1) + "]");
                }
            }
            else if (output.kind.equals("NUMBER")){
                System.out.println(output.value);
            }else{
                System.out.println(output.stringValue);
            }
            
        }
        
        else if (curToken.name.equals("MAKE")){
            nextToken();

            if (curToken.kind.equals("VAR")){
                Token l = curToken;
                String left = l.name;
                nextToken();
                
                
                if (curToken.kind.equals("ASSIGN")){
                    nextToken();
                    Token right = expression();
                    variables.put(left, right);
                }
            }else{
                System.out.println("ERROR: EXPECTING AN IDENTIFIER ON LEFT SIDE");
                System.exit(0);
            }
        }else if (curToken.name.equals("IF")){
            nextToken();
            Token left = expression();
            Token operator = curToken;
            nextToken();
            Token right = expression();            
            Token output = compute(left, operator, right);
            if (output.bool == true){
                if (curToken.kind.equals("OPENBLOCK")){
                    i = i + 2;
                    curToken = tokens.get(i);
                    while (curToken.kind.equals("CLOSEBLOCK")==false){
                        statement();
                        nextToken();
                    }
                }
            }else{
                while (curToken.kind.equals("CLOSEBLOCK") == false){
                        nextToken();
                    }
                nextToken();
                statement();
            }
        }else if (curToken.name.equals("WHILE")){
            nextToken();
            
            int leftStart = i;
            Token left = expression();
            int leftEnd = i;
            
            Token operator = curToken;
            
            nextToken();
            
            int rightStart = i;
            Token right = expression();
            int rightEnd = i+1;
            
            ArrayList<Token> leftTokens = new ArrayList(tokens.subList(leftStart, leftEnd));
            leftTokens.add(new Token(";", "EOL"));
            ArrayList<Token> rightTokens = new ArrayList(tokens.subList(rightStart, rightEnd));
            rightTokens.add(new Token(";", "EOL"));

            Token output = compute(left, operator, right);
            if (output.bool == true){
                if (curToken.kind.equals("OPENBLOCK")){
                    i = i + 2;
                    curToken = tokens.get(i);
                    int startIndex = i;
                    int endIndex;
                    while (output.bool == true){
                        while (curToken.kind.equals("CLOSEBLOCK") == false){
                            statement();
                            nextToken();
                        }
                        ArrayList<Token> allTokens = this.tokens;
                        int curIndex = i;

                        this.tokens = leftTokens;
                        i = 0;
                        curToken = tokens.get(i);
                        left = expression();
                        
                        this.tokens = rightTokens;
                        i = 0;
                        curToken = tokens.get(i);
                        right = expression();
                        
                        this.tokens = allTokens;
                        endIndex = curIndex;
                        i = startIndex;
                        curToken = tokens.get(i);
                        
                        output = compute(left, operator, right);
                        if (output.bool == false){
                            i = endIndex;
                        }
                    }
                }
            }else{
                while (curToken.kind.equals("CLOSEBLOCK") == false){
                        nextToken();
                    }
                nextToken();
                statement();
            }
        }else if (curToken.kind.equals("VAR")){
            if (variables.containsKey(curToken.name)){
                Token l = curToken;
                Token data = null;
                String left = curToken.name;
                nextToken();
                
                if (curToken.kind.equals("ASSIGN")){
                    nextToken();
                    Token right = expression();
                    variables.replace(left, right);
                    l = (Token) variables.get(left);
                }else if (curToken.kind.equals("CALLING")){
                    l = (Token) variables.get(left);
                    nextToken();
                    if (curToken.kind.equals("METHOD")){
                        if (curToken.name.equals("add")){
                            nextToken();
                            if (curToken.kind.equals("OPENPAR")){
                                nextToken();
                                if (curToken.kind.equals("NUMBER") || curToken.kind.equals("STRING") || curToken.kind.equals("VAR")){
                                    if (curToken.kind.equals("VAR")){
                                        curToken = (Token) variables.get(curToken.name);
                                    }
                                    data = curToken;
                                    l.add(data);
                                    nextToken();
                                    if (curToken.kind.equals("CLOSEPAR")){
                                        nextToken();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Token expression() throws CloneNotSupportedException{//this is the second piece of the grammar
        Token left = product();
        while ((curToken.name.equals("+")||curToken.name.equals("-"))){
                Token operator = new Token("+", "OPERATOR");
                Token right = product();
                left = compute(left, operator, right);
            }
        return left;
        
    }
    
        
    public Token product() throws CloneNotSupportedException{//this is the last and final piece of the grammar, and serves as the building block of the whole language
        boolean changeSign = this.checkForNegative();
        Token left = (Token) curToken.clone();
        if (left.kind.equals("VAR")){
            left = ((Token) variables.get(left.name));
        }
        
        if (left.kind.equals("OPENPAR")){
            nextToken();
            left = expression();
        }
        if (left.kind.equals("OPENBRACKET")){//this block of code detects whether or not the user passed in an array
            Token array = new Token("ARRAY");
            nextToken();
            if (curToken.kind.equals("DELIMETER")){
                System.out.println("error: expecting object before and after delimeter");
                System.exit(0);
            }
            while(curToken.kind.equals("CLOSEBRACKET") == false){
                Token element = expression();
                if (curToken.kind.equals("CLOSEBRACKET")){
                    array.add(element);
                    break;
                }
                if (curToken.kind.equals("DELIMETER") == false){
                    System.out.println("error: expecting delimeter after token");
                    System.exit(0);
                }else{
                    array.add(element);
                }
                nextToken();
                        
            }
            left = array;
            
        }
        if (changeSign){
            left.changeSign();
        }

        nextToken();
        if (curToken.name.equals("+")||curToken.name.equals("-")){
            return left;
        }
        while (curToken.name.equals("*")||curToken.name.equals("/")){
            if (left.kind.equals("STRING")){
                System.out.println("error: can't multiply or divide with strings");
                System.exit(0);
            }
            Token operator = curToken;
            nextToken();
            changeSign = this.checkForNegative();
            Token right = (Token) curToken.clone();
            if (right.kind.equals("OPENPAR")){
                nextToken();
                right = expression();
            }
            if (changeSign){
                right.changeSign();
            }
            left = compute(left, operator, right);
            nextToken();
        }
        return left;

}
    
    
    public Token compute(Token left, Token operator, Token right){//this is a helper function that facilitates in executing operations between two products
        Token result = null;
        if (right == null){

            System.out.println("ERROR: VALUE EXPECTED AFTER OPERATOR");
            System.exit(0);
        }
//        System.out.println("-----------");
//        System.out.println("Left: " + left.name + " Right: " + right.name);
//        System.out.println("Left: " + left.value);
//        System.out.println("Operator: " + operator.name);
//        System.out.println("Right: " + right.value);
//        System.out.println("-----------");
        
        if (right.kind.equals("CLOSEPAR")){
            System.out.println("ERROR: VALUE EXPECTED AFTER OPERATOR");
            System.exit(0);
        }
        if (operator.name.equals("==")){
            if (left.value == right.value){
                result = new Token(true);
                return result;
            }else{
                result = new Token(false);
                return result;
            }
        }
        if (operator.name.equals("!=")){
            if (left.value != right.value){
                result = new Token(true);
                return result;
            }else{
                result = new Token(false);
                return result;
            }
        }
        if (operator.name.equals("=>")){
            if (left.value >= right.value){
                result = new Token(true);
                return result;
            }else{
                result = new Token(false);
                return result;
            }
        }
        if (operator.name.equals("=<")){
            if (left.value <= right.value){
                result = new Token(true);
                return result;
            }else{
                result = new Token(false);
                return result;
            }
        }
        if (operator.name.equals("+")){
            if ((left.stringValue != null) && (right.stringValue != null)){
                String output = left.stringValue + right.stringValue;
                result = new Token(output, "STRING");
                return result;
            }
            if ((left.kind.equals("ARRAY")) && right.kind.equals("ARRAY")){
                result = new Token("ARRAY");
                result.array.addAll(left.array);
                result.array.addAll(right.array);
                System.out.println(result.array);
                String newString = "[";
                int k = 0;
                
                for (Token token: result.array){
                    k = k + 1;
                    if (token.kind.equals("STRING")){
                        if (k == result.array.size()){
                            newString = newString + token.name;
                            break;
                        }
                        newString = newString + token.name + ",";
                    }else if (token.kind.equals("NUMBER")){
                        if (k == result.array.size()){
                            newString = newString + token.name;
                            break;
                        }
                        newString = newString + token.value + ",";
                    }
                }
                newString = newString + "]";
                result.arrayToString = newString;
                return result;
            }
            if ((left.kind.equals("NUMBER")) && (right.kind.equals("NUMBER"))){
                long output = left.value + right.value;
                result = new Token(output, "NUMBER");
                return result;
            }else{
                System.out.println("ERROR: TYPES ARE NOT THE SAME");
                System.exit(0);
            }
        }
        if (operator.name.equals("-")){
            if ((left.stringValue != null) || (right.stringValue != null)){
                System.out.println("ERROR: CANNOT SUBTRACT WITH STRINGS");
                System.exit(0);
            }
            if ((left.kind.equals("NUMBER")||left.kind.equals("VAR")) && (right.kind.equals("NUMBER")||right.kind.equals("VAR"))){
                long output = left.value + right.value;
                result = new Token(output, "NUMBER");
                return result;
            }else{
                System.out.println("ERROR: TYPES ARE NOT THE SAME");
                System.exit(0);
            }
        }
        if (operator.name.equals("*")){
            if ((left.kind.equals("NUMBER")||left.kind.equals("VAR")) && (right.kind.equals("NUMBER")||right.kind.equals("VAR"))){
                long output = left.value * right.value;
                result = new Token(output, "NUMBER");
                return result;
            }else{
                System.out.println("ERROR: TYPES ARE NOT THE SAME");
                System.exit(0);
            }
        }
        if (operator.name.equals("/")){
            if ((left.kind.equals("NUMBER")||left.kind.equals("VAR")) && (right.kind.equals("NUMBER")||right.kind.equals("VAR"))){
                long output = left.value/right.value;
                result = new Token(output, "NUMBER");
                return result;
            }else{
                System.out.println("ERROR: TYPES ARE NOT THE SAME");
                System.exit(0);
            }
        }
        return null;
    }
    public HashMap getVariables(){//returns an updated hash table of the variables in the current session
        return variables;
    }

    private boolean checkForNegative() {//this function helps handle negative integers
        int initialNeg = 0;

        while (curToken.name.equals("+")||curToken.name.equals("-")){
            if (curToken.name.equals("-")){
                initialNeg += 1;
            }
            i = i + 1;
            curToken = tokens.get(i);
        }
        if (initialNeg%2 == 0){
                return false;
            }
        return true;
    }
    private void nextToken(){//calling this function gets the next token from the list tokens
        i = i+1;
        curToken = tokens.get(i);
    }
        
        
    }
    
