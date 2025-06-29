grammar Lang;

/*
@header {
package lang.parser;
}
*/

// --- REGRAS DO PARSER (SINTAXE) ---

prog: def+ EOF;

def: data | func;

data
  : ABSTRACT DATA TYID OPEN_BRACE (decl | func)* CLOSE_BRACE
  | DATA TYID OPEN_BRACE decl* CLOSE_BRACE
  ;

func
  // O corpo da função é sempre um bloco, conforme exemplos e estrutura da linguagem.
  : ID OPEN_PAREN params? CLOSE_PAREN (COLON type (COMMA type)*)? block
  ;

params: param (COMMA param)*;
param: ID DOUBLE_COLON type;

decl: ID DOUBLE_COLON type SEMICOLON;

type: btype (OPEN_BRACKET CLOSE_BRACKET)*;

btype: INT_TYPE | CHAR_TYPE | BOOL_TYPE | FLOAT_TYPE | TYID;

block: OPEN_BRACE stm* CLOSE_BRACE;

// Regra de comando (statement) reordenada para resolver ambiguidades.
stm
  : block
  | ifStm
  | iterateStm
  | ret
  | print SEMICOLON
  | read SEMICOLON
  | decl
  | lvalue ASSIGN expr SEMICOLON
  | cmdFcall SEMICOLON
  ;

print: PRINT expr; // Corrigido: Adicionado SEMICOLON implícito via regra stm.

read: READ lvalue;  // Corrigido: Adicionado SEMICOLON implícito via regra stm.

ret: RETURN exprList? SEMICOLON; // Permite 'return;' sem valor. exprList agora opcional.

ifStm: IF OPEN_PAREN expr CLOSE_PAREN stm (ELSE stm)?;

iterateStm: ITERATE OPEN_PAREN (ID COLON)? expr CLOSE_PAREN stm;

exprList: expr (COMMA expr)*;

// Hierarquia de expressões corrigida para refletir precedência e associatividade
expr: bterm (op=AND bterm)*; // Nível 1: && (Associatividade à Esquerda)

bterm: cterm ( (op=EQUAL_EQUAL | op=NOT_EQUAL) cterm)*; // Nível 2: ==, != (Associatividade à Esquerda)

cterm: aterm ( (op=LESS_THAN | op=GREATER_THAN) aterm)?; // Nível 3: <, > (Não Associativo)

aterm: mterm ( (op=PLUS | op=MINUS) mterm)*; // Nível 4: +, - (Associatividade à Esquerda)

mterm: uterm ( (op=MULT | op=DIV | op=MOD) uterm)*; // Nível 5: *, /, % (Associatividade à Esquerda)

uterm: op=(NOT | MINUS) uterm | pterm; // Nível 6: !, - (Associatividade à Direita)

pterm: lvalue
     | exprFcall (OPEN_BRACKET expr CLOSE_BRACKET)? // Chamada de função em expressão
     | literal
     | NEW (TYID | type) (OPEN_BRACKET expr CLOSE_BRACKET)? // new Racional, new Int, new Int[5]
     | OPEN_PAREN expr CLOSE_PAREN
     ;

lvalue: ID (DOT ID | OPEN_BRACKET expr CLOSE_BRACKET)*;

lvalueList: lvalue (COMMA lvalue)*;

cmdFcall: ID OPEN_PAREN exprList? CLOSE_PAREN (LESS_THAN lvalueList GREATER_THAN)?;

exprFcall: ID OPEN_PAREN exprList? CLOSE_PAREN;

literal: INT | FLOAT | CHAR | TRUE | FALSE | NULL;


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