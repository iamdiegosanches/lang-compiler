///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.tychkvisitor;


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