///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class ArrayAccess extends Exp implements LValue {
    private LValue arrayVar;
    private Exp indexExp;

    public ArrayAccess(int line, int col, LValue arrayVar, Exp indexExp) {
        super(line, col);
        this.arrayVar = arrayVar;
        this.indexExp = indexExp;
    }

    public LValue getArrayVar() {
        return arrayVar;
    }

    public Exp getIndexExp() {
        return indexExp;
    }

    @Override
    public String getName() {
        return arrayVar.getName() + "[" + indexExp.toString() + "]";
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }

}