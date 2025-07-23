///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.command;

import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.LangVisitor;

public class Loop extends CNode{

    private Exp cond;
    private CNode body;

    public Loop(int l, int c, Exp e, CNode body){
        super(l,c);
        cond = e;
        this.body = body;
    }

    public Exp getCond(){return cond;}
    public CNode getBody(){return body;}

    public void accept(LangVisitor v){v.visit(this);}

}