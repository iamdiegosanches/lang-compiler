package lang.nodes.visitors.tychkvisitor;

// Mantém os imports existentes
import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;
import lang.nodes.dotutils.DotFile;

public class VTyArr extends VType {

     private VType arg;

     public VTyArr(VType a){
        super(CLTypes.ARR);
        this.arg = a;
     }

     public VType getTyArg(){ return arg;}

     public void setTyArg(VType newArg) {
         this.arg = newArg;
     }

     @Override
     public boolean match(VType t){
          if (t.getTypeValue() == CLTypes.ERR) {
              return false;
          }

          // Um tipo de array pode sempre ser atribuído a um literal 'null'.
          if (t.getTypeValue() == CLTypes.NULL) {
              return true;
          }

          if (this.getTypeValue() == CLTypes.ARR && t.getTypeValue() == CLTypes.ARR){
              VTyArr otherArr = (VTyArr)t;

              if (this.arg.getTypeValue() == CLTypes.UNDETERMINED) {
                  return true;
              }
              if (otherArr.arg.getTypeValue() == CLTypes.UNDETERMINED) {
                  return true;
              }
              return this.arg.match(otherArr.arg);
          }
          return false;
     }

     @Override
     public String toString(){
        if (arg.getTypeValue() == CLTypes.UNDETERMINED) {
            return "Any[]";
        } else if (arg.getTypeValue() == CLTypes.NULL) {
             return "Null[]";
        }
        return arg.toString() + "[]";
     }
}