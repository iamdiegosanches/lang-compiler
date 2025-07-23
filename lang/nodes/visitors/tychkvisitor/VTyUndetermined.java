///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.tychkvisitor;

public class VTyUndetermined extends VType {

     private static VTyUndetermined instance = null;
     private VTyUndetermined(){
        super(CLTypes.UNDETERMINED);
     }

     public static VTyUndetermined newUndetermined(){
         if(instance == null){
             instance = new VTyUndetermined();
         }
         return instance;
     }

     @Override
     public boolean match(VType t){
         return t.getTypeValue() != CLTypes.ERR;
     }

     @Override
     public String toString(){ return "Undetermined"; }
}