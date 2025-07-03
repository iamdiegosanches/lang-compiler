package lang.nodes.decl;

import lang.nodes.CNode;
import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;
import lang.nodes.expr.Var;
import lang.nodes.types.CType;
import lang.nodes.LangVisitor;

public class Bind extends CNode {

      private Var v;
      private CType t;

      public Bind(int line, int col, CType t, Var v){
          super(line,col);
          this.t = t;
          this.v = v;
      }

      public CType getType(){ return t;}
      public Var getVar(){ return v;}

      public void accept(LangVisitor v){v.visit(this);}
}
