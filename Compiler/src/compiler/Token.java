/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

import java.util.ArrayList;

/**
 *
 * @author siddharthgupta
 */
public class Token implements Cloneable{
    String name;
    String kind;
    String stringValue = null;
    long value;
    ArrayList<Token> array;
    String arrayToString;
    boolean bool;
    boolean changedSign = false;
    
    public Token(String n, String k){
        if (k.equals("VAR")){
            this.name = n;
            this.kind = k;
        }else{
            this.name = n;
            this.kind = k;
            this.stringValue = n;
        }
    }
    public Token(boolean bool){
        this.bool = bool;
    }
    public Token(long n, String k){
        this.name = Long.toString(n);
        this.value = n;
        this.kind = k;
        //System.out.prlongln("Token created: " + n + " " + k);
    }
    public Token(String k){
        if (k.equals("ARRAY")){
            this.kind = "ARRAY";
            this.array = new ArrayList();
            this.arrayToString = "[";
        }
    }
    public Object clone() throws CloneNotSupportedException{
        return (Token) super.clone();
        
    }
    
    public static boolean isKeyword(String key){
        ArrayList<String> keyWords = new ArrayList<String>() {{
    add("PRINT");
    add("MAKE");
    add("FOR");
    add("WHILE");
    add("ELSE");
    add("OR");
    add("IF");
    add("AND");
    
}};
        if (keyWords.contains(key)){
            return true;
        }
        return false;
        
        
        
    }
    public static boolean isMethod(String dataType, String method){
        if (dataType.equals("ARRAY")){
            ArrayList<String> methods = new ArrayList<String>() {{
                add("add");
                add("delete");
                add("sum");
                add("intersect");
            }}; 
            if (methods.contains(method)){
                return true;
            }
            return false;
        }
        return false;
    }
    
    public String getID(){
        return "Token: " + this.name + " " + this.kind;
                }
    public void changeSign(){
        this.value = value * -1;
        this.name = Long.toString(value);
        if (this.changedSign){
            this.changedSign = false;
        }else{
            this.changedSign = true;
        }
    }
    public void setValue(long v){
        this.value = v;
    }
    public void setStringValue(String string){
        this.stringValue = string;
    }
    public void add(Token element){
        this.array.add(element);
        if (element.kind.equals("NUMBER")){
            this.arrayToString += element.value + ",";
        }else{
            this.arrayToString += element.stringValue + ",";
        }
    }
    
    public void reset(){
        this.stringValue = null;
    }
}
