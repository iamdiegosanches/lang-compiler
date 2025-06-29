///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import lang.LangInterpreterVisitor;
import lang.parser.LangLexer;
import lang.parser.LangParser;

import java.io.IOException;

public class LangCompiler {

    public static void main(String[] args) {
        String option;
        String filePath;

        if (args.length == 0) {
            printHelp();
            return;
        }

        if (args.length == 1) {
            option = "-i";
            filePath = args[0];
        } else {
            option = args[0];
            filePath = args[1];
        }

        try {
            CharStream input = CharStreams.fromFileName(filePath);
            LangLexer lexer = new LangLexer(input);

            if (option.equals("-lex")) {
                processLexer(lexer);
                return;
            }

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LangParser parser = new LangParser(tokens);

            SyntaxErrorListener errorListener = new SyntaxErrorListener();
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            ParseTree tree = parser.prog();

            if (errorListener.hasErrors()) {
                System.out.println("rejected");
                System.err.println("A análise sintática falhou com " + errorListener.getErrorCount() + " erro(s).");
            } else {
                switch (option) {
                    case "-syn":
                        System.out.println("accepted");
                        break;
                    case "-i":
                        try {
                            LangInterpreterVisitor interpreter = new LangInterpreterVisitor();
                            interpreter.visit(tree);
                        } catch (Exception e) {
                            System.err.println("Erro de execução: " + e.getMessage());
                        }
                        break;
                    default:
                        System.err.println("Opção '" + option + "' inválida.");
                        printHelp();
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    private static void printHelp() {
        System.out.println("Uso: java LangCompiler [<opção>] <arquivo>");
        System.out.println("Opções:");
        System.out.println("  -lex: Realiza a análise léxica e imprime os tokens encontrados. [cite: 435]");
        System.out.println("  -syn: Realiza a análise sintática e verifica se o programa é válido. [cite: 421]");
        System.out.println("  -i  : Executa o interpretador para o programa (ação padrão). [cite: 422]");
    }

    private static void processLexer(LangLexer lexer) {
        org.antlr.v4.runtime.Token token;
        while ((token = lexer.nextToken()).getType() != org.antlr.v4.runtime.Token.EOF) {
            String tokenName = LangLexer.VOCABULARY.getSymbolicName(token.getType());
            System.out.printf(
                "(%d,%d) %s (%s)\n",
                token.getLine(),
                token.getCharPositionInLine() + 1,
                tokenName,
                token.getText()
            );
        }
    }
}

class SyntaxErrorListener extends BaseErrorListener {
    private boolean hasErrors = false;
    private int errorCount = 0;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        hasErrors = true;
        errorCount++;
        // Silencia a mensagem de erro padrão do ANTLR para focar na saída "rejected".
        // Você pode descomentar a linha abaixo para depuração.
        // System.err.printf("Erro sintático na linha %d:%d - %s\n", line, charPositionInLine + 1, msg);
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public int getErrorCount() {
        return errorCount;
    }
}