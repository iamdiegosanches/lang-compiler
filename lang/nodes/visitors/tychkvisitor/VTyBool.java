package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;

public class VTyBool extends VType {

     private static VTyBool instance = null;
     private VTyBool(){
        super(CLTypes.BOOL);
     }

     public static VTyBool newBool(){
         if(instance == null){
             instance = new VTyBool();
         }
         return instance;
     }

     public boolean match(VType t){ return getTypeValue() == t.getTypeValue();}

     public String toString(){ return "Bool";}
}
