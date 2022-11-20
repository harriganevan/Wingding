import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Analyzer{
    
    public static HashMap<String,Integer> keywords = new HashMap<String, Integer>();
    
    public static void main(String[] args) throws IOException {
        
        //token codes
        keywords.put("start", 1);    //program start 
        keywords.put("stop", 2);     //program end
        keywords.put("num", 3);      //data type declaration (integer_literals)
        keywords.put("<-", 4);       //assignment operator
        keywords.put("+", 5);        //addition operator
        keywords.put("-", 6);        //subtraction operator
        keywords.put("*", 7);        //multiplication operator
        keywords.put("/", 8);        //division operator
        keywords.put("%", 9);        //modulo operator
        keywords.put("(", 10);       //left parenthesis
        keywords.put(")", 11);       //right parenthesis
        keywords.put(";", 12);       //statement end
        keywords.put("O", 13);       //loop
        keywords.put("=", 15);       //equal
        keywords.put("=/=", 16);     //not equal
        keywords.put("<", 17);       //less than
        keywords.put(">", 18);       //greater than
        keywords.put("<=", 19);      //less than equal to
        keywords.put(">=", 20);      //greater than equal
        keywords.put("!", 21);       //not
        keywords.put("?", 22);       //if
        keywords.put("|", 22);       //else
        keywords.put("{", 23);       //left bracket
        keywords.put("}", 24);       //right bracket
        keywords.put("id", 25);      //id
        keywords.put("int_lit", 26); //int_lit
        keywords.put("True", 27);	 //True
        keywords.put("False", 28);	 //False
        keywords.put("||", 29);		 //or
        keywords.put("&", 30);		 //and
        
        String code = Files.readString(Paths.get("src/code1.txt"), StandardCharsets.UTF_8); //text file to string
        
        String[] split = code.split("\\s+"); //split code string to array of strings(lexemes)       
        List<String> codeList = Arrays.asList(split); //translating string array to list of strings
        
        List<Integer> tokens = Lexer.getTokens(codeList);
        
        System.out.print("Tokens: ");
        for(int i = 0; i < tokens.size(); i++) {
            System.out.print(tokens.get(i) + " ");
        }
        System.out.println();
        
        Parser.tokens = tokens;
        
        Parser.checkSyntax();
    }
    
    //lexer class to generate tokens from string of code or generate error on invalid code
    static class Lexer {
        //returns list of tokens from code)
        public static List<Integer> getTokens(List<String> lexemes) {
            
            //store tokens
            List<Integer> tokens = new ArrayList<Integer>();
            
            outer: for(String lexeme: lexemes) {
                //if lexeme is in the keywords map
                if(keywords.get(lexeme) != null) {
                    tokens.add(keywords.get(lexeme));
                }
                else { //check if lexeme is a valid int_lit or id
                    if(Character.isLetter(lexeme.charAt(0)) || lexeme.charAt(0) == '_') { //if lexeme starts with a character or '_'
                        if(lexeme.length() < 9 && lexeme.length() > 5) { //if id is valid length
                            for(int i = 1; i < lexeme.length(); i++) { 
                                if(!(Character.isLetter(lexeme.charAt(i)) || lexeme.charAt(i) == '_')) { //if id characters are valid
                                    Error("Invalid identifier - " + "\"" + lexeme + "\"");
                                    break outer;
                                }
                            }
                            tokens.add(keywords.get("id")); //valid id -> add to token list
                        }
                        else {
                            Error("Invalid identifier - " + "\"" + lexeme + "\""); 
                            break outer;
                        }
                    }
                    else if(Character.isDigit(lexeme.charAt(0))) { //if lexeme starts with a digit
                        if(lexeme.length() < 3) {
                            Error("Invalid integer literal - " + "\"" + lexeme + "\"");
                            break outer;
                        }
                        for(int i = 1; i < lexeme.length()-2; i++) {
                            if(!Character.isDigit(lexeme.charAt(i))) { //if there is a character in int_lit that is not a digit
                                Error("Invalid integer literal - " + "\"" + lexeme + "\"");
                                break outer;
                            }
                        }
                        if(lexeme.charAt(lexeme.length()-2) != '_') { //if second to last character isn't '_'
                            Error("Invalid integer literal - " + "\"" + lexeme + "\"");
                            break outer;
                        }
                        if(!(lexeme.charAt(lexeme.length()-1) == 'B' || lexeme.charAt(lexeme.length()-1) == 'S' || lexeme.charAt(lexeme.length()-1) == 'I' || lexeme.charAt(lexeme.length()-1) == 'L')) {
                            Error("Invalid integer literal - " + "\"" + lexeme + "\"");
                            break outer;
                        }
                        tokens.add(keywords.get("int_lit")); //valid int_lit -> add to token list
                    }
                    else { //Isn't a keyword and doesn't start with a digit or letter
                        Error("Unkown token - " + "\"" + lexeme + "\"");
                        break outer;
                    }
                }
            }
            return tokens; //return list of tokens
        }
        
        static void Error(String message){ //print error message
            System.out.println(message);
            System.exit(1);
        }
    }
    
    static class Parser {
        
        static List<Integer> tokens = new ArrayList<Integer>();
        static int currentToken = 0;
        
        public static void checkSyntax() {
            program();
        }
        
        static void program() {
            if(tokens.get(currentToken) == keywords.get("start")) { //if code starts with "start"
            	currentToken++;
                if(tokens.get(currentToken) == keywords.get("stop")) { //if code is only "start stop"
                	System.out.println("COMPLETE - NO ERRORS DETECTED");
                	System.exit(1);
                }
                stmt_list(); //start processing statements
            } else {Error("ERROR - code should start with \"start\"");}

            if(tokens.get(currentToken) == keywords.get("stop")) { //if last token is "stop"
                System.out.println("COMPLETE - NO ERRORS DETECTED");
            } else {Error("ERROR - code should end with \"stop\"");}
        }
        
        static void stmt_list() {    
            stmt();
            if(currentToken == tokens.size()) { //if end of file is reached
        		Error("ERROR- code should end with \"stop\"");
        	}
            if(tokens.get(currentToken) != keywords.get(";")) { //if statement doesn't end with ;
            	Error("ERROR - statements should end with \";\"");
            }
            while(tokens.get(currentToken) == keywords.get(";")) { //if there's a ; after a statement
            	currentToken++;
            	if(currentToken == tokens.size()) { //if end of file is reached
            		Error("ERROR- code should end with \"stop\"");
            	}
            	if(tokens.get(currentToken) == keywords.get("}")) { //if end of an if/or statement
            		break;
            	}
            	if(tokens.get(currentToken) == keywords.get("stop")) { //if no more statements
            		break;
            	}
            	stmt();
            }
        }
        
        static void stmt() {
            if(tokens.get(currentToken) == keywords.get("num")) {
                currentToken++;
                declare();
            }
            else if(tokens.get(currentToken) == keywords.get("id")) {
                currentToken++;
                assign();
            }
            else if(tokens.get(currentToken) == keywords.get("?")) {
                currentToken++;
                _if_();
            }
            else if(tokens.get(currentToken) == keywords.get("O")) {
                currentToken++;
                loop();
            }
            else {Error("ERROR - invalid statement");}
        }
        
        static void declare() {
            if(tokens.get(currentToken) == keywords.get("id")) {
                currentToken++;
            } else {Error("ERROR - invalid declaration");}
        }
        
        static void assign() {
            if(tokens.get(currentToken) == keywords.get("<-")) {
                currentToken++;
                expr();
            } else {Error("ERROR - invalid assignment");}
        }
        
        static void _if_() {
            bool_expr();
            if(tokens.get(currentToken) == keywords.get("{")) {
                currentToken++;
                stmt_list();
                if(tokens.get(currentToken) == keywords.get("}")) {
                    currentToken++;
                    if(tokens.get(currentToken) == keywords.get("|")) {
                        currentToken++;
                        if(tokens.get(currentToken) == keywords.get("{")) {
                            currentToken++;
                            stmt_list();
                            if(tokens.get(currentToken) == keywords.get("}")) {
                                currentToken++;
                            } else {Error("ERROR - unclosed selection statement");}
                        } else {Error("ERROR - unopened selection statement");}
                    } //NOT AN ERROR
                } else {Error("ERROR - unclosed selection statement");}
            } else {Error("ERROR - unopened selection statement");}
        }
        
        static void loop() {
            bool_expr();
            if(tokens.get(currentToken) == keywords.get("{")) {
                currentToken++;
                stmt_list();
                if(tokens.get(currentToken) == keywords.get("}")) {
                    currentToken++;
                } else {Error("ERROR - unclosed loop");}
            } else {Error("ERROR - unopened loop");}
        }
        
        
        //operations
        static void expr() {
            div();
            while(tokens.get(currentToken) == keywords.get("^")) {
                currentToken++;
                div();
            }
        }
        
        static void div() {
            mod();
            while(tokens.get(currentToken) == keywords.get("/")) {
                currentToken++;
                mod();
            }
        }
        
        static void mod() {
            mul();
            while(tokens.get(currentToken) == keywords.get("%")) {
                currentToken++;
                mul();
            }
        }
        
        static void mul() {
            sub();
            while(tokens.get(currentToken) == keywords.get("*")) {
                currentToken++;
                sub();
            }
        }
        
        static void sub() {
            add();
            while(tokens.get(currentToken) == keywords.get("-")) {
                currentToken++;
                add();
            }
        }
        
        static void add() {
            fac();
            while(tokens.get(currentToken) == keywords.get("+")) {
                currentToken++;
                fac();
            }
        }
        
        static void fac() {
            if(tokens.get(currentToken) == keywords.get("id") || tokens.get(currentToken) == keywords.get("int_lit")) {
                currentToken++;
            }
            else if(tokens.get(currentToken) == keywords.get("(")){
                currentToken++;
                expr();
                if(tokens.get(currentToken) == keywords.get(")")) {
                    currentToken++;
                } else {Error("ERROR - unclosed parentheses");}
            } else {Error("ERROR - invalid valid factor");}
        }
        
        
        //BOOLEANS
        static void bool_expr() {
            bequal();
            while(tokens.get(currentToken) == keywords.get("||") || tokens.get(currentToken) == keywords.get("&")) {
                currentToken++;
                bequal();
            }
        }
        
        static void bequal() {
            brel();
            while(tokens.get(currentToken) == keywords.get("=") || tokens.get(currentToken) == keywords.get("=/=")) {
                currentToken++;
                brel();
            }
        }
        
        static void brel() {
            bexpr();
            while(tokens.get(currentToken) == keywords.get("<=") || tokens.get(currentToken) == keywords.get(">=") || tokens.get(currentToken) == keywords.get("<") || tokens.get(currentToken) == keywords.get(">")) {
                currentToken++;
                bexpr();
            }
        }
        
        static void bexpr() {
            bdiv();
            while(tokens.get(currentToken) == keywords.get("^")) {
                currentToken++;
                bdiv();
            }
        }
        
        static void bdiv() {
            bmod();
            while(tokens.get(currentToken) == keywords.get("/")) {
                currentToken++;
                bmod();
            }
        }
        
        static void bmod() {
            bmul();
            while(tokens.get(currentToken) == keywords.get("%")) {
                currentToken++;
                bmul();
            }
        }
        
        static void bmul() {
            bsub();
            while(tokens.get(currentToken) == keywords.get("*")) {
                currentToken++;
                bsub();
            }
        }
        
        static void bsub() {
            badd();
            while(tokens.get(currentToken) == keywords.get("-")) {
                currentToken++;
                badd();
            }
        }
        
        static void badd() {
            bnot();
            while(tokens.get(currentToken) == keywords.get("+")) {
                currentToken++;
                bnot();
            }
        }
        
        static void bnot() {
            if(tokens.get(currentToken) == keywords.get("!")) {
                currentToken++;
            }
            bfac();
        }
        
        static void bfac() {
            if(tokens.get(currentToken) == keywords.get("id") || tokens.get(currentToken) == keywords.get("int_lit") || tokens.get(currentToken) == keywords.get("True") || tokens.get(currentToken) == keywords.get("False")) {
                currentToken++;
            }
            else if(tokens.get(currentToken) == keywords.get("(")){
                currentToken++;
                bool_expr();
                if(tokens.get(currentToken) == keywords.get(")")) {
                    currentToken++;
                } else {Error("ERROR - unclosed parentheses");}
            } else {Error("ERROR - invalid boolean factor");}
        }
        
        static void Error(String message) {
            System.out.println(message);
            System.exit(1);
        }
        
    }
}