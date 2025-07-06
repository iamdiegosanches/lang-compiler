package lang.nodes.command;

import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.expr.Var;
import lang.nodes.LangVisitor;

public class IterateWithVar extends CNode {

    private Var iterVar;
    private Exp condExp; // A expressão que define a iteração (int ou array)
    private CNode body;

    public IterateWithVar(int l, int c, Var iterVar, Exp condExp, CNode body) {
        super(l, c);
        this.iterVar = iterVar;
        this.condExp = condExp;
        this.body = body;
    }

    public Var getIterVar() {
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