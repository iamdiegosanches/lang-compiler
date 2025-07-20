package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;

public class VTyArr extends VType {

     private VType arg;


     public VTyArr(VType a){
        super(CLTypes.ARR);
        arg = a;
     }

     public VType getTyArg(){ return arg;}

     public boolean match(VType t){
          if (getTypeValue() == t.getTypeValue()){
              return arg.match( ((VTyArr)t).getTyArg());
          }
          return false;
     }
}
