package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;
import lang.nodes.dotutils.DotFile;

public abstract  class VType {
     public short type;
     protected VType(short type){ this.type = type;}
     public abstract boolean match(VType t);
     public short getTypeValue(){ return type;}
}
