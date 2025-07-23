///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.types;

import lang.nodes.LangVisitor;

public class TyUser extends CType {
    private String name;

    public TyUser(int line, int col, String name) {
        super(line, col);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return name;
    }
}