# 1TopDownParser
Top down recursive decent parser in JAVA that generates SIC/XE code
----------------------------------------------------------------------

• Our Compiler follows a top down recursive descent method. It is designed to work with the grammar Rules:

stmt-list ::= stmt { stmt }

stmt      ::= id = expr ;

expr      ::= term { + term | - term }

term      ::= factor { * factor | / factor }

factor    ::= id | intnum | ( expr )

• The Assignment is designed so that we can demonstrate our understanding of top down recursive descent method and print out relative SIC X/E code. Our code will read a file containing equations, parse the equations following the grammar rules, and print out the necessary SIC X/E code to a file.

• The user will run the program csucc.java from the command line and will also provide a file containing arithmetic equations as an argument in the command line. 

• The main method in csucc.java verifies that the user has provided a file as an argument, then create file read/write objects that will be needed throughout the program. It then calls stmt_list(File) with the input file as an argument.

Stmt_list( File ): will call Statement for each line in the file, allowing for the first grammar rule to be fulfilled. It also splits each line of the file into a String[] using mkArray(String) and sets the global index value = 0. 

Stmt(): This method will verify the second grammar rule. If the second grammar rule is true it will call expr().

Expr():  expr() will call term() at the very beginning to verify the third grammar rule

Term(): Term will verify the fourth grammar rule. It calls factor()

Factor(): factor will verify the fifth grammar rule. 

Bugs (Solved):  Bugs that we experienced while implementing this assignment were found in both the development of the top down recursive approach and in printing out the correct SIC X/E code to file. 

• We were able to follow the code example you provided for us during class, but some modifications were needed. We needed to allow for stmt_list(), which required us to change lexeme(). We modified lexeme() to simply increment the global index for each equation. Stmt_list() is now responsible for receiving the whole equation at once from the input file and turning it into a String[]. We also added several methods to perform specific actions related to various needs of the assignment. 

• For developing the SIC X/E output portion we encountered several bugs. The bugs primarily focused around keeping track of your current location within each equation. The top down recursive descent approach can search through many levels of grammar before performing any actions, incrementing the global index tracker as it goes. To account for this we used local index within each grammar rule method. Using the local index values we were able to accurately keep track of relative index and perform the appropriate action regarding the time when the method was first called.

• We also needed to use MOV in the SIC X/E output code instead of the traditional LDA & STA.

Project Compiling Instruction:

• To Compile:
	javac csucc.java
• Run:
	java csucc (input File)
