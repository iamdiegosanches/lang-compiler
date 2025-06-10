
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
            case PV: return "(" + line + " ," + column + ") ;";
            case MAIS: return "(" + line + " ," + column + ") +";
            case MENOS: return "(" + line + " ," + column + ") -";
            case VEZES: return "(" + line + " ," + column + ") *";
            case DIV: return "(" + line + " ," + column + ") /";
            case MOD: return "(" + line + " ," + column + ") %";
            case IGUAL: return "(" + line + " ," + column + ") =";
            case IGUALIGUAL: return "(" + line + " ," + column + ") ==";
            case DIF: return "(" + line + " ," + column + ") !=";
            case AND: return "(" + line + " ," + column + ") &&";
            case NOT: return "(" + line + " ," + column + ") !";
            case DP: return "(" + line + " ," + column + ") :";
            case DPTP: return "(" + line + " ," + column + ") ::";
            case PONTO: return "(" + line + " ," + column + ") .";
            case VIRG: return "(" + line + " ," + column + ") ,";
            case AP: return "(" + line + " ," + column + ") (";
            case FP: return "(" + line + " ," + column + ") )";
            case AC: return "(" + line + " ," + column + ") {";
            case FC: return "(" + line + " ," + column + ") }";
            case ABCOL: return "(" + line + " ," + column + ") [";
            case FECOL: return "(" + line + " ," + column + ") ]";
            case MENOR: return "(" + line + " ," + column + ") <";
            case MAIOR: return "(" + line + " ," + column + ") >";
            case EOF: return "(" + line + " ," + column + ") EOF";
            default: return "(" + line + " ," + column + ") " + tk;
        }
    }

}