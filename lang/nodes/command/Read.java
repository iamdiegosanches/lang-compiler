package lang.nodes.command;

import lang.nodes.CNode;
import lang.nodes.LangVisitor;
import lang.nodes.expr.LValue;

public class Read extends CNode{
    private LValue target;

    public Read(int line, int col, LValue target) {
        super(line, col);
        this.target = target;
    }

    public LValue getTarget() { return target; }

    public void accept(LangVisitor v){ v.visit(this); }
}
