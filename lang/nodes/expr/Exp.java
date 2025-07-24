///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;

import lang.nodes.CNode;
import lang.nodes.LangVisitor;

public abstract class Exp extends CNode {

    public Exp(int l, int c){
        super(l,c);
    }

    public abstract void accept(LangVisitor v);

}
