package lang.nodes.types;


import lang.nodes.LangVisitor;


public class TyBool extends CType {

    public TyBool(int line, int col){
        super(line,col);
    }

    public void accept(LangVisitor v){v.visit(this);}

}
