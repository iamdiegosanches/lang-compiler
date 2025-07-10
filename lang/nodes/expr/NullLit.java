package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class NullLit extends Exp {

    public NullLit(int line, int col) {
        super(line, col);
    }

    @Override
    public void accept(LangVisitor v){ v.visit(this); }

    @Override
    public String toString(){ return "null";}
    
}
