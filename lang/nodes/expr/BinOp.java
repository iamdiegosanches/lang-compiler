///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.expr;


public abstract class BinOp extends Exp {
      private Exp left, right;

      public BinOp(int line, int col, Exp el, Exp er){
           super(line,col);
           left = el;
           right = er;
      }

      public Exp getLeft(){return left;}
      public Exp getRight(){return right;}

}

