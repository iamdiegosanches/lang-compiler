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

    private Stack < Hashtable < String, VType >> tyEnv;

    private ArrayList<VType> currentFunctionReturnTypes;

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
        collectFunctionSignatures(p.getFuncs());

        for (FunDef f: p.getFuncs()) {
            enterScope();

            for (Bind b : f.getParams()) {
                b.getType().accept(this);
                VType paramType = stk.pop();
                tyEnv.peek().put(b.getVar().getName(), paramType);
            }

            ArrayList<VType> convertedReturnTypes = new ArrayList<>();
            for (CType retTypeAst : f.getRet()) {
                retTypeAst.accept(this); 
                convertedReturnTypes.add(stk.pop());
            }
            this.currentFunctionReturnTypes = convertedReturnTypes;
            
            f.accept(this);

            leaveScope();
            this.currentFunctionReturnTypes = null; 
        }

        TypeEntry mainEntry = ctx.get("main");
        if (mainEntry == null) {
            throw new RuntimeException("Erro Semântico: Função 'main' não encontrada.");
        }
        if (!mainEntry.ty.match(new VTyFuncProper(new ArrayList<>(), new ArrayList<>()))) { // main deve ter ( ) -> ( )
             throw new RuntimeException("Erro Semântico: A função 'main' deve ter 0 parâmetros e 0 retornos.");
        }
    }

    private void collectFunctionSignatures(ArrayList < FunDef > lf) {
        for (FunDef f: lf) {
            TypeEntry e = new TypeEntry();
            e.sym = f.getFname();
            e.localCtx = new Hashtable < String, VType > ();

            ArrayList<VType> paramTypes = new ArrayList<>();
            for (Bind b: f.getParams()) {
                b.getType().accept(this); // Avalia o tipo do parâmetro e empilha
                paramTypes.add(stk.pop());
            }

            ArrayList<VType> returnTypes = new ArrayList<>();
            for (CType retType : f.getRet()) {
                retType.accept(this);
                returnTypes.add(stk.pop());
            }

            VType[] funcTypesArray = new VType[paramTypes.size() + returnTypes.size()];
            for (int i = 0; i < paramTypes.size(); i++) {
                funcTypesArray[i] = paramTypes.get(i);
            }
            for (int i = 0; i < returnTypes.size(); i++) {
                funcTypesArray[i + paramTypes.size()] = returnTypes.get(i);
            }
            
            e.ty = new VTyFuncProper(paramTypes, returnTypes); // <--- ASSUMIMOS QUE VTyFuncProper FOI CRIADA

            if (ctx.containsKey(f.getFname())) {
                 throw new RuntimeException("Erro Semântico (" + f.getLine() + ", " + f.getCol() + "): Função '" + f.getFname() + "' já declarada.");
            }
            ctx.put(f.getFname(), e);
        }
    }

    public void visit(FunDef d) {
        d.getBody().accept(this);
    }

    public void visit(Bind d) {
        // Nenhuma ação aqui, pois os parâmetros já são processados na collectFunctionSignatures e no visit(Program p).
    }

    public void visit(CSeq d) {
        d.getLeft().accept(this);
        d.getRight().accept(this);
    }

    public void visit(CAttr d) {
        d.getExp().accept(this);
        VType expType = stk.pop();

        LValue lvalue = d.getVar();
        
        String varName = ((Var) lvalue).getName();
        VType varType = findVar(varName);

        if (varType == null) {
            declareVar(varName, expType, d.getLine(), d.getCol());
        } else {
            if (!varType.match(expType)) {
                throw new RuntimeException(
                    "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tipos incompatíveis na atribuição para '" + varName + "'. Esperado '" + varType.toString() + "', encontrado '" + expType.toString() + "'."
                );
            }
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
                ") condição do laço 'iterate' deve ser Int"
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
            throw new RuntimeException("Erro de tipo (" + d.getLine() + ", " + d.getCol() + "): Expressão de iteração para 'iterate' com variável deve ser um inteiro.");
        }

        enterScope();
        String varName = d.getIterVar().getName();
        if (findVar(varName) != null) {
             throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Variável de iteração '" + varName + "' já declarada em escopo externo.");
        }
        declareVar(varName, VTyInt.newInt(), d.getLine(), d.getCol()); // Garante que a variável do loop é Int
        d.getBody().accept(this);
        leaveScope();
    }

    public void visit(If d) {
        d.getCond().accept(this);
        VType tyc = stk.pop();
        if (!(tyc.getTypeValue() == CLTypes.BOOL)) {
            throw new RuntimeException(
                "Erro de tipo (" + d.getLine() + ", " + d.getCol() + ") condição do teste 'if' deve ser Bool"
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
        ArrayList<VType> returnedTypes = new ArrayList<>();
        for (Exp exp : d.getExp()) { 
            exp.accept(this);
            returnedTypes.add(stk.pop());
        }

        if (this.currentFunctionReturnTypes == null) {
            throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Comando 'return' fora do escopo de uma função.");
        }


        if (returnedTypes.size() != this.currentFunctionReturnTypes.size()) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() +
                "): Número de valores retornados (" + returnedTypes.size() +
                ") não corresponde à assinatura da função (" + this.currentFunctionReturnTypes.size() + ")."
            );
        }

        for (int i = 0; i < returnedTypes.size(); i++) {
            VType expectedType = this.currentFunctionReturnTypes.get(i);
            VType actualType = returnedTypes.get(i);
            if (!expectedType.match(actualType)) {

                if (actualType.getTypeValue() == CLTypes.NULL && (expectedType.getTypeValue() == CLTypes.INT ||
                                                               expectedType.getTypeValue() == CLTypes.FLOAT ||
                                                               expectedType.getTypeValue() == CLTypes.BOOL ||
                                                               expectedType.getTypeValue() == CLTypes.CHAR)) {
                    throw new RuntimeException(
                        "Erro Semântico (" + d.getLine() + ", " + d.getCol() +
                        "): Tipo de retorno incompatível. Esperado '" + expectedType.toString() +
                        "', encontrado 'null' para tipo primitivo."
                    );
                }
                if (!expectedType.match(actualType) && actualType.getTypeValue() != CLTypes.NULL) { 
                    throw new RuntimeException(
                        "Erro Semântico (" + d.getLine() + ", " + d.getCol() +
                        "): Tipo de retorno incompatível na posição " + i + ". Esperado '" + expectedType.toString() +
                        "', encontrado '" + actualType.toString() + "'."
                    );
                }
            }
        }
    }

    public void visit(Print d) {
        d.getExp().accept(this);
        VType td = stk.pop();
        if (td.getTypeValue() == CLTypes.INT ||
            td.getTypeValue() == CLTypes.FLOAT ||
            td.getTypeValue() == CLTypes.BOOL ||
            td.getTypeValue() == CLTypes.CHAR ||
            td.getTypeValue() == CLTypes.NULL) {
        } else {
            throw new RuntimeException("Erro de tipo (" + d.getLine() + ", " + d.getCol() + ") Operandos incompatíveis para 'print'.");
        }

    }

    public void visit(Read d) {
        LValue lv = d.getTarget();
        if (!(lv instanceof Var)) {
            throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Comando 'read' só suporta variáveis simples por enquanto.");
        }
        
        VType varType = findVar(((Var)lv).getName()); 
        if (varType == null) {
            throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Variável '" + ((Var)lv).getName() + "' não declarada para leitura.");
        }

        if (!(varType instanceof VTyInt || varType instanceof VTyFloat ||
            varType instanceof VTyChar || varType instanceof VTyBool)) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tipo do destino da leitura ('" + varType.toString() + "') não é permitido no comando 'read'. Apenas Int, Float, Char, Bool são permitidos."
            );
        }
        stk.push(varType);
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
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis para '-'.");
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
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis para '+'.");
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
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis para '*'.");
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
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") Operandos incompatíveis para '/'.");
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

        boolean isIntComparison = leftType.match(VTyInt.newInt()) && rightType.match(VTyInt.newInt());
        boolean isFloatComparison = leftType.match(VTyFloat.newFloat()) && rightType.match(VTyFloat.newFloat());

        if (isIntComparison || isFloatComparison) {
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

        if (te.getTypeValue() == CLTypes.NULL || td.getTypeValue() == CLTypes.NULL) {
            if ((te.getTypeValue() != CLTypes.NULL && (te.getTypeValue() == CLTypes.INT || te.getTypeValue() == CLTypes.FLOAT || te.getTypeValue() == CLTypes.BOOL || te.getTypeValue() == CLTypes.CHAR)) ||
                (td.getTypeValue() != CLTypes.NULL && (td.getTypeValue() == CLTypes.INT || td.getTypeValue() == CLTypes.FLOAT || td.getTypeValue() == CLTypes.BOOL || td.getTypeValue() == CLTypes.CHAR))) {
                throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Null não pode ser comparado com tipos primitivos.");
            }
            stk.push(VTyBool.newBool());
            return;
        }

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

        if (te.getTypeValue() == CLTypes.NULL || td.getTypeValue() == CLTypes.NULL) {
            if ((te.getTypeValue() != CLTypes.NULL && (te.getTypeValue() == CLTypes.INT || te.getTypeValue() == CLTypes.FLOAT || te.getTypeValue() == CLTypes.BOOL || te.getTypeValue() == CLTypes.CHAR)) ||
                (td.getTypeValue() != CLTypes.NULL && (td.getTypeValue() == CLTypes.INT || td.getTypeValue() == CLTypes.FLOAT || td.getTypeValue() == CLTypes.BOOL || td.getTypeValue() == CLTypes.CHAR))) {
                throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Null não pode ser comparado com tipos primitivos.");
            }
            stk.push(VTyBool.newBool());
            return;
        }

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

    @Override 
    public void visit(FCall e) {
        TypeEntry tyd = ctx.get(e.getID());
        if (tyd != null) {
            VTyFuncProper funcType = (VTyFuncProper) tyd.ty;

            ArrayList<VType> actualArgTypes = new ArrayList<>();
            for (Exp argExp : e.getArgs()) {
                argExp.accept(this);
                actualArgTypes.add(stk.pop());
            }

            if (!funcType.matchParamTypes(actualArgTypes)) {
                throw new RuntimeException(
                    "Erro de tipo (" + e.getLine() + ", " + e.getCol() +
                    "): Chamada de função incompatível para '" + e.getID() +
                    "'. Esperado: " + funcType.getParamTypes() + ", Encontrado: " + actualArgTypes
                );
            }

            if (e.getReturnIndex() != null) {
                e.getReturnIndex().accept(this); 
                VType indexType = stk.pop();

                if (indexType.getTypeValue() != CLTypes.INT) {
                    throw new RuntimeException(
                        "Erro de tipo (" + e.getLine() + ", " + e.getCol() +
                        "): Índice de retorno da função '" + e.getID() + "' deve ser um inteiro."
                    );
                }

                ArrayList<VType> declaredReturnTypes = funcType.getReturnTypes();

                if (declaredReturnTypes.isEmpty()) {
                    throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Função '" + e.getID() + "' não retorna valores para serem indexados.");
                }

                stk.push(declaredReturnTypes.get(0)); 
                if (e.getReturnIndex() instanceof IntLit) {
                    int indexVal = ((IntLit) e.getReturnIndex()).getValue();
                    if (indexVal < 0 || indexVal >= declaredReturnTypes.size()) {
                        throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Índice de retorno " + indexVal + " fora dos limites para a função '" + e.getID() + "'.");
                    }
                    stk.push(declaredReturnTypes.get(indexVal));
                } else {

                     if (declaredReturnTypes.isEmpty()) {
                        throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Função '" + e.getID() + "' não retorna valores para serem indexados.");
                    }
                    stk.push(declaredReturnTypes.get(0));
                }


            } else {

                ArrayList<VType> declaredReturnTypes = funcType.getReturnTypes();
                if (declaredReturnTypes.size() > 1) {
                    throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Chamada de função '" + e.getID() + "' sem índice de retorno, mas a função declara múltiplos retornos.");
                } else if (declaredReturnTypes.isEmpty()) {
                     throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Função '" + e.getID() + "' é um procedimento (não retorna valores) mas está sendo usada como expressão.");
                } else {
                    stk.push(declaredReturnTypes.get(0));
                }
            }
        } else {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + ") chamada a função não declarada " + e.getID());
        }
    }

    @Override
    public void visit(FCallCommand d) {
        TypeEntry tyd = ctx.get(d.getID());
        if (tyd != null) {
            VTyFuncProper funcType = (VTyFuncProper) tyd.ty;

            ArrayList<VType> actualArgTypes = new ArrayList<>();
            for (Exp argExp : d.getArgs()) {
                argExp.accept(this);
                actualArgTypes.add(stk.pop());
            }

            if (!funcType.matchParamTypes(actualArgTypes)) {
                throw new RuntimeException(
                    "Erro de tipo (" + d.getLine() + ", " + d.getCol() +
                    "): Chamada de função incompatível para '" + d.getID() +
                    "'. Esperado: " + funcType.getParamTypes() + ", Encontrado: " + actualArgTypes
                );
            }

            ArrayList<VType> declaredReturnTypes = funcType.getReturnTypes();
            ArrayList<LValue> returnTargets = d.getReturnTargets();

            if (declaredReturnTypes.size() != returnTargets.size()) {
                throw new RuntimeException(
                    "Erro de tipo (" + d.getLine() + ", " + d.getCol() +
                    "): Número de valores retornados pela função '" + d.getID() +
                    "' (" + declaredReturnTypes.size() + ") não corresponde ao número de destinos de atribuição (" + returnTargets.size() + ")."
                );
            }

            for (int i = 0; i < declaredReturnTypes.size(); i++) {
                LValue target = returnTargets.get(i);
                VType expectedType = declaredReturnTypes.get(i);

                if (!(target instanceof Var)) {
                     throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Destino de atribuição de retorno não suportado (apenas variáveis simples por enquanto).");
                }
                String varName = ((Var)target).getName();
                VType targetVarType = findVar(varName);

                if (targetVarType == null) {
                   
                    throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Variável '" + varName + "' não declarada para receber valor de retorno.");
                }

                if (!targetVarType.match(expectedType)) {
                    if (expectedType.getTypeValue() == CLTypes.NULL && (targetVarType.getTypeValue() == CLTypes.INT ||
                                                                    targetVarType.getTypeValue() == CLTypes.FLOAT ||
                                                                    targetVarType.getTypeValue() == CLTypes.BOOL ||
                                                                    targetVarType.getTypeValue() == CLTypes.CHAR)) {
                        throw new RuntimeException(
                            "Erro Semântico (" + d.getLine() + ", " + d.getCol() +
                            "): Tipo de atribuição incompatível. Esperado '" + targetVarType.toString() +
                            "', encontrado 'null' para tipo primitivo na posição " + i + "."
                        );
                    }
                    if (!targetVarType.match(expectedType) && expectedType.getTypeValue() != CLTypes.NULL) {
                        throw new RuntimeException(
                            "Erro Semântico (" + d.getLine() + ", " + d.getCol() +
                            "): Tipo de atribuição incompatível na posição " + i + ". Esperado '" + targetVarType.toString() +
                            "', encontrado '" + expectedType.toString() + "' para '" + varName + "'."
                        );
                    }
                }
            }
        } else {
            throw new RuntimeException("Erro de tipo (" + d.getLine() + ", " + d.getCol() + ") Chamada a função não declarada " + d.getID());
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

    public void visit(NullLit e) { stk.push(VTyNull.newNull()); }

    public static void printEnv(Hashtable < String, VType > t) {
        for (java.util.Map.Entry < String, VType > ent: t.entrySet()) {
            System.out.println(ent.getKey() + " -> " + ent.getValue().toString());
        }
    }
}