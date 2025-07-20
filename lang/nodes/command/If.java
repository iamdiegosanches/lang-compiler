package lang.nodes.command;


import lang.nodes.CNode;
import lang.nodes.expr.Exp;
import lang.nodes.LangVisitor;

public class If extends CNode{

    private Exp cond;
    private CNode thn;
    private CNode els;
    public If(int l, int c, Exp e, CNode thn, CNode els){
        super(l,c);
        cond = e;
        this.thn = thn;
        this.els = els;
    }

    public Exp getCond(){return cond;}
    public CNode getThn(){return thn;}
    public CNode getEls(){return els;}

    public void accept(LangVisitor v){v.visit(this);}

}
