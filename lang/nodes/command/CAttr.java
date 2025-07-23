package lang.nodes.command;

import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.expr.LValue;
import lang.nodes.LangVisitor;

public class CAttr extends CNode {

      private LValue v;
      private Exp e;

      public CAttr(int line, int col, LValue v, Exp e){
          super(line,col);
          this.v = v;
          this.e = e;
      }

      public Exp getExp(){ return e;}
      public LValue getVar(){ return v;}

     public void accept(LangVisitor v){v.visit(this);}


}
