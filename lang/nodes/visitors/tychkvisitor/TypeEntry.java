package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.LangVisitor;
import java.util.Hashtable;

public class TypeEntry {
     public String sym;
     public VType ty;
     public Hashtable<String,VType> localCtx;

}
