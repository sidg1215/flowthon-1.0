# flowthon-1.0
Flowthon is the first programming language that I am writing to help me learn about the lexing and parsing components of an interpreter. It is a dynamically typed language. The language supports integers, strings, lists, basic arithmetic using all data types listed, and basic control flow with tools such as if statements and while loops.

Flowthon has a very straightforward grammar:
    STATEMENT ----> PRINT (EXPRESSION) | MAKE (VAR)(=)(EXPRESSION) | IF (EXPRESSION) | WHILE (EXPRESSION)
    EXPRESSION ----> PRODUCT[(+|-|>=|<=|or|and|==)EXPRESSION]
    PRODUCT ----> (NUMBER|STRING|VARIABLE)[(*|/)][EXPRESSION|PRODUCT]

Currently, I am working on adding more control flow options, methods to the different data types, and set operations such as set unions and intersections.

This version of Flowthon is written entirely in Java, however I have plans of moving the project to C once I have completely finished this project. With C, Flowthon will be much faster and efficient.
