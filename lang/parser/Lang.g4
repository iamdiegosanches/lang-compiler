grammar Lang;

/*
@header {
package lang.parser;
}
*/

// --- REGRAS DO PARSER (SINTAXE) ---

prog: def* EOF;

def: data | fun;

data
  : ABSTRACT DATA TYID OPEN_BRACE (decl | fun)* CLOSE_BRACE
  | DATA TYID OPEN_BRACE decl* CLOSE_BRACE
  ;

decl: ID DOUBLE_COLON type SEMICOLON;

fun
  : ID OPEN_PAREN params? CLOSE_PAREN (COLON type (COMMA type)*)? cmd
  ;

params : ID DOUBLE_COLON type (COMMA ID DOUBLE_COLON type)*;

// type : type OPEN_BRACKET CLOSE_BRACKET | btype
type : btype (OPEN_BRACKET CLOSE_BRACKET)*;

btype: INT_TYPE | CHAR_TYPE | BOOL_TYPE | FLOAT_TYPE | TYID;

block: OPEN_BRACE cmd* CLOSE_BRACE;

cmd
  : block                                                                                           # BlockCmd
  | IF OPEN_PAREN exp CLOSE_PAREN cmd (ELSE cmd)?                                                   # IfCmd
  //| IF OPEN_PAREN exp CLOSE_PAREN cmd
  //| IF OPEN_PAREN exp CLOSE_PAREN cmd ELSE cmd
  | ITERATE OPEN_PAREN itcond CLOSE_PAREN cmd                                                       # IterateCmd
  | READ lvalue SEMICOLON                                                                           # ReadCmd
  | PRINT exp SEMICOLON                                                                             # PrintCmd
  | RETURN exp (COMMA exp)* SEMICOLON                                                               # ReturnCmd
  | lvalue ASSIGN exp SEMICOLON                                                                     # AssignmentCmd
  | ID OPEN_PAREN exps? CLOSE_PAREN (LESS_THAN lvalue (COMMA lvalue)* GREATER_THAN)? SEMICOLON      # ProcCallCmd
  ;

itcond : ID COLON exp | exp;

exp
  : logicalAndExp
  ;

logicalAndExp
  : relationalExp (AND relationalExp)*
  ;

relationalExp
  : additiveExp ((LESS_THAN | EQUAL_EQUAL | NOT_EQUAL) additiveExp)*
  ;

additiveExp
  : multiplicativeExp                                             # ToMultExp
  | left=additiveExp operator=('+'|'-') right=multiplicativeExp   # AddSubExp
  ;

multiplicativeExp
  : unaryExp ((MULT | DIV | MOD) unaryExp)*
  ;

unaryExp
  : NOT unaryExp
  | MINUS unaryExp
  | primaryExp
  ;

primaryExp
  : lvalue                                                              # LvalueExp
  | ID OPEN_PAREN exps? CLOSE_PAREN OPEN_BRACKET exp CLOSE_BRACKET      # FunCallExp
  | NEW type (OPEN_BRACKET exp CLOSE_BRACKET)?                          # NewExp
  | OPEN_PAREN exp CLOSE_PAREN                                          # ParenExp
  | TRUE                                                                # TrueExp
  | FALSE                                                               # FalseExp
  | NULL                                                                # NullExp
  | INT                                                                 # IntExp
  | FLOAT                                                               # FloatExp
  | CHAR                                                                # CharExp
  ;

op : AND | LESS_THAN | EQUAL_EQUAL | NOT_EQUAL | PLUS | MINUS | MULT | DIV | MOD ;

// lvalue : ID | lvalue OPEN_BRACKET exp CLOSE_BRACKET | lvalue DOT ID ; 
lvalue
  : ID (OPEN_BRACKET exp CLOSE_BRACKET | DOT ID)*
  ;

exps : exp (COMMA exp)*
  ;


// --- REGRAS DO LEXER (TOKENS) ---
INT_TYPE      : 'Int';
CHAR_TYPE     : 'Char';
BOOL_TYPE     : 'Bool';
FLOAT_TYPE    : 'Float';
TRUE          : 'true';
FALSE         : 'false';
NULL          : 'null';
DATA          : 'data';
ABSTRACT      : 'abstract';
IF            : 'if';
ELSE          : 'else';
RETURN        : 'return';
READ          : 'read';
PRINT         : 'print';
ITERATE       : 'iterate';
NEW           : 'new';
EQUAL_EQUAL   : '==';
NOT_EQUAL     : '!=';
AND           : '&&';
DOUBLE_COLON  : '::';
COLON         : ':';
SEMICOLON     : ';';
COMMA         : ',';
DOT           : '.';
ASSIGN        : '=';
PLUS          : '+';
MINUS         : '-';
MULT          : '*';
DIV           : '/';
MOD           : '%';
LESS_THAN     : '<';
GREATER_THAN  : '>';
NOT           : '!';
OPEN_PAREN    : '(';
CLOSE_PAREN   : ')';
OPEN_BRACKET  : '[';
CLOSE_BRACKET : ']';
OPEN_BRACE    : '{';
CLOSE_BRACE   : '}';

ID            : [a-z][a-zA-Z0-9_]*;
TYID          : [A-Z][a-zA-Z0-9_]*;
INT           : [0-9]+;
FLOAT         : [0-9]* '.' [0-9]+;
CHAR          : '\'' ( ~[\\'\r\n] | '\\' . ) '\'';
COMMENT_LINE  : '--' .*? '\n' -> skip;
COMMENT_BLOCK : '{-' .*? '-}' -> skip;
WS            : [ \t\r\n]+ -> skip;