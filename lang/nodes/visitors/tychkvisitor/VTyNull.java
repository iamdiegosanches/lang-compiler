///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.tychkvisitor;


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
