package lang.nodes.decl;

import lang.nodes.CNode;
import lang.nodes.types.CType;
import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;
import java.util.ArrayList;
import lang.nodes.LangVisitor;

public class FunDef extends CNode{

   private String fname;
   private ArrayList<Bind> params;
   private CType ret;
   private CNode body;

   public FunDef(int l, int c, String s, ArrayList<Bind> params, CType ret, CNode body){
       super(l,c);
       fname = s;
       this.params = params;
       this.ret = ret;
       this.body = body;
   }

   public String getFname(){return fname;}
   public ArrayList<Bind>  getParams(){return params;}
   public CNode getBody(){return body;}
   public CType getRet(){return ret;}

   public void accept(LangVisitor v){v.visit(this);}

}
