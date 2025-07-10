// lang.nodes.expr.Var.java
package lang.nodes.expr;

import lang.nodes.LangVisitor;
import lang.nodes.visitors.GVizVisitor; // Import GVizVisitor

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

      @Override
      public void accept(GVizVisitor v) {
            v.visit(this);
      }
}