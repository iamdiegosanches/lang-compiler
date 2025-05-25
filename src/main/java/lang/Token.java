package lang;

import java_cup.runtime.Symbol;

public class Token extends Symbol {
    public int line;
    public int col;
    public TokenType type;
    public String lexeme;

    // Enumeração completa de todos os tipos de token
    public enum TokenType {
        // Palavras-chave
        DATA, IF, ELSE, ITERATE, READ, PRINT, RETURN, NEW,
        
        // Tipos Primitivos
        INT_TYPE, CHAR_TYPE, BOOL_TYPE, FLOAT_TYPE,

        // Literais
        INT_LITERAL, FLOAT_LITERAL, CHAR_LITERAL, TRUE, FALSE, NULL,

        // Identificadores
        ID, TYID,

        // Operadores e Pontuação
        ASSIGN, PLUS, MINUS, MULTIPLY, DIVIDE, MODULO, AND, NOT,
        LESS_THAN, EQUALS, NOT_EQUALS, LPAREN, RPAREN, LBRACK, RBRACK,
        LBRACE, RBRACE, COMMA, SEMICOLON, DOT,

        // Fim de Arquivo
        EOF, ERROR
    }

    public Token(TokenType type, int line, int col, String lexeme) {
        super(type.ordinal(), null); // Chama o construtor da superclasse Symbol
        this.type = type;
        this.line = line;
        this.col = col;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        // Formato: TIPO_TOKEN(linha, coluna): "lexema"
        String base = String.format("%s(%d, %d): \"%s\"", type, line, col, lexeme);

        // O formato de saída solicitado no PDF é um pouco diferente
        // Vamos adaptar para ficar igual ao exemplo: ID:main, INT:10, RETURN, ;
        switch (type) {
            case ID:
            case TYID:
            case INT_LITERAL:
            case FLOAT_LITERAL:
                return type + ":" + lexeme;
            case RETURN:
            case IF:
                // ... e outras palavras-chave, se precisar de formatação especial
                return type.toString();
            default:
                // Para operadores e pontuação, o próprio lexema é suficiente
                return lexeme;
        }
    }
}
