///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.parser;

import java.util.ArrayList;
import java_cup.runtime.Symbol;

%%

%public
%function nextToken
%type Symbol
%class LangLexer

%line
%column

%unicode

%eofval{
   return new Symbol(LangParserSym.EOF, yyline + 1, yycolumn + 1);
%eofval}

%{
    private ArrayList arr;
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

ID    = [a-z][a-zA-Z0-9_]*
TYID  = [A-Z][a-zA-Z0-9_]*
INT   = [0-9]+
FLOAT = (([0-9]+\.[0-9]+)|(\.[0-9]+))
CHAR  = \'([^\\'\n\r]|\\[nrt'\\]|\\[0-9]{3})\'

%%
<YYINITIAL>{
    "Int"       { return new Symbol(LangParserSym.INT_TYPE, yyline + 1, yycolumn + 1); }
//    "Char"      { return new Symbol(LangParserSym.CHAR_TYPE, yyline + 1, yycolumn + 1); }
    "Bool"      { return new Symbol(LangParserSym.BOOL_TYPE, yyline + 1, yycolumn + 1); }
    "Float"     { return new Symbol(LangParserSym.FLOAT_TYPE, yyline + 1, yycolumn + 1); }
    "true"      { return new Symbol(LangParserSym.TRUE, yyline + 1, yycolumn + 1, true); }
    "false"     { return new Symbol(LangParserSym.FALSE, yyline + 1, yycolumn + 1, false); }

//    "data"      { return new Symbol(LangParserSym.DATA, yyline + 1, yycolumn + 1); }
//    "abstract"  { return new Symbol(LangParserSym.ABSTRACT, yyline + 1, yycolumn + 1); }
//    "if"        { return new Symbol(LangParserSym.IF, yyline + 1, yycolumn + 1); }
//    "else"      { return new Symbol(LangParserSym.ELSE, yyline + 1, yycolumn + 1); }
//    "return"    { return new Symbol(LangParserSym.RETURN, yyline + 1, yycolumn + 1); }
//    "read"      { return new Symbol(LangParserSym.READ, yyline + 1, yycolumn + 1); }
    "print"     { return new Symbol(LangParserSym.PRINT, yyline + 1, yycolumn + 1); }
    "iterate"   { return new Symbol(LangParserSym.ITERATE, yyline + 1, yycolumn + 1); }
//    "new"       { return new Symbol(LangParserSym.NEW, yyline + 1, yycolumn + 1); }
//    "null"      { return new Symbol(LangParserSym.NULL, yyline + 1, yycolumn + 1); }

    "=="        { return new Symbol(LangParserSym.EQUAL_EQUAL, yyline + 1, yycolumn + 1); }
    "!="        { return new Symbol(LangParserSym.NOT_EQUAL, yyline + 1, yycolumn + 1); }
    "&&"        { return new Symbol(LangParserSym.AND, yyline + 1, yycolumn + 1); }
    "::"        { return new Symbol(LangParserSym.DOUBLE_COLON, yyline + 1, yycolumn + 1); }
    ":"         { return new Symbol(LangParserSym.COLON, yyline + 1, yycolumn + 1); }
    ";"         { return new Symbol(LangParserSym.SEMICOLON, yyline + 1, yycolumn + 1); }
    ","         { return new Symbol(LangParserSym.COMMA, yyline + 1, yycolumn + 1); }
//    "."         { return new Symbol(LangParserSym.DOT, yyline + 1, yycolumn + 1); }
//    "="         { return new Symbol(LangParserSym.ASSIGN, yyline + 1, yycolumn + 1); }
    "+"         { return new Symbol(LangParserSym.PLUS, yyline + 1, yycolumn + 1); }
    "-"         { return new Symbol(LangParserSym.MINUS, yyline + 1, yycolumn + 1); }
    "*"         { return new Symbol(LangParserSym.MULT, yyline + 1, yycolumn + 1); }
    "/"         { return new Symbol(LangParserSym.DIV, yyline + 1, yycolumn + 1); }
    "%"         { return new Symbol(LangParserSym.MOD, yyline + 1, yycolumn + 1); }
    "<"         { return new Symbol(LangParserSym.LESS_THAN, yyline + 1, yycolumn + 1); }
//    ">"         { return new Symbol(LangParserSym.GREATER_THAN, yyline + 1, yycolumn + 1); }
    "!"         { return new Symbol(LangParserSym.NOT, yyline + 1, yycolumn + 1); }
    "("         { return new Symbol(LangParserSym.OPEN_PAREN, yyline + 1, yycolumn + 1); }
    ")"         { return new Symbol(LangParserSym.CLOSE_PAREN, yyline + 1, yycolumn + 1); }
//    "["         { return new Symbol(LangParserSym.OPEN_BRACKET, yyline + 1, yycolumn + 1); }
//    "]"         { return new Symbol(LangParserSym.CLOSE_BRACKET, yyline + 1, yycolumn + 1); }
    "{"         { return new Symbol(LangParserSym.OPEN_BRACE, yyline + 1, yycolumn + 1); }
    "}"         { return new Symbol(LangParserSym.CLOSE_BRACE, yyline + 1, yycolumn + 1); }

    {INT}       { return new Symbol(LangParserSym.INT, yyline + 1, yycolumn + 1, str2int(yytext())); }
    {FLOAT}     { return new Symbol(LangParserSym.FLOAT, yyline + 1, yycolumn + 1, str2float(yytext())); }
//    {CHAR}      { return new Symbol(LangParserSym.CHAR, yyline + 1, yycolumn + 1, yytext()); }

    {ID}        { return new Symbol(LangParserSym.ID, yyline + 1, yycolumn + 1, yytext()); }
//    {TYID}      { return new Symbol(LangParserSym.TYID, yyline + 1, yycolumn + 1, yytext()); }

    "--".* { /* ignora */ }
    "{-" [^]* "}-" { /* Ignora */ } 
    [ \t\n\r\f]+           { /* ignora */ }

    .                      { throw new RuntimeException("Token inesperado: \"" + yytext() + "\" na linha " + (yyline+1) + " coluna " + (yycolumn+1)); }
}