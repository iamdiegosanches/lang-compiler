///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;


import lang.nodes.LangVisitor;

public class FloatLit extends Exp{

      private float value;
      public FloatLit(int line, int col, float value){
           super(line,col);
           this.value = value;
      }

      public float getValue(){ return value;}


      public void accept(LangVisitor v){v.visit(this);}


}
