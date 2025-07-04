package lang.nodes.expr;


public abstract class BinOp extends Exp {
      private Exp left, rigth;

      public BinOp(int line, int col, Exp el, Exp er){
           super(line,col);
           left = el;
           rigth = er;
      }

      public Exp getLeft(){return left;}
      public Exp getRight(){return rigth;}

}

