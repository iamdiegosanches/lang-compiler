///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.codegen;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Hashtable;

import lang.nodes.CNode;
import lang.nodes.decl.FunDef;
import lang.nodes.expr.LValue;
import lang.nodes.expr.Var;
import lang.nodes.command.CAttr;
import lang.nodes.command.CSeq;
import lang.nodes.command.FCallCommand;
import lang.nodes.command.If;
import lang.nodes.command.IterateWithVar;
import lang.nodes.command.Loop;
import lang.nodes.visitors.SimpleVisitor;
import lang.nodes.visitors.tychkvisitor.VType;

public class DeclarationVisitor extends SimpleVisitor {
    private final LinkedHashSet<CAttr> declarations = new LinkedHashSet<>();
    private final Set<String> declaredVars = new HashSet<>();
    private final Hashtable<CNode, VType> typeMap;

    public DeclarationVisitor(Hashtable<CNode, VType> typeMap) {
        this.typeMap = typeMap;
    }

    public ArrayList<CAttr> getDeclarations() {
        return new ArrayList<>(declarations);
    }

    @Override
    public void visit(CAttr d) {
        if (d.getVar() instanceof Var) {
            String varName = d.getVar().getName();
            if (!declaredVars.contains(varName)) {
                declarations.add(d);
                declaredVars.add(varName);
            }
        }
    }

    @Override
    public void visit(CSeq s) {
        s.getLeft().accept(this);
        s.getRight().accept(this);
    }

    @Override
    public void visit(If i) {
        i.getThn().accept(this);
        if (i.getEls() != null) i.getEls().accept(this);
    }

    @Override
    public void visit(Loop l) {
        l.getBody().accept(this);
    }

    @Override
    public void visit(IterateWithVar i) {
        if (i.getIterVar() instanceof Var) {
             String varName = ((Var) i.getIterVar()).getName();
             if (!declaredVars.contains(varName)) {
                  // Declara a variável de iteração como uma declaração para o code gen
                  declarations.add(new CAttr(i.getLine(), i.getCol(), i.getIterVar(), null));
                  declaredVars.add(varName);
             }
        }
        i.getBody().accept(this);
    }

    @Override
    public void visit(FCallCommand d) {
        for (LValue target : d.getReturnTargets()) {
            if (target instanceof Var) {
                String varName = target.getName();
                if (!declaredVars.contains(varName)) {
                    declarations.add(new CAttr(target.getLine(), target.getCol(), target, null));
                    declaredVars.add(varName);
                }
            }
        }
    }

    // Sobrescrevemos os métodos de visita vazios para evitar StackOverflow
    @Override public void visit(FunDef d) {}
}