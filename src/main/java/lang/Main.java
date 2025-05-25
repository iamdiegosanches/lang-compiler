import java.io.FileReader;
import java.io.Reader;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso: java Main <arquivo-de-entrada>");
            System.exit(1);
        }

        try (Reader reader = new FileReader(args[0])) {
            Lexer lexer = new Lexer(reader);
            Token token;
            
            // Usando next_token() por causa da interface do java_cup.runtime.Scanner
            while ((token = lexer.next_token()).type != Token.TokenType.EOF) {
                System.out.println(token.toString());
            }
            // Imprime o token EOF no final
            System.out.println(token.toString()); 

        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}
