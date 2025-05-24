public class Token {
    public int line;
    public int cool;
    public TokenType type;
    public String lexeme;
    
    public enum TokenType {
        DATA, IF, ELSE, ITERATE, READ, PRINT, RETURN, NEW,
        
        INT_TYPE, CHAR_TYPE, BOOL_TYPE, FLOAT_TYPE,
        
        INT_LITERAL, FLOAT_LETERAL, CHAR_LITERAL, TRUE, FLASE, NULL,
        
        ID,
        TYID,
        
        ASSIGN,       // =
        PLUS,         // +
        MINUS,        // -
        MULTIPLY,     // *
        DIVIDE,       // /
        MODULO,       // %
        AND,          // &&
        NOT,          // !
        LESS_THAN,    // <
        EQUALS,       // ==
        NOT_EQUALS,   // !=
        LPAREN,       // (
        RPAREN,       // )
        LBRACK,       // [
        RBRACK,       // ]
        LBRACE,       // {
        RBRACE,       // }
        COMMA,        // ,
        SEMICOLON,    // ;
        DOT,          // .
        
        EOF
    }
    
    public Token(TokenType type, int line, int col, String lexeme) {
        this.type = type;
        this.line = line;
        this.col = col;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        if (type == TokenType.ID || type == TokenType.TYID || type == TokenType.INT_LITERAL || type == TokenType.FLOAT_LITERAL) {
            return type + ":" + lexeme;
        }
        return type.toString();
    }
}
