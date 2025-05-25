package lang;

import java.io.FileReader;
import java.io.Reader;
import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso: java -cp build/classes:lib/jflex.jar lang.Main <arquivo-de-entrada>");
            System.exit(1);
        }

        try (Reader reader = new FileReader(args[0])) {
            Lexer lexer = new Lexer(reader);
            Symbol symbol;

            // Loop até encontrar o token EOF
            do {
                symbol = lexer.next_token();
                Token token = (Token) symbol.value; // Extrai nosso Token do Symbol
                if (token != null) {
                    System.out.println(token.toString());
                }
            } while (symbol.sym != Token.TokenType.EOF.ordinal());

        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
