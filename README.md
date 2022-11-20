# PLC-TEST2
# a. Recognizing Lexemes
Addition: + <br/>
Subtraction: - <br/>
Multiplication: * <br/>
Division: / <br/>
Modulo: % <br/>
Less than: < <br/> 
Greater than: > <br/> 
Less than Equal To: <= <br/>
Greater than Equal To: >= <br/>
Equal To: = <br/>
Not Equal To: =/= <br/>
Not: ! <br/>
Assignment: <- <br/>
Integer Literals: [1-9][0-9]*\_(B|S|I|L) <br/>
Identifiers: [a-zA-Z_][a-zA-Z_][a-zA-Z_][a-zA-Z_][a-zA-Z_][a-zA-Z_][a-zA-Z_]?[a-zA-Z_]? <br/>
loop: O <br/>
if: ? <br/>
else: | <br/>
seperate statements: ; <br/>
begin program: start <br/>
end program: stop <br/>
data type declaration: num <br/>

# b. Production Rules
\<program> -> start \<stmt_list> stop <br/>
\<stmt_list> -> \<stmt>';'{<stmt>';'} <br/>
\<stmt> -> \<declare> | \<if> | \<loop> | \<assign> <br/>
\<declare> -> 'num' 'id' \<br/>
\<assign> -> 'id' '<-' \<expr> <br/>
\<if> -> '?' \<bool_expr> '{' \<stmt_list> '}' ['|' '{' \<stmt_list> '}'] <br/>
\<loop> -> 'O' \<bool_expr> '{' \<stmt_list> '}' <br/>

\<expr> -> \<div> {'^' \<div>} <br/>
\<div> -> \<mod> {'/' \<mod>} <br/>
\<mod> -> \<mul> {('%' \<mul>} <br/>
\<mul> -> \<sub> {'*' \<sub>} <br/>
\<sub> -> \<add> {'-' \<add>} <br/>
\<add> -> \<fac> {'+' \<fac>} <br/>
\<fac> -> 'id' | 'int_lit' | '(' \<expr> ')' <br/>

\<bool_expr> -> \<bequal> {('||'|'&' \<bequal>} <br/>
\<bequal> -> \<brel> {('='|'=/=') \<brel>} <br/>
\<brel> -> \<bexpr> {('<='|'>='|'<'|'>') \<bexpr>} <br/>
\<bexpr> -> \<bdiv> {'^' \<bdiv>} <br/>
\<bdiv> -> \<bmod> {'/' \<bmod>} <br/>
\<bmod> -> \<bmul> {('%' \<bmul>} <br/>
\<bmul> -> \<bsub> {'*' \<bsub>} <br/>
\<bsub> -> \<badd> {'-' \<badd>} <br/>
\<badd> -> \<bnot> {'+' \<bnot>} <br/>
\<bnot> -> [!]\<bfac> <br/>
\<bfac> -> 'id' | 'int_lit' | 'bool_lit' | '(' \<bexpr> ')' <br/>

# c. LL Gramar?
Grammar conforms to LL grammar standards: <br/>
1. Grammar holds no left-hand recursion <br/>
2. Grammar passes pairwise disjointness test: <br/>
FIRST(program) = {start} <br/>
FIRST(stmt_list) = {num, id, ?, O} <br/>
FIRST(stmt) = {num}{id}{?}{O} <br/>
FIRST(declare) = {num} <br/>
FIRST(assign) = {id} <br/>
FIRST(if) = {?} <br/>
FIRST(loop) = {O} <br/>
FIRST(expr) = {id, int_lit, (} <br/>
FIRST(div) = {id, int_lit, (} <br/>
FIRST(mod) = {id, int_lit, (} <br/>
FIRST(mul) = {id, int_lit, (} <br/>
FIRST(sub) = {id, int_lit, (} <br/>
FIRST(add) {id, int_lit, (} <br/>
FIRST(fac) = {id}{int_lit}{(} <br/>
FIRST(bool_expr) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(bequal) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(brel) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(bexpr) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(bdiv) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(bmod) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(bmul) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(bsub) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(badd) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(bnot) = {!, id, int_lit, bool_lit, (} <br/>
FIRST(bfac) = {id}{int_lit}{bool_lit}{(} <br/>

# d. Ambiguous?

Grammar rules produce a non-ambiguous grammar

# e-f. Lexical and Syntax Analysis

see code in src

# g. Test Code

see files in testCode folder

code1.txt - valid code

![code1Test](https://user-images.githubusercontent.com/54324630/202879428-cc162586-0662-43a3-8eb0-7c74d8373d07.PNG)

code2.txt - valid code

![code2Test](https://user-images.githubusercontent.com/54324630/202879552-d42073a2-58b1-4138-9cfd-8c2d9baa47e3.PNG)

code3.txt - lexical errors

![code3Test](https://user-images.githubusercontent.com/54324630/202879737-d4225c8e-6919-4c41-a580-c31ebab356ba.PNG)

line 2 - "numb" is not a keyword and starts with a letter so it will be treated as a identifier. "numb" does not match the criteria of a valid identifier, causing an error. <br/>
line 3 - "<--" is an unknown token and cannot be treated as a identifier or integer literal, causing an error. <br/>
line 4 - "true" is not a valid boolean_literal. A valid boolean_literal starts with a capital letter (True). <br/>
line 5 - "asdf" is not a valid identifier, causing an error. <br/>
line 7 - "stoop" is not the valid stop keyword. Will be treated as an identifier but does not match that criteria, causing an error. <br/>

code4.txt - syntax errors

![code4Test](https://user-images.githubusercontent.com/54324630/202880343-55a5ab52-13b3-4a52-9f02-40667804c029.PNG)

line 2 - No opening bracket on the if statement, causing an error. <br/>
line 4 - Assigning "True" to a identifier. This is not allowed, causing an error. <br/>
line 6 - Two valid identifiers next to eachother. Does not make sense in this language, causing an error. <br/>
line 7 - Assigning the 'or' token to a identifier. This is not validm causing an error. <br/>
line 7 - Missing semicolon, causing an error. <br/>

# h. LR Trace

![trace2p1](https://user-images.githubusercontent.com/54324630/202878245-bec3c9bf-ff0e-4e5d-a292-d17508acd74c.PNG)
![trace2p2](https://user-images.githubusercontent.com/54324630/202878247-7062dadd-c30a-43d1-9f75-90856224a2d8.PNG)

<img src="https://user-images.githubusercontent.com/54324630/202878245-bec3c9bf-ff0e-4e5d-a292-d17508acd74c.PNG" width="600" height="700">
