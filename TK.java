///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////


public enum TK {
    // Literals
    INT,
    FLOAT,
    CHAR,
    NULL,
    BOOL,

    TRUE,
    FALSE,

    // Identifiers
    ID,       // identificadores normais (começam com minúscula)
    TYID,     // nomes de tipo (começam com maiúscula)

    // Keywords
    DATA,
    ABSTRACT,
    IF,
    ELSE,
    RETURN,
    READ,
    PRINT,
    ITERATE,
    NEW,

    // Symbols and Operators
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

    // End of File
    EOF
}