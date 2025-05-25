%%

%class Lexer
%implements java_cup.runtime.Scanner
%type Token
%line
%column
%eofval{
    return new Token(Token.TokenType.EOF, yyline + 1, yycolumn + 1, "EOF");
%eofval}
%eofclose

%{
    private Token symbol(Token.TokenType type) {
        return new Token(type, yyline + 1, yycolumn + 1, yytext());
    }

    private Token symbol(Token.TokenType type, String lexeme) {
        return new Token(type, yyline + 1, yycolumn + 1, lexeme);
    }
%}

// Expressões Regulares
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

Identifier = [a-z][a-zA-Z0-9_]*
TypeId = [A-Z][a-zA-Z0-9_]*
IntegerLiteral = [0-9]+
FloatLiteral = [0-9]+"."[0-9]+
CharLiteral = \'([^'\\]|\\.)\'

CommentLine = "--".*
CommentBlock = "{-" [^}]* "}-"

%%

<YYINITIAL> {
    // Palavras-chave
    "data"      { return symbol(Token.TokenType.DATA); }
    "if"        { return symbol(Token.TokenType.IF); }
    "else"      { return symbol(Token.TokenType.ELSE); }
    "iterate"   { return symbol(Token.TokenType.ITERATE); }
    "read"      { return symbol(Token.TokenType.READ); }
    "print"     { return symbol(Token.TokenType.PRINT); }
    "return"    { return symbol(Token.TokenType.RETURN); }
    "new"       { return symbol(Token.TokenType.NEW); }
    "Int"       { return symbol(Token.TokenType.INT_TYPE); }
    "Char"      { return symbol(Token.TokenType.CHAR_TYPE); }
    "Bool"      { return symbol(Token.TokenType.BOOL_TYPE); }
    "Float"     { return symbol(Token.TokenType.FLOAT_TYPE); }
    "true"      { return symbol(Token.TokenType.TRUE); }
    "false"     { return symbol(Token.TokenType.FALSE); }
    "null"      { return symbol(Token.TokenType.NULL); }

    // Literais e Identificadores
    {IntegerLiteral} { return symbol(Token.TokenType.INT_LITERAL); }
    {FloatLiteral}   { return symbol(Token.TokenType.FLOAT_LITERAL); }
    {CharLiteral}    { return symbol(Token.TokenType.CHAR_LITERAL, yytext().substring(1, yylength() - 1)); }
    {Identifier}     { return symbol(Token.TokenType.ID); }
    {TypeId}         { return symbol(Token.TokenType.TYID); }

    // Operadores e Pontuação
    "="         { return symbol(Token.TokenType.ASSIGN); }
    "+"         { return symbol(Token.TokenType.PLUS); }
    "-"         { return symbol(Token.TokenType.MINUS); }
    "*"         { return symbol(Token.TokenType.MULTIPLY); }
    "/"         { return symbol(Token.TokenType.DIVIDE); }
    "%"         { return symbol(Token.TokenType.MODULO); }
    "&&"        { return symbol(Token.TokenType.AND); }
    "!"         { return symbol(Token.TokenType.NOT); }
    "<"         { return symbol(Token.TokenType.LESS_THAN); }
    "=="        { return symbol(Token.TokenType.EQUALS); }
    "!="        { return symbol(Token.TokenType.NOT_EQUALS); }
    "("         { return symbol(Token.TokenType.LPAREN); }
    ")"         { return symbol(Token.TokenType.RPAREN); }
    "["         { return symbol(Token.TokenType.LBRACK); }
    "]"         { return symbol(Token.TokenType.RBRACK); }
    "{"         { return symbol(Token.TokenType.LBRACE); }
    "}"         { return symbol(Token.TokenType.RBRACE); }
    ","         { return symbol(Token.TokenType.COMMA); }
    ";"         { return symbol(Token.TokenType.SEMICOLON); }
    "."         { return symbol(Token.TokenType.DOT); }

    // Comentários e Espaços em Branco
    {CommentLine}   { /* Ignorar */ }
    {CommentBlock}  { /* Ignorar */ }
    {WhiteSpace}    { /* Ignorar */ }
}

[^] { throw new RuntimeException("Caractere inválido: " + yytext() + " na linha " + (yyline+1)); }
