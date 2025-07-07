package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class NotEqual extends BinOp {
    
    public NotEqual(int line, int col, Exp el, Exp er) {
        super(line, col, el, er);
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }

}
