package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class UMinus extends UnOp {

    public UMinus(int line, int col, Exp er) {
        super(line, col, er);
    }

    public void accept(LangVisitor v){v.visit(this);}

}
