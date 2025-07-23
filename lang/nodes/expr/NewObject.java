package lang.nodes.expr;

import lang.nodes.LangVisitor;
import lang.nodes.types.TyUser;

// Representa uma expressão como 'new Ponto'
public class NewObject extends Exp {
    private TyUser type;

    public NewObject(int line, int col, TyUser type) {
        super(line, col);
        this.type = type;
    }

    public TyUser getType() { return type; }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}