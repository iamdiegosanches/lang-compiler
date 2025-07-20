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