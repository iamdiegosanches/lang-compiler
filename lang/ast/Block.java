package lang.ast;

import java.util.List;

public class Block extends NoAST {
    private List<Comando> comandos;
    public Block(List<Comando> comandos) { this.comandos = comandos; }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "Block");
        for (Comando c : comandos) {
            c.print(prefix + "  ");
        }
    }
}