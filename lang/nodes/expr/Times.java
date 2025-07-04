package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class Times extends BinOp {

      public Times(int line, int col, Exp el, Exp er){
           super(line,col,el,er);
      }

      public void accept(LangVisitor v){v.visit(this);}
}
