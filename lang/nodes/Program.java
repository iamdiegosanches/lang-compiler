package lang.nodes;

import java.util.ArrayList;
import lang.nodes.decl.FunDef;

public class Program extends CNode{

    private ArrayList<FunDef> funcs;


    public Program(int l, int c, ArrayList<FunDef> fs){
        super(l,c);
        this.funcs = fs;
    }

    public ArrayList<FunDef> getFuncs(){return funcs;}

    public void accept(LangVisitor v){v.visit(this);}

}
