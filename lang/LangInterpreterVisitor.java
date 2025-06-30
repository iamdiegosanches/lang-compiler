package lang;

import lang.parser.LangBaseVisitor;
import lang.parser.LangParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

public class LangInterpreterVisitor extends LangBaseVisitor<Value> {

    private final Map<String, Value> memory = new HashMap<>();

    @Override
    public Value visitProg(LangParser.ProgContext ctx) {
        return super.visitProg(ctx);
    }

    @Override
    public Value visitDataDef(LangParser.DataDefContext ctx) {
        System.err.println("Interpretador ainda não suporta 'data'.");
        return null;
    }

    @Override
    public Value visitAbstractDataDef(LangParser.AbstractDataDefContext ctx) {
        // Lógica para registrar um novo tipo de dado abstrato.
        System.err.println("Interpretador ainda não suporta 'abstract data'.");
        return null;
    }

    @Override
    public Value visitFunDef(LangParser.FunDefContext ctx) {
        // Registrar uma função
        System.err.println("Interpretador ainda não suporta definição de funções.");
        return null;
    }

    @Override
    public Value visitBlockCmd(LangParser.BlockCmdContext ctx) {
        return super.visitBlockCmd(ctx);
    }

    @Override
    public Value visitIfCmd(LangParser.IfCmdContext ctx) {
        Value condition = visit(ctx.exp());
        
        if (condition.asBoolean()) {
            visit(ctx.cmd(0));
        } else if (ctx.ELSE() != null) {
            visit(ctx.cmd(1));
        }
        
        return null;
    }

    @Override
    public Value visitIterateCmd(LangParser.IterateCmdContext ctx) {
        // Lógica para o comando 'iterate'.
        System.err.println("Interpretador ainda não suporta 'iterate'.");
        return null;
    }

    @Override
    public Value visitReadCmd(LangParser.ReadCmdContext ctx) {
        // Lógica para ler da entrada padrão e atribuir a 'lvalue'.
        System.err.println("Interpretador ainda não suporta 'read'.");
        return null;
    }

    @Override
    public Value visitPrintCmd(LangParser.PrintCmdContext ctx) {
        Value value = visit(ctx.exp());
        System.out.println(value);
        return null;
    }

    @Override
    public Value visitReturnCmd(LangParser.ReturnCmdContext ctx) {
        System.err.println("Interpretador ainda não suporta 'return'.");
        return null;
    }

    @Override
    public Value visitAssignmentCmd(LangParser.AssignmentCmdContext ctx) {
        String varName = ctx.lvalue().getText();
        Value value = visit(ctx.exp());

        memory.put(varName, value);

        return null; 
    }

    @Override
    public Value visitProcCallCmd(LangParser.ProcCallCmdContext ctx) {
        // Lógica para chamada de procedimento (que não retorna valor visível aqui).
        // Ou chamada de função cujo retorno é atribuído a variáveis.
        System.err.println("Interpretador ainda não suporta chamada de procedimentos/funções.");
        return null;
    }

    // --- 3. Expressões (Cadeia de Precedência) ---

    @Override
    public Value visitAndExp(LangParser.AndExpContext ctx) {
        // Lógica para o operador '&&'.
        System.err.println("Interpretador ainda não suporta '&&'.");
        return null;
    }

    @Override
    public Value visitRelationalExp(LangParser.RelationalExpContext ctx) {
        // Lógica para operadores relacionais: <, ==, !=
        System.err.println("Interpretador ainda não suporta operadores relacionais.");
        return null;
    }

    @Override
    public Value visitAddSubExp(LangParser.AddSubExpContext ctx) {
        Value left = visit(ctx.left);
        Value right = visit(ctx.right);
        // Implementação real precisa verificar tipos (Int vs Float).
        if (ctx.operator.getType() == LangParser.PLUS) {
            return new Value(left.asInt() + right.asInt());
        }
        return new Value(left.asInt() - right.asInt());
    }

    @Override
    public Value visitMulDivModExp(LangParser.MulDivModExpContext ctx) {
        // Lógica para operadores: *, /, %
        System.err.println("Interpretador ainda não suporta '*, /, %'.");
        return null;
    }

    @Override
    public Value visitNotExp(LangParser.NotExpContext ctx) {
        // Lógica para negação lógica '!'
        System.err.println("Interpretador ainda não suporta '!'.");
        return null;
    }

    @Override
    public Value visitNegExp(LangParser.NegExpContext ctx) {
        // Lógica para menos unário '-'
        System.err.println("Interpretador ainda não suporta menos unário.");
        return null;
    }

    // -- Expressões Primárias (Regra 'primaryExp') ---


    @Override
    public Value visitLvalueExp(LangParser.LvalueExpContext ctx) {
        String varName = ctx.lvalue().getText();
        if (memory.containsKey(varName)) {
            return memory.get(varName);
        }
        throw new RuntimeException("Erro: variável '" + varName + "' não definida.");
    }
    
    @Override
    public Value visitFunCallExp(LangParser.FunCallExpContext ctx) {
        // Lógica para chamada de função que é parte de uma expressão.
        System.err.println("Interpretador ainda não suporta chamada de função em expressões.");
        return null;
    }

    @Override
    public Value visitNewExp(LangParser.NewExpContext ctx) {
        // Lógica para alocação de memória com 'new'.
        System.err.println("Interpretador ainda não suporta 'new'.");
        return null;
    }

    @Override
    public Value visitParenExp(LangParser.ParenExpContext ctx) {
        // Simplesmente visita a expressão interna para respeitar a precedência.
        return visit(ctx.exp());
    }

    @Override
    public Value visitTrueExp(LangParser.TrueExpContext ctx) {
        return new Value(true);
    }

    @Override
    public Value visitFalseExp(LangParser.FalseExpContext ctx) {
        return new Value(false);
    }

    @Override
    public Value visitNullExp(LangParser.NullExpContext ctx) {
        return new Value(null);
    }
    
    @Override
    public Value visitIntExp(LangParser.IntExpContext ctx) {
        return new Value(Integer.valueOf(ctx.INT().getText()));
    }

    @Override
    public Value visitFloatExp(LangParser.FloatExpContext ctx) {
        return new Value(Float.valueOf(ctx.FLOAT().getText()));
    }
    
    @Override
    public Value visitCharExp(LangParser.CharExpContext ctx) {
        // Remove as aspas simples do início e fim.
        String text = ctx.CHAR().getText();
        return new Value(text.substring(1, text.length() - 1));
    }

    // lendo a variável da memória
    @Override
    public Value visitLvalueExp(LangParser.LvalueExpContext ctx) {
        String varName = ctx.lvalue().getText();
        if (memory.containsKey(varName)) {
            return memory.get(varName);
        }
        throw new RuntimeException("Erro variável '" + varName + "' não definida.");
    }

}