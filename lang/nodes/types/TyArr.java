///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.types;

import lang.nodes.LangVisitor;

public class TyArr extends CType {
    private CType elementType;

    public TyArr(int line, int col, CType elementType) {
        super(line, col);
        this.elementType = elementType;
    }

    public CType getElementType() {
        return elementType;
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        if (elementType == null) {
            return "[]"; 
        }
        return elementType.toString() + "[]";
    }
}