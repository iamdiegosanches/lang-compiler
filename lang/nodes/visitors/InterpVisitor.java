package lang.nodes.visitors;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.dotutils.DotFile;
import lang.nodes.environment.Env;

// import lang.nodes.environment.Env;
import java.util.Stack;
import java.util.Hashtable;
import java.util.ArrayList;

public class InterpVisitor extends LangVisitor {

    private Stack<Hashtable<String, Object>> env;

    private Stack < Object > stk;
    private Hashtable < String, FunDef > fn;
    private boolean retMode;

    public InterpVisitor() {
        stk = new Stack < Object > ();
        fn = new Hashtable < String, FunDef > ();
        retMode = false;
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
        env.peek().put(name, value);
    }

    private Object read(String name) {
        for (int i = env.size() - 1; i >= 0; i--) {
            Hashtable<String, Object> scope = env.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null; // Não encontrado
    }

    public void printEnv() {
        System.out.println(env);
    }

    // visit(Program p) e visit(FunDef d) precisam ser adaptadas para o escopo de função
    public void visit(Program p) {
        FunDef start = null;
        for (FunDef f: p.getFuncs()) {
            fn.put(f.getFname(), f);
            if (f.getFname().equals("inicio")) {
                start = f;
            }
        }
        if (start != null) {
            start.getBody().accept(this);
        } else {
            throw new RuntimeException("Erro: Não há uma função de início no programa.");
        }
    }

    public void visit(FunDef d) {
        d.getBody().accept(this);
    }

    public void visit(Bind d) {
        // Não precisamos fazer nada aqui,
        // O Fcall já toma as providências necessárias !
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
            String varName = d.getVar().getName();
            Object value = stk.pop();
            boolean found = false;
            for (int i = env.size() - 1; i >= 0; i--) {
                if (env.get(i).containsKey(varName)) {
                    env.get(i).put(varName, value);
                    found = true;
                    break;
                }
            }
            if (!found) {
                 throw new RuntimeException("Erro em CAttr: Variável '" + varName + "' não encontrada para atribuição.");
            }
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
                while (count > 0) {
                    d.getBody().accept(this);
                    if (retMode) {
                        break; 
                    }
                    count--;
                }
                leaveScope();
            }
        }
    }

    public void visit(IterateWithVar d) {
        if (!retMode) {
            d.getCondExp().accept(this);
            Object iterSource = stk.pop();
            String varName = d.getIterVar().getName();
            
            if (!(iterSource instanceof Integer)) {
                throw new RuntimeException("Erro de execução (" + d.getLine() + ", " + d.getCol() + "): Expressão de iteração para 'iterate' com variável deve ser um inteiro.");
            }
            
            int count = (Integer) iterSource;
            
            if (count > 0) {
                enterScope();
                
                for (int i = 0; i < count; i++) {
                    store(varName, count - i); 
                    d.getBody().accept(this);
                    if (retMode) {
                        break;
                    }
                }
                leaveScope();
            }
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
            d.getExp().accept(this);
            retMode = true;
        }
    }

    public void visit(Print d) {
        if (!retMode) {
            d.getExp().accept(this);
            System.out.println(stk.pop().toString());
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

        stk.push(left.equals(right));
    }
    
    public void visit(NotEqual e) {
        e.getLeft().accept(this);
        e.getRight().accept(this);

        Object right = stk.pop();
        Object left = stk.pop();

        stk.push(!left.equals(right));
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

    public void visit(FCall e) {
        // FunDef called = fn.get(e.getID());
        // if (called != null) {
        //     for (int j = e.getArgs().size() - 1; j >= 0; j--) {
        //         e.getArgs().get(j).accept(this);
        //     }
        //     Env env1 = env;
        //     env = new Env();
        //     for (Bind b: called.getParams()) {
        //         env.store(b.getVar().getName(), stk.pop());
        //     }
        //     called.accept(this);
        //     retMode = false;
        //     env = env1;
        // } else {
        //     throw new RuntimeException("Chamada a função não delcarada em " + e.getLine() + ", " + e.getCol() + " : " + e.getID());
        // }
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

    public void visit(TyBool t) {}
    public void visit(TyInt t) {}
    public void visit(TyFloat t) {}

}