package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;

public class VTyChar extends VType {

     private static VTyChar instance = null;
     private VTyChar(){
        super(CLTypes.CHAR);
     }

     public static VTyChar newChar(){
         if(instance == null){
             instance = new VTyChar();
         }
         return instance;
     }

     public boolean match(VType t){ return getTypeValue() == t.getTypeValue();}

     @Override
     public String toString(){ return "Char";}
}