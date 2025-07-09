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

    // private Hashtable < String, VType > lolangtx;

    // Uma pilha onde cada elementa da pilha é uma tabela hash que mapeia string para Vtype
    private Stack < Hashtable < String, VType >> tyEnv;

    public TyChecker() {
        errors = new LinkedList < String > ();
        stk = new Stack < VType > ();
        ctx = new Hashtable < String, TypeEntry > ();

        tyEnv = new Stack <>();
    }

    public void enterScope() { // LEMBRAR DE MUDAR PRA PRIVATE DEPOIS
        tyEnv.push(new Hashtable<String, VType>());
    }

    public void leaveScope() { // LEMBRAR DE MUDAR PRA PRIVATE DEPOIS
        tyEnv.pop();
    }

    // private void declareVar(String name, VType type, int line, int col) {
    //     Hashtable<String, VType> currentScope = tyEnv.peek();
    //     if (currentScope.containsKey(name)) {
    //         throw new RuntimeException(
    //             "Erro Semântico (" + line + ", " + col + "): Variável '" + name + "' já foi declarada neste escopo."
    //         );
    //     }
    //     currentScope.put(name, type);
    // }

    private void declareVar(String name, VType type, int line, int col) {
        for (int i = tyEnv.size() - 2; i >= 0; i--) {
            if (tyEnv.get(i).containsKey(name)) {
                throw new RuntimeException(
                    "Erro Semântico (" + line + ", " + col + "): Variável '" + name + "' já foi declarada em escopo superior."
                );
            }
        }

        Hashtable<String, VType> currentScope = tyEnv.peek();
        if (currentScope.containsKey(name)) {
            throw new RuntimeException(
                "Erro Semântico (" + line + ", " + col + "): Variável '" + name + "' já foi declarada neste escopo."
            );
        }

        currentScope.put(name, type);
    }


    private VType findVar(String name) {
        for (int i = tyEnv.size() - 1; i >= 0; i--) {
            Hashtable<String, VType> scope = tyEnv.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null; // Retorna null se não encontrar
    }

    public void visit(Program p) {
        enterScope();

        collectType(p.getFuncs());
        for (FunDef f: p.getFuncs()) {
            tyEnv.clear();
            tyEnv.push(ctx.get(f.getFname()).localCtx); 
            f.accept(this);
        }

        leaveScope();
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
        String varName = d.getVar().getName();

        VType varType = findVar(varName);

        if (varType == null) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Variável '" + varName + "' não foi declarada."
            );
        }

        d.getExp().accept(this);
        VType expType = stk.pop();

        if (!varType.match(expType)) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tipos incompatíveis na atribuição para '" + varName + "'."
            );
        }
    }

    public void visit(CDecl d) {
        d.getExp().accept(this);
        VType expType = stk.pop();

        d.getType().accept(this);
        VType varType = stk.pop();

        if (!varType.match(expType)) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tipos incompatíveis na declaração de '" + d.getVar().getName() + "'."
            );
        }
        
        declareVar(d.getVar().getName(), varType, d.getLine(), d.getCol());
    }

    public void visit(CNull d) {
        // vazio
    }
    

    public void visit(Loop d) {
        d.getCond().accept(this);
        VType tyc = stk.pop();
        if (!(tyc.getTypeValue() == CLTypes.INT)) {
            throw new RuntimeException(
                "Erro de tipo (" + d.getLine() + ", " +
                d.getCol() +
                ") condição do laço deve ser bool"
            );

        }
        enterScope();
        d.getBody().accept(this);
        leaveScope();
    }

    public void visit(IterateWithVar d) {
        d.getCondExp().accept(this);
        VType condType = stk.pop();

        if (condType.getTypeValue() != CLTypes.INT) {
            // Futuramente, poderia aceitar arrays aqui
            throw new RuntimeException("Erro de tipo (" + d.getLine() + ", " + d.getCol() + "): Expressão do 'iterate' com variável deve ser um inteiro.");
        }

        enterScope();
        
        String varName = d.getIterVar().getName();
        declareVar(varName, VTyInt.newInt(), d.getLine(), d.getCol());
        
        d.getBody().accept(this);
        
        leaveScope();
    }


    public void visit(If d) {
        d.getCond().accept(this);
        VType tyc = stk.pop();
        if (!(tyc.getTypeValue() == CLTypes.BOOL)) {
            throw new RuntimeException(
                "Erro de tipo (" + d.getLine() + ", " + d.getCol() + ") condição do teste deve ser bool"
            );
        }
        
        enterScope();
        d.getThn().accept(this);
        leaveScope();

        if (d.getEls() != null) {
            enterScope();
            d.getEls().accept(this);
            leaveScope();
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
            td.getTypeValue() == CLTypes.BOOL ||
            td.getTypeValue() == CLTypes.CHAR) {} else {
            throw new RuntimeException("Erro de tipo (" + d.getLine() + ", " + d.getCol() + ") Operandos incompatíveis");
        }

    }

    public void visit(Read d) {
        LValue lv = d.getTarget();

        if (!(lv instanceof Var)) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): read só suporta variáveis simples."
            );
        }

        String varName = ((Var) lv).getName();

        VType varType = findVar(varName);

        if (varType == null) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Variável '" + varName + "' usada em read não foi declarada."
            );
        }

        if (!(varType instanceof VTyInt || varType instanceof VTyFloat ||
            varType instanceof VTyChar || varType instanceof VTyBool)) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tipo de '" + varName + "' não é permitido em read."
            );
        }
    }

    public void visit(And e){
        e.getLeft().accept(this);
        e.getRight().accept(this);

        VType rightType = stk.pop();
        VType leftType = stk.pop();

        if (leftType.getTypeValue() == CLTypes.BOOL && rightType.getTypeValue() == CLTypes.BOOL) {
            stk.push(VTyBool.newBool());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + 
                                       "): Operador '&&' espera operandos do tipo 'Bool'.\n" +
                                       "\t- Operando da esquerda é do tipo '" + leftType.toString() + "'.\n" +
                                       "\t- Operando da direita é do tipo '" + rightType.toString() + "'.");
        }
    }

    public void visit(BinOp e) {}
    public void visit(UnOp e) {}

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

        VType rightType = stk.pop();
        VType leftType = stk.pop();

        if (leftType.getTypeValue() == CLTypes.INT && rightType.getTypeValue() == CLTypes.INT) {
            stk.push(VTyInt.newInt()); 
        } else {
            String errorMsg = "Erro de Tipo (" + e.getLine() + ", " + e.getCol() + "): " +
                            "O operador de módulo '%' espera operandos do tipo 'Int'.\n" +
                            "\t- Operando da esquerda é do tipo '" + leftType.toString() + "'.\n" +
                            "\t- Operando da direita é do tipo '" + rightType.toString() + "'.";
            throw new RuntimeException(errorMsg);
        }
    }

    public void visit(LessThan e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        VType rightType = stk.pop();
        VType leftType = stk.pop();

        // A comparação pode ser entre Ints ou entre Floats.
        boolean isIntComparison = leftType.match(VTyInt.newInt()) && rightType.match(VTyInt.newInt());
        boolean isFloatComparison = leftType.match(VTyFloat.newFloat()) && rightType.match(VTyFloat.newFloat());

        if (isIntComparison || isFloatComparison) {
            // --- CORREÇÃO ---
            // O RESULTADO de uma operação de comparação (<, ==, !=) é SEMPRE um booleano.
            stk.push(VTyBool.newBool());
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + 
                                    "): Operador '<' espera operandos do mesmo tipo (Int ou Float), mas recebeu " + 
                                    leftType + " e " + rightType);
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
                case CLTypes.CHAR:
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

    public void visit(NotEqual e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);

        VType td = stk.pop();
        VType te = stk.pop();

        if (te.getTypeValue() == td.getTypeValue()) {
            
            switch (te.getTypeValue()) {
                case CLTypes.INT:
                case CLTypes.FLOAT:
                case CLTypes.BOOL:
                case CLTypes.CHAR:
                    
                    stk.push(VTyBool.newBool());
                    break;
                    
                default:
                    throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + 
                                               "): Operador '!=' não pode ser aplicado a operandos do tipo " + te.getTypeValue());
            }

        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + 
                                       "): Tipos incompatíveis para o operador '!='.");
        }
    }

    public void visit(Not e) {
        e.getRight().accept(this);
        VType td = stk.pop();
        if (td.getTypeValue() == CLTypes.BOOL) {
            stk.push(VTyBool.newBool());
        } else {
            throw new RuntimeException(" Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") deve ser Bool.");
        }
    }

    public void visit(UMinus e) {
        e.getRight().accept(this);
        VType td = stk.pop();
        if (td.getTypeValue() == CLTypes.INT) {
            stk.push(VTyInt.newInt());
        } else if (td.getTypeValue() == CLTypes.FLOAT) {
            stk.push(VTyFloat.newFloat());
        }else {
            throw new RuntimeException(" Erro de tipo (" + e.getLine() + ", " + e.getCol() + ").");
        }
    }

    public void visit(Var e) {
        VType ty = findVar(e.getName());
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
    
    public void visit(TyChar t) { stk.push(VTyChar.newChar()); }
    public void visit(CharLit e) { stk.push(VTyChar.newChar()); } 

    public static void printEnv(Hashtable < String, VType > t) {
        for (java.util.Map.Entry < String, VType > ent: t.entrySet()) {
            System.out.println(ent.getKey() + " -> " + ent.getValue().toString());
        }
    }

}