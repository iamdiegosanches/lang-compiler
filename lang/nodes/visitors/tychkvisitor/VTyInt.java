///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.tychkvisitor;


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
