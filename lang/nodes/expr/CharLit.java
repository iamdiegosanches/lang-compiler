package lang.nodes.expr;

import lang.nodes.LangVisitor;

public class CharLit extends Exp{
    
    private char value;
    public CharLit(int line, int col, char value){
        super(line,col);
        this.value = value;
    }

    public char getValue(){ return value;}

    @Override
    public void accept(LangVisitor v){v.visit(this);}

    @Override
    public String toString(){ return "'"+value+"'";}
}
