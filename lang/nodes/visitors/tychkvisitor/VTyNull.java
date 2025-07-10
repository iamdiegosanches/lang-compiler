package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;

public class VTyNull extends VType {

    private static VTyNull instance = null;

    private VTyNull() {
        super(CLTypes.NULL);
    }

    public static VTyNull newNull() {
        if (instance == null) {
            instance = new VTyNull();
        }
        return instance;
    }

    @Override
    public boolean match(VType t) {
        return t.getTypeValue() != CLTypes.INT &&
                 t.getTypeValue() != CLTypes.FLOAT &&
                 t.getTypeValue() != CLTypes.BOOL &&
                 t.getTypeValue() != CLTypes.CHAR &&
                 t.getTypeValue() != CLTypes.ERR;
    }

    @Override
    public String toString() { return "Null"; }
}
