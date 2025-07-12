package lang.nodes.expr;

import lang.nodes.LangVisitor;

public interface LValue {

    String getName();

    void accept(LangVisitor v);

    
}
