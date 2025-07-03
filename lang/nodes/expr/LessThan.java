package lang.nodes.expr;

import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;
import lang.nodes.LangVisitor;

public class LessThan extends BinOp {
    public LessThan(int line, int col, Exp el, Exp er) {
        super(line, col, el, er);
    }
    public void accept(LangVisitor v) { v.visit(this); }
}

