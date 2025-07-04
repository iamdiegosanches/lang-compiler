package lang.nodes.expr;


import lang.nodes.LangVisitor;

public class BoolLit extends Exp{

      private boolean value;
      public BoolLit(int line, int col, boolean value){
           super(line,col);
           this.value = value;
      }

      public boolean getValue(){ return value;}


      public void accept(LangVisitor v){v.visit(this);}

}
