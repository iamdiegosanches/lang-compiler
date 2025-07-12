// lang.nodes.expr.Var.java
package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class Var extends Exp implements LValue {

      private String name;

      public Var(int line, int col, String name){
            super(line, col);
            this.name = name;
      }

      public String getName() {
            return name;
      }

      public void accept(LangVisitor v){
            v.visit(this);
      }

}