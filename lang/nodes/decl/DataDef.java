///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.decl;

import lang.nodes.LangVisitor;
import java.util.ArrayList;

public class DataDef extends Def {
    private String typeName;
    private ArrayList<Decl> attributes;
    private ArrayList<FunDef> functions;
    private boolean isAbstract;

    public DataDef(int line, int col, String typeName, ArrayList<Decl> attributes, ArrayList<FunDef> functions, boolean isAbstract) {
        super(line, col);
        this.typeName = typeName;
        this.attributes = attributes;
        this.functions = functions;
        this.isAbstract = isAbstract;
    }

    public String getTypeName() { return typeName; }
    public ArrayList<Decl> getAttributes() { return attributes; }
    public ArrayList<FunDef> getFunctions() { return functions; }
    public boolean isAbstract() { return isAbstract; }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}