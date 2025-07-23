package lang.nodes.command;

import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.expr.LValue;
import lang.nodes.LangVisitor;

public class IterateWithVar extends CNode {

    private LValue iterVar;
    private Exp condExp;
    private CNode body;

    public IterateWithVar(int l, int c, LValue iterVar, Exp condExp, CNode body) {
        super(l, c);
        this.iterVar = iterVar;
        this.condExp = condExp;
        this.body = body;
    }

    public LValue getIterVar() {
        return iterVar;
    }

    public Exp getCondExp() {
        return condExp;
    }

    public CNode getBody() {
        return body;
    }

    public void setBody(CNode body) {
        this.body = body;
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}