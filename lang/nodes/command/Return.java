package lang.nodes.command;


import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;
import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.LangVisitor;

public class Return extends CNode {

      private Exp e;

      public Return(int line, int col, Exp e){
          super(line,col);
          this.e = e;
      }

      public Exp getExp(){ return e;}

      public void accept(LangVisitor v){v.visit(this);}


}
