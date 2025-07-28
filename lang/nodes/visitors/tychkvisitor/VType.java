///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.tychkvisitor;


public abstract  class VType {
     public short type;
     protected VType(short type){ this.type = type;}
     public abstract boolean match(VType t);
     public short getTypeValue(){ return type;}
}
