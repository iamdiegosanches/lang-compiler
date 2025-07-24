///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class Not extends UnOp {

    public Not(int line, int col, Exp er) {
        super(line, col, er);
    }

    public void accept(LangVisitor v){v.visit(this);}

}
