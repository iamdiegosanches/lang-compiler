///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class And extends BinOp {

    public And(int line, int col, Exp el, Exp er) {
        super(line, col, el, er);
    }
    
    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}
