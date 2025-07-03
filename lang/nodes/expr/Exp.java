package lang.nodes.expr;


import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;
import lang.nodes.LangVisitor;

import lang.nodes.CNode;

public abstract class Exp extends CNode {

      public Exp(int l, int c){
          super(l,c);
      }

}
