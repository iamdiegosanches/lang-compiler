package lang.nodes.expr;

import lang.nodes.LangVisitor;
import lang.nodes.types.CType;

public class NewArray extends Exp {
    private CType type;
    private Exp sizeExp;

    public NewArray(int line, int col, CType type, Exp sizeExp) {
        super(line, col);
        this.type = type;
        this.sizeExp = sizeExp;
    }

    public CType getType() {
        return type;
    }

    public Exp getSizeExp() {
        return sizeExp;
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}