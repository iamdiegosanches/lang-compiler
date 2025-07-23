package lang.nodes.decl;

import lang.nodes.LangVisitor;
import java.util.ArrayList;

public class DataDef extends Def {
    private String typeName;
    private ArrayList<Decl> attributes;
    private boolean isAbstract;

    public DataDef(int line, int col, String typeName, ArrayList<Decl> attributes, boolean isAbstract) {
        super(line, col);
        this.typeName = typeName;
        this.attributes = attributes;
        this.isAbstract = isAbstract;
    }

    public String getTypeName() { return typeName; }
    public ArrayList<Decl> getAttributes() { return attributes; }
    public boolean isAbstract() { return isAbstract; }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}