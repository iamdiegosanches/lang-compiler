// lang/nodes/Program.java
package lang.nodes;

import java.util.ArrayList;
import lang.nodes.decl.Def; // <-- MUDANÇA AQUI
import lang.nodes.decl.FunDef;

public class Program extends CNode {
    private ArrayList<Def> defs; // <-- MUDANÇA AQUI (de FunDef para Def)

    public Program(int l, int c, ArrayList<Def> ds) { // <-- MUDANÇA AQUI
        super(l, c);
        this.defs = ds;
    }

    public ArrayList<Def> getDefs() { return defs; } // <-- MUDANÇA AQUI

    // Opcional: manter getFuncs por compatibilidade ou remover
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