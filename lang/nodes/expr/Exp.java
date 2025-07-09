package lang.nodes.expr;

import lang.nodes.CNode;
import lang.nodes.LangVisitor;

public abstract class Exp extends CNode {

    public Exp(int l, int c){
        super(l,c);
    }

    public abstract void accept(LangVisitor v);

}
