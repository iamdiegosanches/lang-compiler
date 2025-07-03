package lang.nodes.expr;


import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;
import lang.nodes.LangVisitor;

public class FloatLit extends Exp{

      private float value;
      public FloatLit(int line, int col, float value){
           super(line,col);
           this.value = value;
      }

      public float getValue(){ return value;}


      public void accept(LangVisitor v){v.visit(this);}


}
