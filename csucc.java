/*	CIS 335			Assignment 7			Fall 2015

 * Names:  	Chad K Bartley			Jay Upadhyay
 * ID:		chbartle				jaupadhy	
 
 * Description: Top down recursive decent parser that generates SIC / XE code
 
 * Grammar Rules:
 *<stmt-list> ::= <stmt> { <stmt>}
 *<stmt>      ::= id = <expr> ;
 *<expr>      ::= <term> { + <term> | - <term> }
 *<term>      ::= <factor> { * <factor> | / <factor> }
 *<factor>    ::= id | intnum | ( <expr> )

 */

 import java.io.*;
 import java.util.*;

public class csucc {

//define Global Variables
	public static String[] S;
	public static int node = 0;
	public static int index = 0;
	public static ArrayList variables = new ArrayList();
	public static int REGA;
	public static int Ti = 1;
	public static Scanner scanner = null;

//File writer varialbles
  public static File file = null;
  public static FileWriter fw = null;
  public static BufferedWriter writer;

 public static void main(String[] args) throws IOException{

	//Read File from
	File inFile = null;

	if (0 < args.length){
      //If the user has supplied a file in command line

			inFile = new File(args[0]);

		try{
				//create output file
				file = new File("sample.asm");
	
				if(!file.exists()){
				//if file does not exist then create
				file.createNewFile();
			}
	
			//create file writer and bufferedWriter to print output SIC X/E
			fw = new FileWriter(file.getAbsoluteFile());
			writer = new BufferedWriter(fw);
	
		}
		catch( IOException e){	  
		}
		
	}

    //Call Statement List with File
		stmt_list( inFile );

 }


//Statement List Class
//Will Call Statement for each line in file
	static void stmt_list(File inFile){

		String end = "";
		BufferedReader br = null;

		try{

			//Assign BufferedReader
			br = new BufferedReader(new FileReader(inFile));

			//End = First Line of file
			end = br.readLine();

			while( end != null ){
			//While File has lines

				//Call Make Array
				mkArray( end );

				if(id(S[0])){
				//If first element of line is an ID then that line is a equation
				//Begin Calling Statment
					lexeme();
					stmt();
				}

				//Reset Index Value for next line of File
				index = 0;
				end = br.readLine();
			}
			//end of while

		}
		catch( IOException e){
			e.printStackTrace();
		}

		output("\n\n");

		//Print the resources generataed by the equations in the input file
		for(int i=0; i < variables.size(); i++){
			output( variables.get(i) + "\tRESW\t1");
		}

		//Close file reader/writer
		try{
			if (br != null)br.close();
			if (writer != null)writer.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
	}

	//Splits each equation into a string array
	static void mkArray( String input){
		S = input.split("\\s+");
	}

  //Writes the necessary SIC X/E output to the output file
  static void output(String outputString){
    try{
      writer.write(outputString + "\n");
    }
	catch (IOException ex){
    }
  }

   static void stmt(){
	   int i, j;
	   i = index;
	   if(S[ 1 ].equals("=")){
		   if(S.length == 4 ){
				output("\tMOV\t#" + S[ 2 ] + " , %EAX");
		   }
		   else{
				j = expr();
		   }
	    output("\tMOV\t" + "%EAX , " + S[0]);
		REGA = 0;
		lexeme();
	  }
   }
   
   static int expr(){
	   int i,j;
	   i = term();
	   while( next("+ -", index ) ){
		   lexeme();
		   String operator = S[ index  ];
		   j = term();
		   if(operator.equals("+")){
			   if(i == REGA){
				   output("\tADD\t" + S[j ]);
			   }
			   else if(j == REGA){
				   output("\tADD\t" + S[i]);
			   }
			   else{
				   GETA(i);
				   output("\tADD\t" + S[j]);
			   }
		   }
		   else{
			   if(i == REGA){
				   output("\tSUB\t" + S[j]);
			   }
			   else{
				   GETA(i);
				   output("\tSUB\t" + S[j]);
			   }
		   }
	   }
	   return i;
   }

   static int term(){
	   int i,j;
	   i = factor() ;
	   while( next("* /", index) ){
		  lexeme();
		   String operator = S[ index ];
		   j = factor();
		   if(operator.equals("*")){
			   if(i == REGA){
				   output("\tMUL\t" + S[j]);
			   }
			   else if(j == REGA){
				   output("\tMUL\t" + S[i]);
			   }
			   else{
				   GETA(i);
				   output("\tMUL\t" + S[j]);
			   }
		   }
		   else{
			   if(i == REGA){
				   output("\tDIV\t" + S[j]);
			   }
			   else{
				   GETA(i);
				   output("\tDIV\t" + S[j]);
			   }
		   }
	   }
	   return i;
   }

   static int factor() {
	   int i;
	   lexeme();
	   if(id(S[index])){
		   return index;
	   }
	   else if(intnum(S[index ])){
		   S[index ] = "#" + S[index];
		   return index;
	   }
	   else if(S[index].equals("(")){
		   i = expr();
		   lexeme();
		   return i;
	   }
	   return index + 1;
   }

//Increment the global index by 1
   static boolean lexeme(){
	   try{
		   index++;
		   return true;
	   }
	   catch(Exception e){
	   }
	   return false;
   }

   //Perform the necessary operations with the A register
   static void GETA(int node){
		if(REGA == 0){
        //Register A is empty. Move the String at location Node into a
			output("\tMOV\t" + S[node] + " , %EAX");
			REGA = node;
		}
		else if(node == REGA){
		//A is already filled with the object at S[node]. Do Nothing
		}
		else{
		//Swap what is in register A to a temp variables
		//Move new value into Register A
			output("\tMOV\t" + "%EAX , T" + Ti);		//save REGA
			S[REGA] = "T" + Ti;
			variables.add("T" + Ti);
			output("\tMOV\t" + S[node] + " , %EAX");	//new REGA
			Ti++;
			REGA = node;
		}
		return;
	}

	//Check if a string is an identifier
	//Return true is it is
	//Return False if not
	static boolean id(String string){
		if(string.matches("[a-zA-Z]")){
			//Check for duplicate variables
			//Do not add duplicate identifier to the list
			if(variables.contains(string)) {
				//Variables already contains this identifier
			}
			else{
				variables.add(string);
			}
			return true;
		}
		else{
			//String passed is not an identifier
			return false;
		}
	}

	//Check if a String reperesents a Number
	//Return True if it is a number
	static boolean intnum(String string){
		try{
			double d = Double.parseDouble(string);
		}
		catch(NumberFormatException e){
			return false;
		}
		return true;
	}

	//Checks if the symbol located at the position relative to the caller
	//Is equal to an Operator + - * /
	//Return true if it is
	static boolean next(String pattern, int i){
		try{
			String [] test = pattern.split(" ");
			
			if(S[i + 1].equals(test[0]) || S[i + 1].equals(test[1]) ){
				return true;
			}
		}
		catch(Exception e){
		}
		return false;
	}
}