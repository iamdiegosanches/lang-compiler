package lang.nodes.visitors.tychkvisitor;

import java.util.ArrayList;

public class VTyFuncProper extends VType {

     private ArrayList<VType> paramTypes;
     private ArrayList<VType> returnTypes;

     public VTyFuncProper(ArrayList<VType> paramTypes, ArrayList<VType> returnTypes){
        super(CLTypes.FUNC);
        this.paramTypes = paramTypes;
        this.returnTypes = returnTypes;
     }

     public ArrayList<VType> getParamTypes(){ return paramTypes;}
     public ArrayList<VType> getReturnTypes(){ return returnTypes;}

     @Override
     public boolean match(VType t){
          if (t instanceof VTyFuncProper) {
             VTyFuncProper other = (VTyFuncProper) t;
             if (this.paramTypes.size() != other.paramTypes.size()) return false;
             for (int i = 0; i < this.paramTypes.size(); i++) {
                 if (!this.paramTypes.get(i).match(other.paramTypes.get(i))) return false;
             }
             if (this.returnTypes.size() != other.returnTypes.size()) return false;
             for (int i = 0; i < this.returnTypes.size(); i++) {
                 if (!this.returnTypes.get(i).match(other.returnTypes.get(i))) return false;
             }
             return true;
          }
          return false;
     }

     public boolean matchParamTypes(ArrayList<VType> actualParamTypes){
          if (this.paramTypes.size() != actualParamTypes.size()) return false;
          for(int i = 0; i < this.paramTypes.size(); i++){
              if (!this.paramTypes.get(i).match(actualParamTypes.get(i))) return false;
          }
          return true;
     }

     @Override
     public String toString(){
          StringBuilder sb = new StringBuilder();
          sb.append("(");
          for(int i = 0; i < paramTypes.size(); i++){
              sb.append(paramTypes.get(i).toString());
              if (i < paramTypes.size() - 1) sb.append(", ");
          }
          sb.append(")");
          
          sb.append(" -> (");
          for(int i = 0; i < returnTypes.size(); i++){
              sb.append(returnTypes.get(i).toString());
              if (i < returnTypes.size() - 1) sb.append(", ");
          }
          sb.append(")");
          return sb.toString();
     }
}