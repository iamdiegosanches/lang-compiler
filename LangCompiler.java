///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
import java.io.*;
import java_cup.runtime.*;
import lang.nodes.CNode;
import lang.nodes.environment.Env;
import lang.parser.LangParser;
import lang.parser.LangLexer;
import lang.parser.LangParserSym;
import lang.nodes.visitors.SimpleVisitor;
import lang.nodes.visitors.InterpVisitor;
import lang.nodes.visitors.tychkvisitor.TyChecker;

public class LangCompiler {

      public static void runLexer(LangLexer lex) throws IOException, Exception {
            Symbol tk = lex.nextToken();
            while (tk.sym != LangParserSym.EOF) {
                  System.out.println("(" + tk.left + "," + tk.right + ")" + tk.sym);
                  tk = lex.nextToken();
            }
            System.out.println(tk.toString());
      }

      public static void interpretAndType(LangParser p) throws IOException, Exception {
            Symbol presult = p.parse();
            CNode root = (CNode) presult.value;
            if (root != null) {
                try {
                    TyChecker tv = new TyChecker();
                    tv.enterScope();
                    root.accept(tv);
                    tv.leaveScope();
                    System.out.println("well-typed");
                } catch (RuntimeException e) {
                    System.out.println("ill-typed");
                    System.err.println("Erro: " + e.getMessage());
                }
            } else {
                  System.out.println("root was null !");
            }
      }

      public static void interpret(LangParser p) throws IOException, Exception {
            Symbol presult = p.parse();
            CNode root = (CNode) presult.value;
            if (root != null) {
                  InterpVisitor v = new InterpVisitor();
                  root.accept(v);
            } else {
                  System.out.println("root was null !");
            }
      }

      public static void runSyntaxCheck(LangParser p) {
        try {
            p.parse();
            System.out.println("accepted");
        } catch (Exception e) {
            System.out.println("rejected");
            // e.printStackTrace();
        }
    }

      public static void main(String args[]) throws IOException, Exception {
            int fname = 0;
            if (args.length < 1 || args.length > 2) {
                  System.out.println("use java LangCompiler [opcao] <nome-de-arquivo>");
                  System.out.println("opcao: ");
                  System.out.println("   -lex  : lista os toke ");
                  System.out.println("   -i    : Interpreta o programa.");
                  System.out.println("   -ty    : Verifica Tipos.");
                  System.out.println("   -id   : Interpreta o programa e imprime a tabela de ambiente de execução.");
                  System.out.println("   -syn  : Executa o analisador sintático e imprime \"accepted\" ou \"rejected\".");
                  // System.out.println(" :");
                  System.exit(0);
            } else {
                  if (args.length == 2) {
                        fname = 1;
                  }
                  LangLexer lex = new LangLexer(new FileReader(args[fname]));
                  LangParser p = new LangParser(lex);
                  if (args.length == 2 && args[0].equals("-lex")) {
                        runLexer(lex);
                        System.exit(0);
                  } else if(args.length == 2 && args[0].equals("-ty")){
                        interpretAndType(p);
                        System.exit(0);
                  }
                  else if (args.length == 2 && args[0].equals("-i")) {
                        interpret(p);
                        System.exit(0);
                  } else if(args.length == 2 && args[0].equals("-syn")){
                        runSyntaxCheck(p);
                        System.exit(0);
                  }
            }
      }
}