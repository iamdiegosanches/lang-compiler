package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;

import java.util.Hashtable;
import java.util.Stack;
import java.util.LinkedList;
import java.util.ArrayList;

public class TyChecker extends LangVisitor {

    private LinkedList < String > errors;
    private Stack < VType > stk;
    private Hashtable < String, TypeEntry > ctx;
    private Hashtable < String, VType > lolangtx;

    public TyChecker() {
        errors = new LinkedList < String > ();
        stk = new Stack < VType > ();
        ctx = new Hashtable < String, TypeEntry > ();
    }

    public void visit(Program p) {
        collectType(p.getFuncs());
        for (FunDef f: p.getFuncs()) {
            lolangtx = ctx.get(f.getFname()).localCtx;
            f.accept(this);
        }
    }

    private void collectType(ArrayList < FunDef > lf) {
        for (FunDef f: lf) {

            TypeEntry e = new TypeEntry();
            e.sym = f.getFname();
            e.localCtx = new Hashtable < String, VType > ();

            int typeln = f.getParams().size() + 1;
            for (Bind b: f.getParams()) {
                b.getType().accept(this);
                e.localCtx.put(b.getVar().getName(), stk.peek());
            }
            f.getRet().accept(this);
            VType[] v = new VType[typeln];
            for (int i = typeln - 1; i >= 0; i--) {
                v[i] = stk.pop();
            }

            e.ty = new VTyFunc(v);

            ctx.put(f.getFname(), e);
        }
    }

    public void visit(FunDef d) {

        d.getRet().accept(this);

        for (Bind b: d.getParams()) {
            b.accept(this);

        }
        d.getBody().accept(this);

    }

    public void visit(Bind d) {

        d.getType().accept(this);

        d.getVar().accept(this);
    }

    public void visit(CSeq d) {

        d.getLeft().accept(this);

        d.getRight().accept(this);

    }

    public void visit(CAttr d) {
        d.getExp().accept(this);
        if (lolangtx.get(d.getVar().getName()) == null) {
            lolangtx.put(d.getVar().getName(), stk.pop());
        } else {
            VType ty = lolangtx.get(d.getVar().getName());
            if (!ty.match(stk.pop())) {
                throw new RuntimeException(
                    "Erro de tipo (" + d.getLine() + ", " + d.getCol() + ") tipo da var " + d.getVar().getName() + " incompativel"
                );
            }
        }
    }

    public void visit(Loop d) {

        d.getCond().accept(this);
        VType tyc = stk.pop();
        if (!(tyc.getTypeValue() == CLTypes.BOOL)) {
            throw new RuntimeException(
                "Erro de tipo (" + d.getLine() + ", " +
                d.getCol() +
                ") condição do laço deve ser bool"
            );

        }
        d.getBody().accept(this);
    }

    public void visit(If d) {
        d.getCond().accept(this);
        VType tyc = stk.pop();

        if (!(tyc.getTypeValue() == CLTypes.BOOL)) {
            throw new RuntimeException(
                "Erro de tipo (" + d.getLine() + ", " +
                d.getCol() +
                ") condição do teste deve ser bool"
            );
        }
        Hashtable < String, VType > lcal1 = (Hashtable < String, VType > ) lolangtx.clone();

        d.getThn().accept(this);


        if (d.getEls() != null) {
            Hashtable < String, VType > lcal2 = (Hashtable < String, VType > ) lolangtx.clone();
            lolangtx = lcal1;
            d.getEls().accept(this);
            LinkedList < String > keys = new LinkedList < String > ();
            for (java.util.Map.Entry < String, VType > ent: lolangtx.entrySet()) {
                if (!lcal2.containsKey(ent.getKey())) {
                    keys.add(ent.getKey());
                    //System.out.println("To remove " + ent.getKey());
                }
            }
            for (String k: keys) {
                lolangtx.remove(k);
            }
        } else {
            lolangtx = lcal1;
        }
    }

    public void visit(Return d) {
        d.getExp().accept(this);

    }
    public void visit(Print d) {
        d.getExp().accept(this);
        VType td = stk.pop();
        if (td.getTypeValue() == CLTypes.INT ||
            td.getTypeValue() == CLTypes.FLOAT ||
            td.getTypeValue() == CLTypes.BOOL) {} else {
            throw new RuntimeException("Erro de tipo (" + d.getLine() + ", " + d.getCol() + ") Operandos incompatíveis");
        }

    }

    public void visit(BinOp e) {}

    public void visit(Sub e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        VType td = stk.pop();
        VType te = stk.pop();
        if (td.getTypeValue() == CLTypes.INT &&
            te.getTypeValue() == CLTypes.INT) {
            stk.push(VTyInt.newInt());
        } else if (td.getTypeValue() == CLTypes.FLOAT &&
            te.getTypeValue() == CLTypes.FLOAT) {
            stk.push(VTyFloat.newFloat());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis");
        }
    }

    public void visit(Plus e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        VType td = stk.pop();
        VType te = stk.pop();
        if (td.getTypeValue() == CLTypes.INT &&
            te.getTypeValue() == CLTypes.INT) {
            stk.push(VTyInt.newInt());
        } else if (td.getTypeValue() == CLTypes.FLOAT &&
            te.getTypeValue() == CLTypes.FLOAT) {
            stk.push(VTyFloat.newFloat());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis");
        }
    }

    public void visit(Times e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        VType td = stk.pop();
        VType te = stk.pop();
        if (td.getTypeValue() == CLTypes.INT &&
            te.getTypeValue() == CLTypes.INT) {
            stk.push(VTyInt.newInt());
        } else if (td.getTypeValue() == CLTypes.FLOAT &&
            te.getTypeValue() == CLTypes.FLOAT) {
            stk.push(VTyFloat.newFloat());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis");
        }
    }

    public void visit(Div e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        VType td = stk.pop();
        VType te = stk.pop();
        if (td.getTypeValue() == CLTypes.INT &&
            te.getTypeValue() == CLTypes.INT) {
            stk.push(VTyInt.newInt());
        } else if (td.getTypeValue() == CLTypes.FLOAT &&
            te.getTypeValue() == CLTypes.FLOAT) {
            stk.push(VTyFloat.newFloat());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis");
        }
    }

    public void visit(Mod e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        VType td = stk.pop();
        VType te = stk.pop();
        if (td.getTypeValue() == CLTypes.INT &&
            te.getTypeValue() == CLTypes.INT) {
            stk.push(VTyInt.newInt());
        } else if (td.getTypeValue() == CLTypes.FLOAT &&
            te.getTypeValue() == CLTypes.FLOAT) {
            stk.push(VTyFloat.newFloat());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis");
        }
    }

    public void visit(LessThan e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        VType td = stk.pop();
        VType te = stk.pop();
        if (td.getTypeValue() == CLTypes.INT &&
            te.getTypeValue() == CLTypes.INT) {
            stk.push(VTyInt.newInt());
        } else if (td.getTypeValue() == CLTypes.FLOAT &&
            te.getTypeValue() == CLTypes.FLOAT) {
            stk.push(VTyFloat.newFloat());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis");
        }
    }

    public void visit(Equal e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);

        VType td = stk.pop();
        VType te = stk.pop();

        if (te.getTypeValue() == td.getTypeValue()) {
            
            switch (te.getTypeValue()) {
                case CLTypes.INT:
                case CLTypes.FLOAT:
                //case CLTypes.CHAR:
                case CLTypes.BOOL:
                    
                    stk.push(VTyBool.newBool());
                    break;
                    
                default:
                    throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + 
                                               "): Operador '==' não pode ser aplicado a operandos do tipo " + te.getTypeValue());
            }

        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + 
                                       "): Tipos incompatíveis para o operador '=='.");
        }
    }

    public void visit(Var e) {
        VType ty = lolangtx.get(e.getName());
        if (ty == null) {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") variavel não declarada: " + e.getName());
        } else {
            stk.push(ty);
        }
    }

    public void visit(FCall e) {

        for (Exp ex: e.getArgs()) {
            ex.accept(this);
        }
        VType vt[] = new VType[e.getArgs().size()];
        for (int j = e.getArgs().size() - 1; j >= 0; j--) {
            vt[j] = stk.pop();
        }
        TypeEntry tyd = ctx.get(e.getID());
        if (tyd != null) {
            if (!((VTyFunc) tyd.ty).matchArgs(vt)) {
                throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") chamada de função incompatível ");
            }
            stk.push(((VTyFunc) tyd.ty).getReturnType());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") chamada a função não declarada " + e.getID());
        }
    }

    public void visit(IntLit e) {
        stk.push(VTyInt.newInt());
    }
    public void visit(BoolLit e) {
        stk.push(VTyBool.newBool());
    }
    public void visit(FloatLit e) {
        stk.push(VTyFloat.newFloat());
    }

    public void visit(TyBool t) {
        stk.push(VTyBool.newBool());
    }
    public void visit(TyInt t) {
        stk.push(VTyInt.newInt());
    }
    public void visit(TyFloat t) {
        stk.push(VTyFloat.newFloat());
    }

    public static void printEnv(Hashtable < String, VType > t) {
        for (java.util.Map.Entry < String, VType > ent: t.entrySet()) {
            System.out.println(ent.getKey() + " -> " + ent.getValue().toString());
        }
    }

}