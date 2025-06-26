package lang.ast;

public class CmdPrint extends Comando {
    private Expr exp;
    public CmdPrint(Expr exp) { this.exp = exp; }

    @Override
    public void print(String prefix) {
        System.out.println(prefix + "CmdPrint");
        exp.print(prefix + "  ");
    }
}
