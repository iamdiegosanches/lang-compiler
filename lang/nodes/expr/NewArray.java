package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class NewArray extends Exp {
    private Exp sizExp;

    public NewArray(int line, int col, Exp sizExp) {
        super(line, col);
        this.sizExp = sizExp;
    }

    public Exp getSizeExp() {
        return sizExp;
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}
