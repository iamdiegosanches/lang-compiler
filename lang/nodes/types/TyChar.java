package lang.nodes.types;

import lang.nodes.LangVisitor;

public class TyChar extends CType {

    public TyChar(int line, int col){
        super(line,col);
    }

    @Override
    public void accept(LangVisitor v){v.visit(this);}

    @Override
    public String toString(){ return "Char";}
}