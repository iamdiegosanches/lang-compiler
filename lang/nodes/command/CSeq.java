///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.command;


import lang.nodes.CNode;
import lang.nodes.LangVisitor;


public class CSeq extends CNode {

    private CNode left;
    private CNode right;

    public CSeq(int line, int col, CNode l, CNode r){
        super(line,col);
        left = l;
        right = r;
    }

    public CNode getLeft(){ return left;}
    public CNode getRight(){ return right;}

    public void accept(LangVisitor v){v.visit(this);}
}
