package lang.nodes.command;

import java.util.ArrayList;

import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.LangVisitor;

public class Return extends CNode {

    private ArrayList<Exp> e; 

    public Return(int line, int col, ArrayList<Exp> e){ 
        super(line,col);
        this.e = e;
    }

    public ArrayList<Exp> getExp(){ return e;} 

    public void accept(LangVisitor v){v.visit(this);}

}
