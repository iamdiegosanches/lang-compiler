///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.types;


import lang.nodes.LangVisitor;

public class TyFloat extends CType {

    public TyFloat(int line, int col){
        super(line,col);
    }

    public void accept(LangVisitor v){v.visit(this);}

}
