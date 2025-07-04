package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class Div extends BinOp {

      public Div(int line, int col, Exp el, Exp er){
           super(line,col,el,er);

      }

      public void accept(LangVisitor v){v.visit(this);}
}


