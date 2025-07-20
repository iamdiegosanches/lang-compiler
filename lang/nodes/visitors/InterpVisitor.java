package lang.nodes.visitors;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.visitors.tychkvisitor.VTyUndetermined;
import lang.nodes.*;

import java.util.Stack;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Scanner;

public class InterpVisitor extends LangVisitor {

    private Stack<Hashtable<String, Object>> env;

    private Stack < Object > stk;
    private Hashtable < String, FunDef > fn;
    private boolean retMode;
    private Object returnValue;

    private Scanner scanner = new Scanner(System.in);

    public InterpVisitor() {
        stk = new Stack < Object > ();
        fn = new Hashtable < String, FunDef > ();
        retMode = false;
        returnValue = null;
        env = new Stack<>();
        env.push(new Hashtable<String, Object>()); // Escopo global
    }

    private void enterScope() {
        env.push(new Hashtable<String, Object>());
    }

    private void leaveScope() {
        env.pop();
    }

    private void store(String name, Object value) {
        for (int i = env.size() - 1; i >= 0; i--) {
            Hashtable<String, Object> scope = env.get(i);
            if (scope == null) {
                throw new RuntimeException("Erro interno: Escopo nulo encontrado na pilha de ambientes em índice " + i + ". Isso não deveria acontecer.");
            }
            if (scope.containsKey(name)) {
                scope.put(name, value);
                return;
            }
        }
        env.peek().put(name, value);
    }

    private Object read(String name) {
        for (int i = env.size() - 1; i >= 0; i--) {
            Hashtable<String, Object> scope = env.get(i);
            if (scope == null) { // Verificação defensiva
                throw new RuntimeException("Erro interno: Escopo nulo encontrado na pilha de ambientes ao ler variável.");
            }
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null; // Não encontrado
    }

    public void printEnv() {
        System.out.println(env);
    }

    public void visit(Program p) {
        FunDef start = null;
        for (FunDef f: p.getFuncs()) {
            fn.put(f.getFname(), f);
            if (f.getFname().equals("main")) {
                start = f;
            }
        }
        if (start != null) {
            if (!start.getParams().isEmpty() || !start.getRet().isEmpty()) {
                throw new RuntimeException("Erro: A função 'main' deve ter 0 parâmetros e 0 retornos.");
            }
            start.getBody().accept(this);
        } else {
            throw new RuntimeException("Erro: Não há uma função 'main' no programa.");
        }
    }

    public void visit(FunDef d) {
        retMode = false;
        returnValue = null;
        d.getBody().accept(this);
    }

    public void visit(Bind d) {
        // Não precisamos fazer nada aqui,
    }

    public void visit(CSeq d) {
        if (!retMode) {
            d.getLeft().accept(this);
            if (!retMode) {
                d.getRight().accept(this);
            }
        }
    }

    public void visit(CAttr d) {
        if (!retMode) {
            d.getExp().accept(this);
            Object value = stk.pop();

        LValue lvalue = d.getVar();

        if (lvalue instanceof Var) {
            String varName = ((Var) lvalue).getName();
            store(varName, value); 
        } else if (lvalue instanceof ArrayAccess) {
            ArrayAccess arrayAccess = (ArrayAccess) lvalue;
            
            arrayAccess.getArrayVar().accept(this);
            Object arrayObj = stk.pop();
            
            arrayAccess.getIndexExp().accept(this);
            Object indexObj = stk.pop();

            if (!(arrayObj instanceof Object[])) {
                throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Tentativa de acesso como array em uma variável não é um array.");
            }
            if (!(indexObj instanceof Integer)) {
                throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Índice de array deve ser um inteiro.");
            }

            Object[] array = (Object[]) arrayObj;
            int index = (Integer) indexObj;

            if (index < 0 || index >= array.length) {
                throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Índice de array fora dos limites: " + index + ", tamanho: " + array.length);
            }
            array[index] = value;
        } else {
            throw new RuntimeException("Erro: LValue não suportado na atribuição.");
        }
    }

    public void visit(CDecl d) {
        if (!retMode) {
            d.getExp().accept(this);
            Object value = stk.pop();
            store(d.getVar().getName(), value);
        }
    }

    public void visit(CNull d) {}

    public void visit(Loop d) {
        if (!retMode) {
            d.getCond().accept(this);
            Object loopLimit = stk.pop();
            if (!(loopLimit instanceof Integer)) {
                throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Condição do 'iterate' simples deve ser um inteiro.");
            }

            int count = (Integer) loopLimit;

            if (count > 0) {
                enterScope(); 
                for (int i = 0; i < count; i++) {
                    d.getBody().accept(this);
                    if (retMode) {
                        break; 
                    }
                }
                leaveScope();
            }
        }
    }

    public void visit(IterateWithVar d) {
        if (!retMode) {
            d.getCondExp().accept(this);
            Object iterSource = stk.pop();
            String varName = ((Var) d.getIterVar()).getName(); 

            enterScope();

            if (iterSource instanceof Integer) {
                int count = (Integer) iterSource;
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        store(varName, count - i);
                        d.getBody().accept(this);
                        if (retMode) {
                            break;
                        }
                    }
                }
            } else if (iterSource instanceof Object[]) {
                Object[] array = (Object[]) iterSource;
                for (int i = 0; i < array.length; i++) {
                    store(varName, array[i]);
                    d.getBody().accept(this);
                }
            } else {
                throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Expressão de iteração para 'iterate' com variável deve ser um inteiro ou um array. Tipo encontrado: " + iterSource.getClass().getSimpleName());
            }
            
            leaveScope();
        }
    }

    public void visit(If d) {
        if (!retMode) {
            d.getCond().accept(this);
            if ((boolean) stk.pop()) {
                enterScope();
                d.getThn().accept(this);
                leaveScope();
            } else {
                if (d.getEls() != null) {
                    enterScope();
                    d.getEls().accept(this);
                    leaveScope();
                }
            }
        }
    }

    public void visit(Return d) {
        if (!retMode) {
            ArrayList<Object> returnedValues = new ArrayList<>();
            for (Exp exp : d.getExp()) {
                exp.accept(this);
                returnedValues.add(stk.pop()); 
            }
            returnValue = returnedValues; 
            retMode = true;
        }
    }

    public void visit(Print d) {
        if (!retMode) {
            d.getExp().accept(this);
            System.out.println(stk.pop());
        }
    }

    public void visit(Read d) {
        if (!retMode) {
            LValue lv = d.getTarget();
<<<<<<< HEAD
            System.out.print(lv.getName() + " = ");
=======
            if (!(lv instanceof Var)) {
                throw new RuntimeException("Erro: read só suporta variáveis simples por enquanto.");
            }

            String varName = ((Var) lv).getName();
            Object currentValue = read(varName);

            if (currentValue == null) {
                throw new RuntimeException("Erro em read (" + d.getLine() + "," + d.getCol() + "): Não é possível ler para uma variável não inicializada ou de tipo complexo nulo.");
            }

            System.out.print(varName + " = ");
>>>>>>> upstream/main
            String input = scanner.nextLine();
            Object newValue;

<<<<<<< HEAD
            if (lv instanceof Var) {
                String varName = ((Var) lv).getName();
                boolean found = false;
                for (int i = env.size() - 1; i >= 0; i--) {
                    Hashtable<String, Object> scope = env.get(i);
                    if (scope == null) {
                        throw new RuntimeException("Erro interno: Escopo nulo encontrado na pilha de ambientes ao ler variável.");
                    }
                    if (scope.containsKey(varName)) {
                        env.get(i).put(varName, value);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new RuntimeException("Erro em Read: Variável '" + varName + "' não encontrada.");
                }
            } else if (lv instanceof ArrayAccess) {
                ArrayAccess arrayAccess = (ArrayAccess) lv;

                arrayAccess.getArrayVar().accept(this);
                Object arrayObj = stk.pop();

                arrayAccess.getIndexExp().accept(this);
                Object indexObj = stk.pop();

                if (!(arrayObj instanceof Object[])) {
                    throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Tentativa de acesso como array em uma variável não é um array.");
                }
                if (!(indexObj instanceof Integer)) {
                    throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Índice de array deve ser um inteiro.");
                }

                Object[] array = (Object[]) arrayObj;
                int index = (Integer) indexObj;

                if (index < 0 || index >= array.length) {
                    throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Índice de array fora dos limites: " + index + ", tamanho: " + array.length);
                }
                array[index] = value;
            } else {
                throw new RuntimeException("Erro: read só suporta variáveis simples ou acesso a elementos de array por enquanto.");
            }
=======
            try {
                if (currentValue instanceof Integer) {
                    newValue = Integer.parseInt(input);
                } else if (currentValue instanceof Float) {
                    newValue = Float.parseFloat(input);
                } else if (currentValue instanceof Boolean) {
                    if (input.equals("true")) {
                        newValue = true;
                    } else if (input.equals("false")) {
                        newValue = false;
                    } else {
                        throw new NumberFormatException();
                    }
                } else if (currentValue instanceof Character) {
                    if (input.length() == 1) {
                        newValue = input.charAt(0);
                    } else {
                        throw new NumberFormatException();
                    }
                } else {
                    throw new RuntimeException("Tipo da variável '" + varName + "' não suportado pelo comando read.");
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException("Erro em read (" + d.getLine() + "," + d.getCol() + "): A entrada '" + input + "' é inválida para o tipo da variável '" + varName + "'.");
            }

            store(varName, newValue);
>>>>>>> upstream/main
        }
    }

    public void visit(And e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);

        Object right = stk.pop();
        Object left = stk.pop();

        if (left instanceof Boolean && right instanceof Boolean) {
            stk.push((Boolean)left && (Boolean)right);
        } else {
            throw new RuntimeException("Operação '&&' não permitida entre os tipos " + e.getLine() + ", " + e.getCol() + ". Esperado 'Bool' em ambos os operandos.");
        }
    }

    public void visit(BinOp e) {}

    public void visit(UnOp e) {}

    public void visit(Sub e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        if (stk.peek() instanceof Integer) {
            Integer id = (Integer) stk.pop();
            if (stk.peek() instanceof Integer) {
                Integer ie = (Integer) stk.pop();
                stk.push(ie - id);
            }
        } else if (stk.peek() instanceof Float) {
            Float id = (Float) stk.pop();
            if (stk.peek() instanceof Float) {
                Float ie = (Float) stk.pop();
                stk.push(ie - id);
            }
        } else {
            throw new RuntimeException("Operção não permitida entre os tipos " + e.getLine() + ", " + e.getCol() + ".");
        }
    }

    public void visit(Plus e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        if (stk.peek() instanceof Integer) {
            Integer id = (Integer) stk.pop();
            if (stk.peek() instanceof Integer) {
                Integer ie = (Integer) stk.pop();
                stk.push(ie + id);
            }
        } else if (stk.peek() instanceof Float) {
            Float id = (Float) stk.pop();
            if (stk.peek() instanceof Float) {
                Float ie = (Float) stk.pop();
                stk.push(ie + id);
            }
        } else {
            throw new RuntimeException("Operção não permitida entre os tipos " + e.getLine() + ", " + e.getCol() + ".");
        };
    }

    public void visit(Times e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        if (stk.peek() instanceof Integer) {
            Integer id = (Integer) stk.pop();
            if (stk.peek() instanceof Integer) {
                Integer ie = (Integer) stk.pop();
                stk.push(ie * id);
            }
        } else if (stk.peek() instanceof Float) {
            Float id = (Float) stk.pop();
            if (stk.peek() instanceof Float) {
                Float ie = (Float) stk.pop();
                stk.push(ie * id);
            }
        } else {
            throw new RuntimeException("Operção não permitida entre os tipos " + e.getLine() + ", " + e.getCol() + ".");
        }
    }

    public void visit(Div e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        if (stk.peek() instanceof Integer) {
            Integer id = (Integer) stk.pop();
            if (stk.peek() instanceof Integer) {
                Integer ie = (Integer) stk.pop();
                stk.push(ie / id);
            }
        } else if (stk.peek() instanceof Float) {
            Float id = (Float) stk.pop();
            if (stk.peek() instanceof Float) {
                Float ie = (Float) stk.pop();
                stk.push(ie / id);
            }
        } else {
            throw new RuntimeException("Operção não permitida entre os tipos " + e.getLine() + ", " + e.getCol() + ".");
        }
    }

    public void visit(Mod e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        if (stk.peek() instanceof Integer) {
            Integer id = (Integer) stk.pop();
            if (stk.peek() instanceof Integer) {
                Integer ie = (Integer) stk.pop();
                stk.push(ie % id);
            } else {
                throw new RuntimeException("Operção não permitida entre os tipos " + e.getLine() + ", " + e.getCol() + ". O operador de módulo '%' espera operandos do tipo 'Int'.");
            }
        } else {
            throw new RuntimeException("Operção não permitida entre os tipos " + e.getLine() + ", " + e.getCol() + ". O operador de módulo '%' espera operandos do tipo 'Int'.");
        }
    }

    public void visit(LessThan e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);
        if (stk.peek() instanceof Integer) {
            Integer right = (Integer) stk.pop();
            if (stk.peek() instanceof Integer) {
                Integer left = (Integer) stk.pop();
                stk.push(left < right ? true : false);
            }
        } else if (stk.peek() instanceof Float) {
            Float right = (Float) stk.pop();
            if (stk.peek() instanceof Float) {
                Float left = (Float) stk.pop();
                stk.push(left < right ? true : false);
            }
        } else {
            throw new RuntimeException("Operção não ptermitida entre os tipos " + e.getLine() + ", " + e.getCol() + ".");
        }
    }

    public void visit(Equal e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);

        Object right = stk.pop();
        Object left = stk.pop();

        if (left instanceof Character && right instanceof Character) {
            stk.push(left.equals(right));
        } else if (left instanceof Boolean && right instanceof Boolean) {
            stk.push(left.equals(right));
        } else if (left instanceof Integer && right instanceof Integer) {
            stk.push(left.equals(right));
        } else if (left instanceof Float && right instanceof Float) {
            stk.push(left.equals(right));
        } else if (left == null || right == null) { 
            stk.push(left == right); 
        }
        else {
            throw new RuntimeException("Operação '==' não permitida entre os tipos " + left.getClass().getSimpleName() + " e " + right.getClass().getSimpleName() + " em " + e.getLine() + ", " + e.getCol() + ".");
        }
    }
    
    public void visit(NotEqual e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);

        Object right = stk.pop();
        Object left = stk.pop();

        if (left instanceof Character && right instanceof Character) {
            stk.push(!left.equals(right));
        } else if (left instanceof Boolean && right instanceof Boolean) {
            stk.push(!left.equals(right));
        } else if (left instanceof Integer && right instanceof Integer) {
            stk.push(!left.equals(right));
        } else if (left instanceof Float && right instanceof Float) {
            stk.push(!left.equals(right));
        } else if (left == null || right == null) { 
            stk.push(left != right); 
        }
        else {
            throw new RuntimeException("Operação '!=' não permitida entre os tipos " + left.getClass().getSimpleName() + " e " + right.getClass().getSimpleName() + " em " + e.getLine() + ", " + e.getCol() + ".");
        }
    }

    public void visit(Not e) {
        e.getRight().accept(this);

        if (stk.peek() instanceof Boolean) {
            Boolean right = (Boolean) stk.pop();
            if (right == true) {
                stk.push(false);
            } else {
                stk.push(true);
            }
        } else {
            throw new RuntimeException(" Operação não permitida com o tipo " + e.getLine() + ", " + e.getCol() + ".");
        }
    }

    public void visit(UMinus e) {
        e.getRight().accept(this);

        Object value = stk.pop();

        if (value instanceof Integer) {
            stk.push(-(Integer) value);
        } else if (value instanceof Float) {
            stk.push(-(Float) value);
        } else {
            throw new RuntimeException(" Operação não permitida com o tipo " + e.getLine() + ", " + e.getCol() + ".");
        }
    }

    public void visit(Var e) {
        Object val = read(e.getName());
        if (val != null) {
            stk.push(val);
        } else {
            throw new RuntimeException("Variável não declarada " + e.getLine() + ", " + e.getCol() + " : " + e.getName());
        }
    }

    @Override
    public void visit(FCall e) {
        FunDef called = fn.get(e.getID());
        if (called != null) {
            
            ArrayList<Object> evaluatedArgs = new ArrayList<>();
            for (Exp argExp : e.getArgs()) {
                argExp.accept(this);
                evaluatedArgs.add(stk.pop());
            }

            if (evaluatedArgs.size() != called.getParams().size()) {
                throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): Número de argumentos incompatível para a função '" + e.getID() + "'. Esperado " + called.getParams().size() + ", encontrado " + evaluatedArgs.size() + ".");
            }
            
            Stack<Hashtable<String, Object>> callerEnv = this.env;
            this.env = new Stack<>();
            enterScope();

            for (int i = 0; i < called.getParams().size(); i++) {
                Bind paramBind = called.getParams().get(i);
                Object argValue = evaluatedArgs.get(i);
                store(paramBind.getVar().getName(), argValue);
            }

            called.accept(this);
            Object result = returnValue;
            retMode = false;

            this.env = callerEnv;
            
            if (e.getReturnIndex() != null) {
                if (!(result instanceof ArrayList)) {
                    throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): Tentativa de indexar um valor não-lista de retorno da função '" + e.getID() + "'.");
                }
                ArrayList<Object> returnedList = (ArrayList<Object>) result;

                e.getReturnIndex().accept(this);
                Object idxValue = stk.pop();

                if (!(idxValue instanceof Integer)) {
                    throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): O índice de retorno da função '" + e.getID() + "' deve ser um inteiro.");
                }
                int index = (Integer) idxValue;

                if (index < 0 || index >= returnedList.size()) {
                    throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): Índice de retorno fora dos limites para a função '" + e.getID() + "'. Índice: " + index + ", Tamanho: " + returnedList.size() + ".");
                }
                stk.push(returnedList.get(index));
            } else {
                stk.push(result);
            }

        } else {
            throw new RuntimeException("Chamada a função não declarada em " + e.getLine() + ", " + e.getCol() + " : " + e.getID());
        }
    }

    @Override
    public void visit(FCallCommand d) {
        if (!retMode) {
            FunDef called = fn.get(d.getID());
            if (called != null) {
                
                ArrayList<Object> evaluatedArgs = new ArrayList<>();
                for (Exp argExp : d.getArgs()) {
                    argExp.accept(this);
                    evaluatedArgs.add(stk.pop());
                }

                if (evaluatedArgs.size() != called.getParams().size()) {
                    throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Número de argumentos incompatível para a função '" + d.getID() + "'. Esperado " + called.getParams().size() + ", encontrado " + evaluatedArgs.size() + ".");
                }

                Stack<Hashtable<String, Object>> callerEnv = this.env;
                this.env = new Stack<>();
                enterScope();

                for (int i = 0; i < called.getParams().size(); i++) {
                    Bind paramBind = called.getParams().get(i);
                    Object argValue = evaluatedArgs.get(i);
                    store(paramBind.getVar().getName(), argValue);
                }

                called.accept(this);
                Object result = returnValue;
                retMode = false;

                this.env = callerEnv;

                if (!(result instanceof ArrayList)) {
                    throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Função '" + d.getID() + "' não retornou uma lista de valores para atribuição múltipla.");
                }
                ArrayList<Object> returnedList = (ArrayList<Object>) result;

                if (returnedList.size() != d.getReturnTargets().size()) {
                    throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Número de valores retornados pela função '" + d.getID() + "' (" + returnedList.size() + ") não corresponde ao número de destinos de atribuição (" + d.getReturnTargets().size() + ").");
                }

                for (int i = 0; i < returnedList.size(); i++) {
                    LValue target = d.getReturnTargets().get(i);
                    Object valueToAssign = returnedList.get(i);

                    if (!(target instanceof Var)) {
                        throw new RuntimeException("Erro de execução: Atribuição de retorno a LValue não suportada (apenas variáveis simples por enquanto).");
                    }
                    store(((Var) target).getName(), valueToAssign);
                }

            } else {
                throw new RuntimeException("Chamada a função não declarada em " + d.getLine() + ", " + d.getCol() + " : " + d.getID());
            }
        }
    }

    public void visit(IntLit e) {
        stk.push(e.getValue());
    }
    public void visit(BoolLit e) {
        stk.push(e.getValue());
    }
    public void visit(FloatLit e) {
        stk.push(e.getValue());
    }

    public void visit(TyChar t) { }
    public void visit(CharLit e) {
        stk.push(e.getValue());
    }

    public void visit(NullLit e) {
        stk.push(null);
    }

    public void visit(TyBool t) {}
    public void visit(TyInt t) {}
    public void visit(TyFloat t) {}
    public void visit(TyArr t) {}
    public void visit(VTyUndetermined t) {}

    public void visit(NewArray e) {
        e.getSizeExp().accept(this);
        Object sizeObj = stk.pop();

        if (!(sizeObj instanceof Integer)) {
            throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): Tamanho do array deve ser um inteiro.");
        }
        int size = (Integer) sizeObj;

        if (size < 0) {
            throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): Tamanho do array não pode ser negativo.");
        }

        Object[] newArray = new Object[size];
        // Inicializa o array com nulls, pois o tipo do elemento não é especificado na alocação
        for (int i = 0; i < size; i++) {
            newArray[i] = null;
        }
        stk.push(newArray); // Coloca o novo array na pilha
    }

    public void visit(ArrayAccess e) {
        e.getArrayVar().accept(this); 
        Object arrayObj = stk.pop();
        
        e.getIndexExp().accept(this); 
        Object indexObj = stk.pop();

        if (!(arrayObj instanceof Object[])) {
            throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): Tentativa de acesso como array em uma variável que não é um array.");
        }
        if (!(indexObj instanceof Integer)) {
            throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): Índice de array deve ser um inteiro.");
        }

        Object[] array = (Object[]) arrayObj;
        int index = (Integer) indexObj;

        if (index < 0 || index >= array.length) {
            throw new RuntimeException("Erro de execução (" + e.getLine() + ", " + e.getCol() + "): Índice de array fora dos limites: " + index + ", tamanho: " + array.length);
        }
        stk.push(array[index]);
    }


}