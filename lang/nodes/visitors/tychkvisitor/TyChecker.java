///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.tychkvisitor;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;

import java.util.Hashtable;
import java.util.Stack;
import java.util.ArrayList;

public class TyChecker extends LangVisitor {

    private Stack < VType > stk;
    private Hashtable < String, TypeEntry > ctx;

    private Stack < Hashtable < String, VType >> tyEnv;

    private ArrayList<VType> currentFunctionReturnTypes;

    public TyChecker() {
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
            
            e.ty = new VTyFuncProper(paramTypes, returnTypes); 

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

        if (lvalue instanceof Var) {
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
        } else if (lvalue instanceof ArrayAccess) {
            ArrayAccess arrayAccess = (ArrayAccess) lvalue;
            
            arrayAccess.getArrayVar().accept(this);
            VType arrayVarType = stk.pop();

            if (!(arrayVarType.getTypeValue() == CLTypes.ARR)) {
                throw new RuntimeException(
                    "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tentativa de atribuição a elemento de array em uma variável que não é um array. Tipo encontrado: " + arrayVarType.toString()
                );
            }
            VTyArr actualArrayType = (VTyArr) arrayVarType;

            arrayAccess.getIndexExp().accept(this);
            VType indexExpType = stk.pop();

            if (!(indexExpType.getTypeValue() == CLTypes.INT)) {
                throw new RuntimeException(
                    "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Índice de array deve ser um inteiro. Tipo encontrado: " + indexExpType.toString()
                );
            }

            VType currentElementType = actualArrayType.getTyArg();

            if (currentElementType.getTypeValue() == CLTypes.UNDETERMINED) {
                actualArrayType.setTyArg(expType);
            } else {
                if (!currentElementType.match(expType)) {
                    throw new RuntimeException(
                        "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tipos incompatíveis na atribuição a elemento de array. Esperado '" + currentElementType.toString() + "', encontrado '" + expType.toString() + "'."
                    );
                }
            }
        } else {
            throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): LValue de atribuição não suportado.");
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
                ") condição do laço deve ser int"
            );
        }
        enterScope();
        d.getBody().accept(this);
        leaveScope();
    }

    public void visit(IterateWithVar d) {
        d.getCondExp().accept(this);
        VType condExpTy = stk.pop();

        VType iterVarTy;
        if (condExpTy.getTypeValue() == CLTypes.INT) {
            iterVarTy = VTyInt.newInt();
        } else if (condExpTy.getTypeValue() == CLTypes.ARR) {
            iterVarTy = ((VTyArr) condExpTy).getTyArg();
            if (iterVarTy.getTypeValue() == CLTypes.UNDETERMINED) {
                 throw new RuntimeException(
                    "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Não é possível iterar sobre um array com tipo de elemento indeterminado. Atribua um valor a um elemento do array primeiro para determinar seu tipo."
                );
            }
        } else {
            throw new RuntimeException("Erro de tipo (" + d.getLine() + ", " + d.getCol() + "): A expressão no 'iterate' deve ser um inteiro ou um array. Encontrado: " + condExpTy.toString());
        }

        enterScope();
        
        String varName = ((Var)d.getIterVar()).getName();
        declareVar(varName, iterVarTy, d.getLine(), d.getCol());
        
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
            td.getTypeValue() == CLTypes.NULL ||
            td.getTypeValue() == CLTypes.ARR) {} else {
            throw new RuntimeException("Erro de tipo (" + d.getLine() + ", " + d.getCol() + ") Operandos incompatíveis");
        }

    }


    public void visit(Read d) {
        LValue lv = d.getTarget();
        
        if (lv instanceof Var) {
            lv.accept(this); 
            VType varType = stk.pop();

            if (!(varType instanceof VTyInt || varType instanceof VTyFloat ||
                varType instanceof VTyChar || varType instanceof VTyBool)) {
                throw new RuntimeException(
                    "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tipo da variável ('" + varType.toString() + "') não é permitido no comando 'read'."
                );
            }
        } else if (lv instanceof ArrayAccess) {
            lv.accept(this); 
            VType elementType = stk.pop();

            if (!(elementType instanceof VTyInt || elementType instanceof VTyFloat ||
                elementType instanceof VTyChar || elementType instanceof VTyBool)) {
                throw new RuntimeException(
                    "Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Tipo do elemento do array ('" + elementType.toString() + "') não é permitido no comando 'read'."
                );
            }
        } else {
            throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Alvo de leitura não suportado.");
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
        if (tyd == null) {
            throw new RuntimeException("Erro Semântico (" + d.getLine() + ", " + d.getCol() + "): Função '" + d.getID() + "' não declarada.");
        }

        VTyFuncProper funcType = (VTyFuncProper) tyd.ty;

        ArrayList<VType> actualArgTypes = new ArrayList<>();
        for (Exp argExp : d.getArgs()) {
            argExp.accept(this);
            actualArgTypes.add(stk.pop());
        }
        if (!funcType.matchParamTypes(actualArgTypes)) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() +
                "): Tipos dos argumentos na chamada da função '" + d.getID() + "' estão incorretos."
            );
        }

        ArrayList<VType> declaredReturnTypes = funcType.getReturnTypes();
        ArrayList<LValue> returnTargets = d.getReturnTargets();

        if (declaredReturnTypes.size() != returnTargets.size()) {
            throw new RuntimeException(
                "Erro Semântico (" + d.getLine() + ", " + d.getCol() +
                "): O número de variáveis (" + returnTargets.size() +
                ") não corresponde ao número de retornos da função '" + d.getID() +
                "' (" + declaredReturnTypes.size() + ")."
            );
        }

        for (int i = 0; i < returnTargets.size(); i++) {
            LValue target = returnTargets.get(i);
            VType returnType = declaredReturnTypes.get(i); 

            if (!(target instanceof Var)) {
                throw new RuntimeException("Erro Semântico (" + target.getLine() + ", " + target.getCol() + "): Apenas variáveis simples são suportadas como destino de retorno de função.");
            }

            String varName = ((Var) target).getName();
            VType existingVarType = findVar(varName);

            if (existingVarType == null) {
                declareVar(varName, returnType, target.getLine(), target.getCol());
            } else {
                if (!existingVarType.match(returnType)) {
                    throw new RuntimeException(
                        "Erro Semântico (" + target.getLine() + ", " + target.getCol() +
                        "): Conflito de tipos para a variável '" + varName + "'. A função retorna '" +
                        returnType.toString() + "', mas a variável existente é do tipo '" + existingVarType.toString() + "'."
                    );
                }
            }
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

    public void visit(NewArray e) {
        e.getSizeExp().accept(this);
        VType sizeType = stk.pop();

        if (sizeType.getTypeValue() != CLTypes.INT) {
            throw new RuntimeException("Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): O tamanho do array deve ser uma expressão do tipo Int.");
        }

        e.getType().accept(this);
        VType baseType = stk.pop();

        stk.push(new VTyArr(baseType));
    }

    public void visit(ArrayAccess e) {
        e.getArrayVar().accept(this); 
        VType arrayType = stk.pop();

        if (!(arrayType.getTypeValue() == CLTypes.ARR)) {
            throw new RuntimeException(
                "Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Tentativa de acesso indexado em uma variável que não é um array. Tipo encontrado: " + arrayType.toString()
            );
        }
        VTyArr actualArrayType = (VTyArr) arrayType;

        e.getIndexExp().accept(this);
        VType indexType = stk.pop();

        if (!(indexType.getTypeValue() == CLTypes.INT)) {
            throw new RuntimeException(
                "Erro de tipo (" + e.getLine() + ", " + e.getCol() + "): Índice de array deve ser um inteiro. Tipo encontrado: " + indexType.toString()
            );
        }
        
        stk.push(actualArrayType.getTyArg());
    }

    public void visit(TyArr t) {
        if (t.getElementType() != null) {
            t.getElementType().accept(this);
            VType elementType = stk.pop();
            stk.push(new VTyArr(elementType));
        } else {
            stk.push(new VTyArr(VTyUndetermined.newUndetermined()));
        }
    }

    public static void printEnv(Hashtable < String, VType > t) {
        for (java.util.Map.Entry < String, VType > ent: t.entrySet()) {
            System.out.println(ent.getKey() + " -> " + ent.getValue().toString());
        }
    }

    @Override
    public void visit(DataDef d) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(Decl d) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(TyUser t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(NewObject e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public void visit(DotAccess e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
}