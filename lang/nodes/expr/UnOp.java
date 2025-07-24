///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;

public abstract class UnOp extends Exp {
    private Exp right;

    public UnOp(int line, int col, Exp er) {
        super(line, col);
        right = er;
    }

    public Exp getRight() {
        return right;
    }

}
