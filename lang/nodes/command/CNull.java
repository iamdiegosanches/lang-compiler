package lang.nodes.command;

import lang.nodes.CNode;
import lang.nodes.LangVisitor;

public class CNull extends CNode {

    public CNull(int line, int col) {
        super(line, col);
    }

    @Override
    public void accept(LangVisitor v) {
        v.visit(this);
    }
}
