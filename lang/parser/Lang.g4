grammar Lang;

/*
@header {
package lang.parser;
}
*/

prog: def+ EOF;

def: data | func;
data: DATA TYID OPEN_BRACE decls CLOSE_BRACE;
func: ID OPEN_PAREN params? CLOSE_PAREN (DOUBLE_COLON type)? block;

params: param (COMMA param)*;
param: ID DOUBLE_COLON type;

decls: decl+;
decl: ID DOUBLE_COLON type SEMICOLON;

type: btype (OPEN_BRACKET CLOSE_BRACKET)*;
btype: INT_TYPE | CHAR_TYPE | BOOL_TYPE | FLOAT_TYPE | TYID;

block: OPEN_BRACE stm* CLOSE_BRACE;

stm: decl
   | lvalue ASSIGN expr SEMICOLON
   | fcall SEMICOLON
   | print SEMICOLON
   | read SEMICOLON
   | ret SEMICOLON
   | ifStm
   | iterateStm
   | block
   ;

print: PRINT expr;
read: READ lvalue;
ret: RETURN expr?;
ifStm: IF expr block (ELSE block)?;
iterateStm: ITERATE expr block;

expr: bterm (op=AND bterm)*;
bterm: cterm ( (op=EQUAL_EQUAL | op=NOT_EQUAL) cterm)*;
cterm: aterm ( (op=LESS_THAN | op=GREATER_THAN) aterm)*;
aterm: mterm ( (op=PLUS | op=MINUS) mterm)*;
mterm: uterm ( (op=MULT | op=DIV | op=MOD) uterm)*;
uterm: op=(NOT | MINUS)? pterm;

// --- MUDANÇA ESTÁ AQUI ---
pterm: lvalue
     | fcall
     | literal
     | NEW type // ANTES: NEW btype. AGORA PERMITE CRIAR ARRAYS.
     | OPEN_PAREN expr CLOSE_PAREN
     ;

lvalue: ID (DOT ID | OPEN_BRACKET expr CLOSE_BRACKET)*;
fcall: ID OPEN_PAREN (expr (COMMA expr)*)? CLOSE_PAREN;
literal: INT | FLOAT | CHAR | TRUE | FALSE | NULL;


// --- REGRAS DO LEXER (TOKENS) ---
// (Esta parte permanece a mesma)

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
FLOAT         : ([0-9]+ '.' [0-9]*) | ('.' [0-9]+);
CHAR          : '\'' ( ~[\\'\r\n] | '\\' . ) '\'';

COMMENT_LINE  : '--' .*? '\n' -> skip;
COMMENT_BLOCK : '{-' .*? '-}' -> skip;
WS            : [ \t\r\n]+ -> skip;