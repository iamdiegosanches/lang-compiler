package lang.nodes.visitors;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;


public class SimpleVisitor extends LangVisitor{

    public void visit(Program p){
         System.out.println("numero de funcoes : " + p.getFuncs().size() );
    }

    public void visit(FunDef d) { }
    public void visit(Bind  d) { }

    public void visit(CSeq d) { }
    public void visit(CAttr d) { }
    public void visit(CDecl d) { }
    public void visit(CNull d) { }
    public void visit(Loop d) { }
    public void visit(IterateWithVar d) { }
    public void visit(If d) { }
    public void visit(Return d) { }
    public void visit(Print d) { }
    public void visit(Read d) { }
    public void visit(FCallCommand d) { }

    public void visit(And e) { }
    public void visit(BinOp e) { }
    public void visit(UnOp e) { }
    public void visit(Sub  e) { }
    public void visit(Plus e) { }
    public void visit(Times e) { }
    public void visit(Div e) { }
    public void visit(Mod e) { }
    public void visit(Var e) { }
    public void visit(LessThan e) { }
    public void visit(Equal e) { }
    public void visit(NotEqual e) { }
    public void visit(Not e) { }
    public void visit(UMinus e) { }
    public void visit(FCall e) { }
    public void visit(IntLit e) { }
    public void visit(BoolLit e) { }
    public void visit(FloatLit e) { }
    public void visit(NullLit e) { }
    public void visit(NewArray e) { }
    public void visit(ArrayAccess e) { }

    public void visit(TyChar t) { }
    public void visit(CharLit e) { }
    public void visit(TyArr e) { }

    public void visit(TyBool t) { }
    public void visit(TyInt t) { }
    public void visit(TyFloat t) { }
    public void visit(DataDef d) { }
    public void visit(Decl d) { }
    public void visit(TyUser t) { }


}