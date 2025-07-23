///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;


import lang.nodes.LangVisitor;

public class IntLit extends Exp{

      private int value;
      public IntLit(int line, int col, int value){
           super(line,col);
           this.value = value;
      }

      public int getValue(){ return value;}

      public void accept(LangVisitor v){v.visit(this);}

}
