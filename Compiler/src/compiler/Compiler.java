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
public class Compiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        // TODO code application logic here
        Compiler("/Users/siddharthgupta/Desktop/Compiler/javaCompiler.txt");
    }
    public static void Compiler(String path) throws IOException, CloneNotSupportedException{
        Path p = Paths.get(path);
        List<String> file = Files.readAllLines(p, StandardCharsets.UTF_8);
        HashMap variables = new HashMap();
        String program = "";
        for (String line: file){
            program = program + line+";";
        }
        Lexer lexer = new Lexer(program, variables);
        ArrayList<Token> tokens = lexer.getTokens();
        Parser parser = new Parser(tokens, variables);
        HashMap newVariables = parser.getVariables();
        newVariables.putAll(variables);
    }
    
}
