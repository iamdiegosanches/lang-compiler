///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////

public class Token {
    public Object value;
    public TK tk;
    public int line, column;

    public Token(TK tk, int line, int column){
        this.tk = tk;
        this.line = line;
        this.column = column;
    }

    public Token(TK tk, int line, int column, Object val){
        this(tk,line,column);
        value = val;
    }

    @Override
    public String toString() {
        if (value != null)
            return "(" + line + " ," + column + ") " + tk + " " + value;
        
        switch (tk) {
            case SEMICOLON: return "(" + line + " ," + column + ") ;";
            case PLUS: return "(" + line + " ," + column + ") +";
            case MINUS: return "(" + line + " ," + column + ") -";
            case MULT: return "(" + line + " ," + column + ") *";
            case DIV: return "(" + line + " ," + column + ") /";
            case MOD: return "(" + line + " ," + column + ") %";
            case ASSIGN: return "(" + line + " ," + column + ") =";
            case EQUAL_EQUAL: return "(" + line + " ," + column + ") ==";
            case NOT_EQUAL: return "(" + line + " ," + column + ") !=";
            case AND: return "(" + line + " ," + column + ") &&";
            case NOT: return "(" + line + " ," + column + ") !";
            case COLON: return "(" + line + " ," + column + ") :";
            case DOUBLE_COLON: return "(" + line + " ," + column + ") ::";
            case DOT: return "(" + line + " ," + column + ") .";
            case COMMA: return "(" + line + " ," + column + ") ,";
            case OPEN_PAREN: return "(" + line + " ," + column + ") (";
            case CLOSE_PAREN: return "(" + line + " ," + column + ") )";
            case OPEN_BRACE: return "(" + line + " ," + column + ") {";
            case CLOSE_BRACE: return "(" + line + " ," + column + ") }";
            case OPEN_BRACKET: return "(" + line + " ," + column + ") [";
            case CLOSE_BRACKET: return "(" + line + " ," + column + ") ]";
            case LESS_THAN: return "(" + line + " ," + column + ") <";
            case GREATER_THAN: return "(" + line + " ," + column + ") >";
            case EOF: return "(" + line + " ," + column + ") EOF";
            default: return "(" + line + " ," + column + ") " + tk;
        }
    }

}