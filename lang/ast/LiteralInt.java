package lang.ast;

public class LiteralInt extends Expr {
    private int valor;
    public LiteralInt(int valor) { this.valor = valor; }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "LiteralInt(" + valor + ")");
    }
}