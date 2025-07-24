///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.decl;

import lang.nodes.CNode;
import lang.nodes.LangVisitor;
import lang.nodes.expr.Var;
import lang.nodes.types.CType;

public class Decl extends CNode {
    private Var var;
    private CType type;

    public Decl(int line, int col, Var var, CType type) {
        super(line, col);
        this.var = var;
        this.type = type;
    }

    public Var getVar() { return var; }
    public CType getType() { return type; }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}