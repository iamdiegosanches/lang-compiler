///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import lang.BuildAstVisitor;
import lang.ast.NoAST;
import lang.parser.LangLexer;
import lang.parser.LangParser;

import java.io.IOException;

public class LangCompiler {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java LangCompiler <opção> <arquivo>");
            System.out.println("Opções:");
            System.out.println("  -lex: Realiza apenas a análise léxica e imprime os tokens.");
            System.out.println("  -syn: Realiza a análise sintática.");
            // System.out.println("  -ast: Cria a Árvore Sintática Abstrata (AST)."); // Opção nova/renomeada
            System.out.println("  -i: Executa a interpretação.");
            return;
        }

        String option = args[0];
        String filePath = args[1];

        try {
            // Cria o fluxo de caracteres a partir do arquivo
            CharStream input = CharStreams.fromFileName(filePath);
            
            // Cria o analisador léxico (Lexer)
            LangLexer lexer = new LangLexer(input);

            // Opção para análise léxica
            if (option.equals("-lex")) {
                // System.out.println("Realizando análise léxica para: " + filePath);
                org.antlr.v4.runtime.Token token;
                while ((token = lexer.nextToken()).getType() != org.antlr.v4.runtime.Token.EOF) {
                    // Usando o vocabulário do lexer para obter nomes simbólicos dos tokens
                    String tokenName = LangLexer.VOCABULARY.getSymbolicName(token.getType());
                    System.out.printf(
                        "(%d,%d) %s (%s)\n",
                        token.getLine(),
                        token.getCharPositionInLine(),
                        tokenName,
                        token.getText()
                    );
                }
                lexer.reset();
                System.out.println("Análise léxica concluída.");
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
                if (option.equals("-syn")) {
                    System.out.println("accepted");
                    System.out.println("Análise sintática concluída com sucesso.");
                }
                
                if (option.equals("-ast")) {
                    System.out.println("Análise sintática concluída. Construindo e exibindo a AST...");
    
                    BuildAstVisitor astVisitor = new BuildAstVisitor();
                    NoAST astRoot = astVisitor.visit(tree);
                    
                    // A mágica acontece aqui!
                    if(astRoot != null) {
                        astRoot.print(""); // Imprime a AST a partir da raiz
                    } else {
                        System.err.println("Erro: A raiz da AST é nula.");
                    }
                }

                if (option.equals("-i")) {
                    System.out.println("Interpretação ainda não implementada.");
                    // AQUI VOCÊ TERIA A LÓGICA DE INTERPRETAÇÃO, POSSIVELMENTE USANDO UM VISITOR DIFERENTE SOBRE A AST
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
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
        System.err.printf("Erro sintático na linha %d:%d - %s\n", line, charPositionInLine, msg);
    }

    public boolean hasErrors() {
        return hasErrors;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
}