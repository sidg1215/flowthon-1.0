/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
        if (curToken.name.equals("PRINT")){//checks if the current statement is a PRINT statement
            nextToken();//goes to next token
            Token output = expression();//stores output to be printed in Token output
            if (output.kind.equals("ARRAY")){//if the token stored in output is an array
                if (output.arrayToString.length() == 1){//if the length of output is 1
                    System.out.println("[]");
                }else{//print the entire array
                    System.out.println(output.arrayToString.substring(0, output.arrayToString.length()-1) + "]");
                }
            }
            else if (output.kind.equals("NUMBER")){//if the token stored in output is a number
                System.out.println(output.value);
            }else{
                System.out.println(output.stringValue);
            }
            
        }
        
        else if (curToken.name.equals("MAKE")){//checks if the current statement is a MAKE statement
            nextToken();//goes to the next token

            if (curToken.kind.equals("VAR")){//if the current token is of the type variable
                Token l = curToken;//sets the left side of the assignment 
                String left = l.name;//stores the name of the left side of the assignment
                nextToken();//goes to the next token
                
                
                if (curToken.kind.equals("ASSIGN")){//checks to see if the current token is an equal sign
                    nextToken();//goes to the next token
                    Token right = expression();//calculates the right side of the assignment
                    variables.put(left, right);//stores the rightside of the assignment in the variables hash table
                }else{
                    System.out.println("error: expecting an equal sign");
                    System.exit(0);
                }
            }else{
                System.out.println("ERROR: EXPECTING AN IDENTIFIER ON LEFT SIDE");
                System.exit(0);
            }
        }else if (curToken.name.equals("IF")){//checks if the current statement is an IF statement
            nextToken();//goes to the next token
            Token left = expression();//calculates the left side of the boolean expression and stores it
            Token operator = curToken;//operator is set as one of the boolean operators that is used in the if statement
            nextToken();//goes to the next token
            Token right = expression();//calculates the right side of the boolean expression and stores it           
            Token output = compute(left, operator, right);//calculates whether the boolean expression is true or false and stores it in the variable output
            if (output.bool == true){//if the expression is true
                if (curToken.kind.equals("OPENBLOCK")){//check if the current token is a curly bracket, indicating that the 
                    i = i + 2;//skips the "EOL" token on the end of the current line which has the OPENBLOCK token and goes to the next line
                    curToken = tokens.get(i);
                    while (curToken.kind.equals("CLOSEBLOCK")==false){//while the current token is not a CLOSEBLOCK token...
                        statement();//execute a statement
                        nextToken();//go to the next token after the end of the statement
                    }
                }
            }else{//if the boolean expression is not true...
                while (curToken.kind.equals("CLOSEBLOCK") == false){//while the current token is not a curly closing bracket...
                    nextToken();//go to the next token
                }
                if (curToken.name.equals("ELSE")){//if there is an else statement
                    nextToken();
                    System.out.println(curToken.getID());
                    if (curToken.kind.equals("OPENBLOCK")){//check for an open curly bracket
                        System.out.println("hkjlasdflhjk");
                        i = i + 2;
                        curToken = tokens.get(i);
                        while (curToken.kind.equals("CLOSEBLOCK")==false){
                            statement();
                            nextToken();
                        }
                    }
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
                        if (curToken.name.equals("add") && l.kind.equals("ARRAY")){
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
                        }else if (curToken.name.equals("intersect") && l.kind.equals("ARRAY")){
                            Set<String> intersection = new HashSet<String>();
                            nextToken();
                            if (curToken.kind.equals("OPENPAR")){
                                nextToken();
                                Token parameter = expression();
                                if (parameter.kind.equals("ARRAY")){
                                    Set<String> a = new HashSet<String>();
                                    for (Token token : l.array){
                                        a.add(token.name);
                                    }
                                    for (Token token : parameter.array){
                                        if (a.contains(token.name)){
                                            intersection.add(token.name);
                                        }
                                    }
                                    System.out.println(intersection);
                                    nextToken();
                                    if (curToken.kind.equals("CLOSEPAR")){
                                        nextToken();
                                    }
                                }
                            }
                        }
                        else{
                            System.out.println("error: cannot add elements to " + left + ", " + left + " not an array");
                            System.exit(0);
                        }
                    }
                }else if (curToken.kind.equals("OPENBRACKET")){
                    l = (Token) variables.get(left);
                    if (l.kind.equals("ARRAY")){
                        nextToken();
                        if (curToken.kind.equals("NUMBER") || curToken.kind.equals("VAR")){
                            if (curToken.kind.equals("VAR")){
                                l = (Token) variables.get(left);
                            }
                            int index = (int) curToken.value;
                            if (index >= l.array.size()){
                                System.out.println("error: array index out of bounds");
                                System.exit(0);
                            }
                            Token element = l.array.get(index);
                            System.out.println(element.name);
                        }else{
                            System.out.println("error: expecitng integer as a parameter");
                            System.exit(0);
                        }
                        nextToken();
                        if (curToken.kind.equals("CLOSEBRACKET")){
                            nextToken();
                        }else{
                            System.out.println("error: expecting closing parantheses");
                            System.exit(0);
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
            if (variables.containsKey(left.name)){
                left = ((Token) variables.get(left.name));
            }else{
                System.out.println("variable " + left.name + " does not exist");
                System.exit(0);
            }
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
    
