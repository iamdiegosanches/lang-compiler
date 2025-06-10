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

    private Boolean str2bool(String s) {
        try {
            return Boolean.parseBoolean(s);
        } catch(Exception e) {
            System.out.println("Erro ao converter bool: " + s);
        }
        return null;
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

%%
<YYINITIAL>{
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
    "=="        { return mkTk(TK.IGUALIGUAL); }
    "!="        { return mkTk(TK.DIF); }
    "&&"        { return mkTk(TK.AND); }
    "::"        { return mkTk(TK.DPTP); }
    ":"         { return mkTk(TK.DP); }
    ";"         { return mkTk(TK.PV); }
    ","         { return mkTk(TK.VIRG); }
    "."         { return mkTk(TK.PONTO); }
    "="         { return mkTk(TK.IGUAL); }
    "+"         { return mkTk(TK.MAIS); }
    "-"         { return mkTk(TK.MENOS); }
    "*"         { return mkTk(TK.VEZES); }
    "/"         { return mkTk(TK.DIV); }
    "%"         { return mkTk(TK.MOD); }
    "<"         { return mkTk(TK.MENOR); }
    ">"         { return mkTk(TK.MAIOR); }
    "!"         { return mkTk(TK.NOT); }
    "("         { return mkTk(TK.AP); }
    ")"         { return mkTk(TK.FP); }
    "["         { return mkTk(TK.ABCOL); }
    "]"         { return mkTk(TK.FECOL); }
    "{"         { return mkTk(TK.AC); }
    "}"         { return mkTk(TK.FC); }

    // Literais
    {INT}       { return mkTk(TK.INT, str2int(yytext())); }
    {FLOAT}     { return mkTk(TK.FLOAT, str2float(yytext())); }
    {CHAR}      { return mkTk(TK.CHAR, yytext()); }
    "true"      { return mkTk(TK.BOOL, str2bool(yytext())); }
    "false"     { return mkTk(TK.BOOL, str2bool(yytext())); }

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
