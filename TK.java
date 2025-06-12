///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////


public enum TK {
    INT,
    FLOAT,
    CHAR,
    NULL,
    TRUE,
    FALSE,

    // Type
    INT_TYPE,
    CHAR_TYPE,
    BOOL_TYPE,
    FLOAT_TYPE,

    // Itendificadores
    ID,       // identificadores normais (começam com minúscula)
    TYID,     // nomes de tipo (começam com maiúscula)

    // Palavras chave
    DATA,
    ABSTRACT,
    IF,
    ELSE,
    RETURN,
    READ,
    PRINT,
    ITERATE,
    NEW,

    // Simbolos e operadores
    SEMICOLON, // ;
    COLON,     // :
    DOUBLE_COLON, // ::
    COMMA,     // ,
    OPEN_PAREN, // (
    CLOSE_PAREN, // )
    OPEN_BRACE, // {
    CLOSE_BRACE, // }
    LESS_THAN,    // <
    GREATER_THAN, // >
    PLUS,     // +
    MINUS,    // -
    MULT, // *
    DIV,   // /
    MOD,   // %
    ASSIGN,   // =
    NOT_EQUAL, // !=
    EQUAL_EQUAL, // ==
    AND,      // &&
    NOT,      // !
    DOT,      // .
    OPEN_BRACKET, // [
    CLOSE_BRACKET, // ]

    // Fim
    EOF
}