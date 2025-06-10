import java.io.FileReader;

public class LangCompiler {

    public static void main(String[] args) throws Exception {
        if (args.length == 2 && args[0].equals("-lex")) {
            LangLex lex = new LangLex(new FileReader(args[1]));
            Token t = lex.nextToken();
            while (t.tk != TK.EOF) {
                System.out.println(t.toString());
                t = lex.nextToken();
            }
            System.out.println(t.toString());
        } else if (args.length == 1) {
            LangLex lex = new LangLex(new FileReader(args[0]));
            while (lex.nextToken().tk != TK.EOF);
        } else {
            System.out.println("Uso: java LangCompiler [-lex] <arquivo>");
        }
    }
}
