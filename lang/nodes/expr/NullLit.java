///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
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
