package lang.nodes.command;

import lang.nodes.CNode;
import lang.nodes.LangVisitor;
import lang.nodes.expr.Exp;
import lang.nodes.expr.LValue;
import java.util.ArrayList;

public class FCallCommand extends CNode {
    private String id;
    private ArrayList<Exp> args;
    private ArrayList<LValue> returnTargets; // Lista de LValues para receber os retornos

    public FCallCommand(int line, int col, String id, ArrayList<Exp> args, ArrayList<LValue> returnTargets) {
        super(line, col);
        this.id = id;
        this.args = args;
        this.returnTargets = returnTargets;
    }

    public String getID() { return id; }
    public ArrayList<Exp> getArgs() { return args; }
    public ArrayList<LValue> getReturnTargets() { return returnTargets; }

    public void accept(LangVisitor v) {
        v.visit(this);
    }
}