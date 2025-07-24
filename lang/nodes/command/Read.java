///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
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
