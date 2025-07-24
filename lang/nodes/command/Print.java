///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.command;


import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.LangVisitor;

public class Print extends CNode {

    private Exp e;

    public Print(int line, int col, Exp e){
        super(line,col);
        this.e = e;
    }

    public Exp getExp(){ return e;}

    public void accept(LangVisitor v){v.visit(this);}

}
