package lang.nodes;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;


public abstract class LangVisitor{

    public abstract void visit(Program p);

    public abstract void visit(FunDef d);
    public abstract void visit(Bind  d);

    public abstract void visit(CSeq d);
    public abstract void visit(CAttr d);
    public abstract void visit(Loop d);
    public abstract void visit(If d);
    public abstract void visit(Return d);
    public abstract void visit(Print d);

    public abstract void visit(And e);
    public abstract void visit(BinOp e);
    public abstract void visit(UnOp e);
    public abstract void visit(Sub  e);
    public abstract void visit(Plus e);
    public abstract void visit(Times e);
    public abstract void visit(Div e);
    public abstract void visit(Mod e);
    public abstract void visit(Var e);
    public abstract void visit(LessThan e);
    public abstract void visit(Equal e);
    public abstract void visit(NotEqual e);
    public abstract void visit(Not e);
    public abstract void visit(UMinus e);
    public abstract void visit(FCall e);
    public abstract void visit(IntLit e);
    public abstract void visit(BoolLit e);
    public abstract void visit(FloatLit e);

    public abstract void visit(TyBool t);
    public abstract void visit(TyInt t);
    public abstract void visit(TyFloat t);

}
