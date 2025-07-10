package lang.nodes.expr;

import lang.nodes.LangVisitor;
import lang.nodes.visitors.GVizVisitor;

public interface LValue {

    String getName();

    void accept(LangVisitor v);

    void accept(GVizVisitor gVizVisitor);
    
}
