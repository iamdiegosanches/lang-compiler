///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
// lang/nodes/Program.java
package lang.nodes;

import java.util.ArrayList;
import lang.nodes.decl.Def; 
import lang.nodes.decl.FunDef;

public class Program extends CNode {
    private ArrayList<Def> defs; 

    public Program(int l, int c, ArrayList<Def> ds) { 
        super(l, c);
        this.defs = ds;
    }

    public ArrayList<Def> getDefs() { return defs; } 

    public ArrayList<FunDef> getFuncs() {
        ArrayList<FunDef> funcs = new ArrayList<>();
        for (Def d : defs) {
            if (d instanceof FunDef) {
                funcs.add((FunDef) d);
            }
        }
        return funcs;
    }

    public void accept(LangVisitor v) { v.visit(this); }
}