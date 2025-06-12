///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////

%%
%public
%class LangLex
%line
%column
%function nextToken
%type Token

%{
    private int str2int(String s) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            System.out.println("Erro ao converter int: " + s);
        }
        return 0;
    }

    private float str2float(String s) {
        try {
            return Float.parseFloat(s);
        } catch(Exception e) {
            System.out.println("Erro ao converter float: " + s);
        }
        return 0;
    }

    private char escape2Char(String s) {
        switch (s.charAt(2)) {
            case 'n': return '\n';
            case 't': return '\t';
            case 'b': return '\b';
            case 'r': return '\r';
            case '\'': return '\'';
            case '\"': return '\"';
            case '\\': return '\\';
            default: throw new Error("Caractere de escape inválido: " + s);
        }
    }

    public Token mkTk(TK tk){
        return new Token(tk, yyline + 1, yycolumn + 1);
    }

    public Token mkTk(TK tk, Object o){
        return new Token(tk, yyline + 1, yycolumn + 1, o);
    }
%}

%eofval{
    return mkTk(TK.EOF);
%eofval}

// Regras léxicas
ID    = [a-z][a-zA-Z0-9_]*
TYID  = [A-Z][a-zA-Z0-9_]*
INT   = [0-9]+
FLOAT = (([0-9]+\.[0-9]+)|(\.[0-9]+))
CHAR  = \'([^\\'\n\r]|\\[nrt'\\]|\\[0-9]{3})\'
ESCAPE = "'" \\[ntbr\\\\e'\"] "'"
WHITE =  [ \n\t\r]+

%%
<YYINITIAL>{
    "Int"       { return mkTk(TK.INT_TYPE); }  // Tipo Int
    "Char"      { return mkTk(TK.CHAR_TYPE); } // Tipo Char
    "Bool"      { return mkTk(TK.BOOL_TYPE); } // Tipo Bool
    "Float"     { return mkTk(TK.FLOAT_TYPE); } // Tipo Float
    "true"      { return mkTk(TK.TRUE, true); }
    "false"     { return mkTk(TK.FALSE, false); }


    // Palavras-chave
    "data"      { return mkTk(TK.DATA); }
    "abstract"  { return mkTk(TK.ABSTRACT); }
    "if"        { return mkTk(TK.IF); }
    "else"      { return mkTk(TK.ELSE); }
    "return"    { return mkTk(TK.RETURN); }
    "read"      { return mkTk(TK.READ); }
    "print"     { return mkTk(TK.PRINT); }
    "iterate"   { return mkTk(TK.ITERATE); }
    "new"       { return mkTk(TK.NEW); }
    "null"      { return mkTk(TK.NULL); }

    // Operadores e símbolos
    "=="        { return mkTk(TK.EQUAL_EQUAL); }
    "!="        { return mkTk(TK.NOT_EQUAL); }
    "&&"        { return mkTk(TK.AND); }
    "::"        { return mkTk(TK.DOUBLE_COLON); }
    ":"         { return mkTk(TK.COLON); }
    ";"         { return mkTk(TK.SEMICOLON); }
    ","         { return mkTk(TK.COMMA); }
    "."         { return mkTk(TK.DOT); }
    "="         { return mkTk(TK.ASSIGN); }
    "+"         { return mkTk(TK.PLUS); }
    "-"         { return mkTk(TK.MINUS); }
    "*"         { return mkTk(TK.MULT); }
    "/"         { return mkTk(TK.DIV); }
    "%"         { return mkTk(TK.MOD); }
    "<"         { return mkTk(TK.LESS_THAN); }
    ">"         { return mkTk(TK.GREATER_THAN); }
    "!"         { return mkTk(TK.NOT); }
    "("         { return mkTk(TK.OPEN_PAREN); }
    ")"         { return mkTk(TK.CLOSE_PAREN); }
    "["         { return mkTk(TK.OPEN_BRACKET); }
    "]"         { return mkTk(TK.CLOSE_BRACKET); }
    "{"         { return mkTk(TK.OPEN_BRACE); }
    "}"         { return mkTk(TK.CLOSE_BRACE); }

    // Literais
    {INT}       { return mkTk(TK.INT, str2int(yytext())); }
    {FLOAT}     { return mkTk(TK.FLOAT, str2float(yytext())); }
    {CHAR}      { return mkTk(TK.CHAR, yytext()); }
    {ESCAPE}    { return mkTk(TK.CHAR, escape2Char(yytext())); }

    // Identificadores
    {ID}        { return mkTk(TK.ID, yytext()); }
    {TYID}      { return mkTk(TK.TYID, yytext()); }

    // Comentários
    "--".*                 { /* comentário de uma linha */ }
    "{-"([^*]|\\*+[^-])*"-}" { /* comentário de múltiplas linhas */ }

    // Espaços
    [ \t\n\r\f]+           { /* ignora */ }

    // Token inválido
    .                      { throw new RuntimeException("Token inesperado: \"" + yytext() + "\" na linha " + (yyline+1) + " coluna " + (yycolumn+1)); }
}
