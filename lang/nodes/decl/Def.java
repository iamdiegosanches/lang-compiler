package lang.nodes.decl;

import lang.nodes.CNode;


public abstract class Def extends CNode {
    public Def(int line, int col) {
        super(line, col);
    }
}