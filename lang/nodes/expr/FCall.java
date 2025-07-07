package lang.nodes.expr;

import java.util.ArrayList;
import lang.nodes.LangVisitor;

public class FCall extends Exp{

    private String id;
    private ArrayList<Exp> args;

    public FCall(int l, int c, String id, ArrayList<Exp> args){
         super(l,c);
         this.id = id;
         this.args = args;
    }

    public String getID(){return id;}
    public ArrayList<Exp> getArgs(){return args;}

    public void accept(LangVisitor v){v.visit(this);}
}































