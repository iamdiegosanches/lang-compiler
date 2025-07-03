package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;
import lang.nodes.dotutils.DotFile;

public class VTyFunc extends VType {

     private VType[] args;

     public VTyFunc(VType[] args){
        super(CLTypes.ARR);
        this.args= args;
     }


     public VType[] getArgs(){ return args;}

     public boolean match(VType t){
          boolean r = true;
          if (getTypeValue() == t.getTypeValue()){

             VType[] argArgs = ((VTyFunc)t).getArgs();
             for(int i =0; i < args.length; i++ ){
                 r  = r && args[i].match(argArgs[i]);
             }
             return r && args.length == argArgs.length;
          }
          return false;
     }

     public boolean matchArgs(VType[] t){
          boolean r = true;
          if(t.length == args.length - 1){
             for(int i =0; i < args.length-1; i++ ){
                 r  = r && args[i].match(t[i]);
             }
             return r;
          }
          return false;
     }

     public VType getReturnType(){
          return args[args.length-1];
     }

     public String toString(){
          String s = args[0].toString();
          for(int i = 1; i < args.length; i++){
             s += " -> "+ args[i].toString();
          }
          return s;
     }
}
