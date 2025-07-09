package lang.nodes.command;

import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.expr.LValue;
import lang.nodes.types.CType;
import lang.nodes.LangVisitor;

public class CDecl extends CNode {

    private CType type;
    private LValue var;
    private Exp exp;

    public CDecl(int line, int col, CType type, LValue var, Exp exp) {
        super(line, col);
        this.type = type;
        this.var = var;
        this.exp = exp;
    }

    public CType getType() {
        return type;
    }
    
    public LValue getVar() {
        return var;
    }

    public Exp getExp() {
        return exp;
    }

    public void accept(LangVisitor v) {
        v.visit(this);
    }
}