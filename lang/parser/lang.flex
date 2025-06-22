///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.parser;

import java_cup.runtime.Symbol;

%%
%public
%class LangLex
%implements java_cup.runtime.Scanner
%line
%column
%function next_token
%type java_cup.runtime.Symbol
%cupsym sym

%{
    private Symbol symbolFactory(int type) {
        return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbolFactory(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }

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
%}

%eofval{
    return symbolFactory(sym.EOF);
%eofval}

ID    = [a-z][a-zA-Z0-9_]*
TYID  = [A-Z][a-zA-Z0-9_]*
INT   = [0-9]+
FLOAT = (([0-9]+\.[0-9]+)|(\.[0-9]+))
CHAR  = \'([^\\'\n\r]|\\[nrt'\\]|\\[0-9]{3})\'

%%
<YYINITIAL>{
    "Int"       { return symbolFactory(sym.INT_TYPE); }
    "Char"      { return symbolFactory(sym.CHAR_TYPE); }
    "Bool"      { return symbolFactory(sym.BOOL_TYPE); }
    "Float"     { return symbolFactory(sym.FLOAT_TYPE); }
    "true"      { return symbolFactory(sym.TRUE, true); }
    "false"     { return symbolFactory(sym.FALSE, false); }

    "data"      { return symbolFactory(sym.DATA); }
    "abstract"  { return symbolFactory(sym.ABSTRACT); }
    "if"        { return symbolFactory(sym.IF); }
    "else"      { return symbolFactory(sym.ELSE); }
    "return"    { return symbolFactory(sym.RETURN); }
    "read"      { return symbolFactory(sym.READ); }
    "print"     { return symbolFactory(sym.PRINT); }
    "iterate"   { return symbolFactory(sym.ITERATE); }
    "new"       { return symbolFactory(sym.NEW); }
    "null"      { return symbolFactory(sym.NULL); }

    "=="        { return symbolFactory(sym.EQUAL_EQUAL); }
    "!="        { return symbolFactory(sym.NOT_EQUAL); }
    "&&"        { return symbolFactory(sym.AND); }
    "::"        { return symbolFactory(sym.DOUBLE_COLON); }
    ":"         { return symbolFactory(sym.COLON); }
    ";"         { return symbolFactory(sym.SEMICOLON); }
    ","         { return symbolFactory(sym.COMMA); }
    "."         { return symbolFactory(sym.DOT); }
    "="         { return symbolFactory(sym.ASSIGN); }
    "+"         { return symbolFactory(sym.PLUS); }
    "-"         { return symbolFactory(sym.MINUS); }
    "*"         { return symbolFactory(sym.MULT); }
    "/"         { return symbolFactory(sym.DIV); }
    "%"         { return symbolFactory(sym.MOD); }
    "<"         { return symbolFactory(sym.LESS_THAN); }
    ">"         { return symbolFactory(sym.GREATER_THAN); }
    "!"         { return symbolFactory(sym.NOT); }
    "("         { return symbolFactory(sym.OPEN_PAREN); }
    ")"         { return symbolFactory(sym.CLOSE_PAREN); }
    "["         { return symbolFactory(sym.OPEN_BRACKET); }
    "]"         { return symbolFactory(sym.CLOSE_BRACKET); }
    "{"         { return symbolFactory(sym.OPEN_BRACE); }
    "}"         { return symbolFactory(sym.CLOSE_BRACE); }

    {INT}       { return symbolFactory(sym.INT, str2int(yytext())); }
    {FLOAT}     { return symbolFactory(sym.FLOAT, str2float(yytext())); }
    {CHAR}      { return symbolFactory(sym.CHAR, yytext()); }

    {ID}        { return symbolFactory(sym.ID, yytext()); }
    {TYID}      { return symbolFactory(sym.TYID, yytext()); }

    "--".* { /* ignora */ }
    "{-"([^*]|\\*+[^-])*"-}" { /* ignora */ }
    [ \t\n\r\f]+           { /* ignora */ }

    .                      { throw new RuntimeException("Token inesperado: \"" + yytext() + "\" na linha " + (yyline+1) + " coluna " + (yycolumn+1)); }
}
