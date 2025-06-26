package lang.ast;

import java.util.List;

public class Prog extends NoAST{
    private List<Funcao> funcs;
    public Prog(List<Funcao> funcs) { this.funcs = funcs; }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Prog");
        for (Funcao f : funcs) {
            f.print(prefix + "  ");
        }
    }
}
