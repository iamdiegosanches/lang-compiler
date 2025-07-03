package lang.nodes.command;

import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;
import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.LangVisitor;

public class Loop extends CNode{

   private Exp cond;
   private CNode body;

   public Loop(int l, int c, Exp e, CNode body){
       super(l,c);
       cond = e;
       this.body = body;
   }

   public Exp getCond(){return cond;}
   public CNode getBody(){return body;}

   public void accept(LangVisitor v){v.visit(this);}



}
