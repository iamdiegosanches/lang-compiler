package lang;

import lang.parser.LangBaseVisitor;
import lang.parser.LangParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

public class LangInterpreterVisitor extends LangBaseVisitor<Value> {

    private final Map<String, Value> memory = new HashMap<>();

    @Override
    public Value visitIntExp(LangParser.IntExpContext ctx) {
        return new Value(Integer.valueOf(ctx.INT().getText()));
    }

    @Override
    public Value visitTrueExp(LangParser.TrueExpContext ctx) {
        return new Value(true);
    }

    @Override
    public Value visitFalseExp(LangParser.FalseExpContext ctx) {
        return new Value(false);
    }

    // Falta verificar tipos para funcionar perfeitamente (float vs Int)
    @Override
    public Value visitAddSubExp(LangParser.AddSubExpContext ctx) {
        Value left = visit(ctx.left);
        Value right = visit(ctx.right); 

        if (ctx.operator.getType() == LangParser.PLUS) {
            return new Value(left.asInt() + right.asInt());
        } else {
            return new Value(left.asInt() - right.asInt());
        }
    }

    // ---------------------------------------------------------------

    // Colocando a variável na memória
    @Override
    public Value visitAssignmentCmd(LangParser.AssignmentCmdContext ctx) {
        String varName = ctx.lvalue().getText();
        Value value = visit(ctx.exp()); // Agora funciona corretamente

        memory.put(varName, value);

        return null; // Comandos podem retornar null se não produzem valor
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

    @Override
    public Value visitPrintCmd(LangParser.PrintCmdContext ctx) {
        Value value = visit(ctx.exp());
        System.out.println(value);
        return null;
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
}