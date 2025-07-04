package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class Sub extends BinOp {

      public Sub(int line, int col, Exp el, Exp er){
           super(line,col,el,er);
      }

      public void accept(LangVisitor v){v.visit(this);}

}


