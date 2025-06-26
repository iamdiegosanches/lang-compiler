package lang.ast;

public class ExprBinaria extends Expr {
    private Expr esq;
    private String op;
    private Expr dir;
    
    public ExprBinaria(Expr esq, String op, Expr dir) {
        this.esq = esq;
        this.op = op;
        this.dir = dir;
    }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "ExprBin(" + op + ")");
        esq.print(prefix + "  ");
        dir.print(prefix + "  ");
    }
}