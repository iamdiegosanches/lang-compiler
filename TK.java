public enum TK {
    // Literais
    INT,
    FLOAT,
    CHAR,
    BOOL,
    NULL,

    // Identificadores
    ID,       // identificadores normais (começam com minúscula)
    TYID,     // nomes de tipo (começam com maiúscula)

    // Palavras-chave
    DATA,
    ABSTRACT,
    IF,
    ELSE,
    RETURN,
    READ,
    PRINT,
    ITERATE,
    NEW,

    // Símbolos e Operadores
    PV,       // ;
    DP,       // :
    DPTP,     // ::
    VIRG,     // ,
    AP,       // (
    FP,       // )
    AC,       // {
    FC,       // }
    MENOR,    // <
    MAIOR,    // >
    MAIS,     // +
    MENOS,    // -
    VEZES,    // *
    DIV,      // /
    MOD,      // %
    IGUAL,    // =
    DIF,      // !=
    IGUALIGUAL, // ==
    AND,      // &&
    NOT,      // !
    PONTO,    // .
    ABCOL,    // [
    FECOL,    // ]

    // Fim de arquivo
    EOF
}
