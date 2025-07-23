///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.decl;

import lang.nodes.CNode;


public abstract class Def extends CNode {
    public Def(int line, int col) {
        super(line, col);
    }
}