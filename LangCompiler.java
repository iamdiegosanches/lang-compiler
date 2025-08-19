///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
import java.io.*;
import java_cup.runtime.*;
import lang.nodes.CNode;
import lang.parser.LangParser;
import lang.parser.LangLexer;
import lang.parser.LangParserSym;
import lang.nodes.visitors.InterpVisitor;
import lang.nodes.visitors.tychkvisitor.TyChecker;
import lang.nodes.visitors.CodeGenVisitor;
import java.util.Hashtable;
import lang.nodes.visitors.tychkvisitor.VType;

public class LangCompiler {

      public static void runLexer(LangLexer lex) throws IOException, Exception {
            Symbol tk = lex.nextToken();
            while (tk.sym != LangParserSym.EOF) {
                  System.out.println("(" + tk.left + "," + tk.right + ")" + tk.sym);
                  tk = lex.nextToken();
            }
            System.out.println(tk.toString());
      }

      public static TyChecker typeCheck(CNode root) {
            if (root == null) return null;
            try {
                TyChecker tv = new TyChecker();
                root.accept(tv);
                System.out.println("well-typed");
                return tv;
            } catch (RuntimeException e) {
                System.out.println("ill-typed");
                System.err.println("Erro: " + e.getMessage());
                return null;
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
        }
    }

    public static void compile(LangParser p, String inputFileName) throws IOException, Exception {
        Symbol presult = p.parse();
        CNode root = (CNode) presult.value;
        if (root != null) {
            TyChecker typeChecker = new TyChecker();
            try {
                 root.accept(typeChecker);
            } catch (RuntimeException e) {
                System.err.println("Erro de tipo, não é possível compilar: " + e.getMessage());
                return;
            }
            
            Hashtable<CNode, VType> typeMap = typeChecker.getTypeMap();
            CodeGenVisitor codeGen = new CodeGenVisitor(typeMap);
            root.accept(codeGen);

            String cCode = codeGen.getCode();
            
            // Cria o arquivo de saída .c
            String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".c";
            try (FileWriter writer = new FileWriter(outputFileName)) {
                writer.write(cCode);
                System.out.println("Código C gerado com sucesso em: " + outputFileName);
            } catch (IOException e) {
                System.err.println("Erro ao escrever o arquivo de saída: " + e.getMessage());
            }
        } else {
            System.out.println("root was null !");
        }
    }

      public static void main(String args[]) throws IOException, Exception {
            int fname = 0;
            if (args.length < 1 || args.length > 2) {
                  System.out.println("use java LangCompiler [opcao] <nome-de-arquivo>");
                  System.out.println("opcao: ");
                  System.out.println("   -lex      : lista os tokens");
                  System.out.println("   -i        : Interpreta o programa.");
                  System.out.println("   -ty       : Verifica Tipos.");
                  System.out.println("   -syn      : Executa o analisador sintático.");
                  System.out.println("   -compile  : Compila o programa para a linguagem C.");
                  System.exit(0);
            }

            if (args.length == 2) {
                fname = 1;
            }
            
            String inputFileName = args[fname];
            LangLexer lex = new LangLexer(new FileReader(inputFileName));
            LangParser p = new LangParser(lex);

            String option = args.length == 2 ? args[0] : "-i"; // Default to interpret if no option

            switch(option) {
                case "-lex":
                    runLexer(lex);
                    break;
                case "-ty":
                    CNode root_ty = (CNode) p.parse().value;
                    typeCheck(root_ty);
                    break;
                case "-i":
                    interpret(p);
                    break;
                case "-syn":
                    runSyntaxCheck(p);
                    break;
                case "-compile":
                    compile(p, inputFileName);
                    break;
                default:
                     if (args.length == 1) { // Case where only filename is provided
                        p = new LangParser(new LangLexer(new FileReader(args[0])));
                        interpret(p);
                    } else {
                         System.out.println("Opção inválida: " + option);
                    }
                    break;
            }
      }
}