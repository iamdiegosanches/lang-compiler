///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.decl;

import lang.nodes.CNode;
import lang.nodes.types.CType;

import java.util.ArrayList;
import lang.nodes.LangVisitor;

public class FunDef extends Def{

    private String fname;
    private ArrayList<Bind> params;
    private ArrayList<CType> ret;
    private CNode body;

    public FunDef(int l, int c, String s, ArrayList<Bind> params, ArrayList<CType> ret, CNode body){
        super(l,c);
        fname = s;
        this.params = params;
        this.ret = ret;
        this.body = body;
    }

    public String getFname(){return fname;}
    public ArrayList<Bind>  getParams(){return params;}
    public CNode getBody(){return body;}
    public ArrayList<CType> getRet(){return ret;}

    public void accept(LangVisitor v){v.visit(this);}

}