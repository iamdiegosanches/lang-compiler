package lang.nodes.command;

import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;
import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.expr.Var;
import lang.nodes.LangVisitor;

public class CAttr extends CNode {

      private Var v;
      private Exp e;

      public CAttr(int line, int col, Var v, Exp e){
          super(line,col);
          this.v = v;
          this.e = e;
      }

      public Exp getExp(){ return e;}
      public Var getVar(){ return v;}

     public void accept(LangVisitor v){v.visit(this);}


}
