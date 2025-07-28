///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.tychkvisitor;


public class VTyFloat extends VType {

     private static VTyFloat instance = null;
     private VTyFloat(){
        super(CLTypes.FLOAT);
     }

     public static VTyFloat newFloat(){
         if(instance == null){
             instance = new VTyFloat();
         }
         return instance;
     }

     public boolean match(VType t){ return getTypeValue() == t.getTypeValue();}

     public String toString(){ return "Float";}
}
