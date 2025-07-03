package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;
import lang.nodes.dotutils.DotFile;

public class VTyInt extends VType {

     private static VTyInt instance = null;
     private VTyInt(){
        super(CLTypes.INT);
     }

     public static VTyInt newInt(){
         if(instance == null){
             instance = new VTyInt();
         }
         return instance;
     }

     public boolean match(VType t){ return getTypeValue() == t.getTypeValue();}

     public String toString(){ return "Int";}
}
