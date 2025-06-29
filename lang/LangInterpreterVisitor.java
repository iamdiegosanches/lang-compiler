package lang;

import lang.parser.LangBaseVisitor;
import lang.parser.LangParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class LangInterpreterVisitor extends LangBaseVisitor<Object> {

    // --- Classes Internas para Representar Estruturas de Dados ---
    private static class LangObject {
        private final String typeName;
        private final Map<String, Object> fields = new HashMap<>();
        LangObject(String typeName) { this.typeName = typeName; }
        public String getTypeName() { return typeName; }
        public void set(String field, Object value) { fields.put(field, value); }
        public Object get(String field) { return fields.get(field); }
    }

    private static class LangArray {
        private final Object[] values;
        LangArray(int size) { this.values = new Object[size]; }
        public int size() { return values.length; }
        public void set(int index, Object value) { values[index] = value; }
        public Object get(int index) { return values[index]; }
    }

    private static class ReturnValue extends RuntimeException {
        final Object[] values;
        ReturnValue(Object[] values) {
            super(null, null, false, false);
            this.values = values;
        }
    }

    private static class FunctionDefinition {
        final LangParser.FuncContext context;
        final String parentDataName;
        FunctionDefinition(LangParser.FuncContext context, String parentDataName) {
            this.context = context;
            this.parentDataName = parentDataName;
        }
    }

    // --- Estado do Interpretador ---
    private final Stack<Map<String, Object>> memory = new Stack<>();
    private final Map<String, FunctionDefinition> functions = new HashMap<>();
    private final Map<String, LangParser.DataContext> typeDefs = new HashMap<>();
    private final Set<String> abstractTypes = new HashSet<>();
    private final Scanner inputScanner = new Scanner(System.in);
    private String currentFunctionParent = null;

    public LangInterpreterVisitor() {
        memory.push(new HashMap<>()); // Escopo Global
    }

    // --- Métodos Auxiliares ---
    private void assign(String name, Object value) {
        for (int i = memory.size() - 1; i >= 0; i--) {
            if (memory.get(i).containsKey(name)) {
                memory.get(i).put(name, value);
                return;
            }
        }
        memory.peek().put(name, value);
    }

    private Object resolve(String name) {
        for (int i = memory.size() - 1; i >= 0; i--) {
            if (memory.get(i).containsKey(name)) {
                return memory.get(i).get(name);
            }
        }
        throw new RuntimeException("Erro: Variável '" + name + "' não foi declarada.");
    }
    
    private Object[] resolveLValueForAssignment(LangParser.LvalueContext ctx) {
        Object current = resolve(ctx.ID(0).getText());
        int idIndex = 1;
        int exprIndex = 0;
        
        for (int i = 1; i < ctx.getChildCount() - 2; i++) {
             var child = ctx.getChild(i);
             if (child instanceof TerminalNode && ((TerminalNode)child).getSymbol().getType() == LangParser.DOT) {
                 String fieldName = ctx.ID(idIndex++).getText();
                 checkAbstractAccess(((LangObject)current).getTypeName(), fieldName);
                 current = ((LangObject)current).get(fieldName);
             } else if (child instanceof TerminalNode && ((TerminalNode)child).getSymbol().getType() == LangParser.OPEN_BRACKET) {
                 int index = (Integer) visit(ctx.expr(exprIndex++));
                 current = ((LangArray)current).get(index);
             }
        }

        var lastOp = ctx.getChild(ctx.getChildCount() - 2);
        if (lastOp instanceof TerminalNode && ((TerminalNode)lastOp).getSymbol().getType() == LangParser.DOT) {
            String fieldName = ctx.ID(idIndex).getText();
            checkAbstractAccess(((LangObject)current).getTypeName(), fieldName);
            return new Object[]{current, fieldName};
        } else if (lastOp instanceof TerminalNode && ((TerminalNode)lastOp).getSymbol().getType() == LangParser.OPEN_BRACKET) {
            return new Object[]{current, visit(ctx.expr(exprIndex))};
        }
        
        return new Object[]{null, ctx.ID(0).getText()};
    }

    private void checkAbstractAccess(String typeName, String fieldName) {
        if (abstractTypes.contains(typeName)) {
            if (currentFunctionParent == null || !currentFunctionParent.equals(typeName)) {
                throw new RuntimeException("Erro: Acesso ao campo '" + fieldName + "' do tipo abstrato '" + typeName + "' de fora de seu escopo.");
            }
        }
    }
    
    // --- Ponto de Entrada e Definições ---
    @Override
    public Object visitProg(LangParser.ProgContext ctx) {
        for (LangParser.DefContext def : ctx.def()) {
            if (def.data() != null) {
                String typeName = def.data().TYID().getText();
                typeDefs.put(typeName, def.data());
                if (def.data().ABSTRACT() != null) abstractTypes.add(typeName);
            } else if (def.func() != null) {
                functions.put(def.func().ID().getText(), new FunctionDefinition(def.func(), null));
            }
        }
        FunctionDefinition mainFuncDef = functions.get("main");
        if (mainFuncDef == null) throw new RuntimeException("Erro: Função 'main' não encontrada.");
        if (mainFuncDef.context.params() != null) throw new RuntimeException("Erro: Função 'main' não deve ter parâmetros.");
        visit(mainFuncDef.context);
        return null;
    }

    // --- Comandos ---
    @Override public Object visitBlock(LangParser.BlockContext ctx) { memory.push(new HashMap<>()); try { return super.visitBlock(ctx); } finally { memory.pop(); } }
    @Override
    public Object visitStm(LangParser.StmContext ctx) {
        if (ctx.lvalue() != null && ctx.ASSIGN() != null) {
            Object valueToAssign = visit(ctx.expr());
            if (ctx.lvalue().getChildCount() == 1) {
                assign(ctx.lvalue().getText(), valueToAssign);
            } else {
                Object[] resolution = resolveLValueForAssignment(ctx.lvalue());
                Object container = resolution[0]; Object key = resolution[1];
                if (container instanceof LangObject) ((LangObject) container).set((String) key, valueToAssign);
                else if (container instanceof LangArray) ((LangArray) container).set((Integer) key, valueToAssign);
            }
            return null;
        }
        return super.visitChildren(ctx);
    }
    @Override public Object visitDecl(LangParser.DeclContext ctx) { assign(ctx.ID().getText(), null); return null; }
    @Override public Object visitPrint(LangParser.PrintContext ctx) { System.out.println(visit(ctx.expr())); return null; }
    @Override public Object visitRead(LangParser.ReadContext ctx) { /* ... */ return null; }
    @Override public Object visitIfStm(LangParser.IfStmContext ctx) { if ((Boolean) visit(ctx.expr())) visit(ctx.block(0)); else if (ctx.ELSE() != null) visit(ctx.block(1)); return null; }
    @Override public Object visitIterateStm(LangParser.IterateStmContext ctx) { /* ... */ return null; }

    // --- Expressões ---
    @Override public Object visitExpr(LangParser.ExprContext ctx) { if (ctx.bterm().size() == 1) return visit(ctx.bterm(0)); return (Boolean) visit(ctx.bterm(0)) && (Boolean) visit(ctx.bterm(1)); }
    @Override public Object visitBterm(LangParser.BtermContext ctx) { if (ctx.cterm().size() == 1) return visit(ctx.cterm(0)); Object left = visit(ctx.cterm(0)); Object right = visit(ctx.cterm(1)); return ctx.op.getType() == LangParser.EQUAL_EQUAL ? Objects.equals(left, right) : !Objects.equals(left, right); }
    @Override public Object visitCterm(LangParser.CtermContext ctx) { if (ctx.aterm().size() == 1) return visit(ctx.aterm(0)); double left = ((Number) visit(ctx.aterm(0))).doubleValue(); double right = ((Number) visit(ctx.aterm(1))).doubleValue(); return ctx.op.getType() == LangParser.LESS_THAN ? left < right : left > right; }
    @Override public Object visitAterm(LangParser.AtermContext ctx) { if (ctx.mterm().size() == 1) return visit(ctx.mterm(0)); double left = ((Number) visit(ctx.mterm(0))).doubleValue(); double right = ((Number) visit(ctx.mterm(1))).doubleValue(); return ctx.op.getType() == LangParser.PLUS ? left + right : left - right; }
    @Override public Object visitMterm(LangParser.MtermContext ctx) { if (ctx.uterm().size() == 1) return visit(ctx.uterm(0)); double left = ((Number) visit(ctx.uterm(0))).doubleValue(); double right = ((Number) visit(ctx.uterm(1))).doubleValue(); if (ctx.op.getType() == LangParser.MULT) return left * right; if (ctx.op.getType() == LangParser.DIV) return left / right; return ((Number)left).intValue() % ((Number)right).intValue(); }
    @Override public Object visitUterm(LangParser.UtermContext ctx) { if (ctx.op == null) return visit(ctx.pterm()); Object val = visit(ctx.pterm()); if (ctx.op.getType() == LangParser.NOT) return !(Boolean) val; return -((Number) val).doubleValue(); }
    @Override public Object visitLiteral(LangParser.LiteralContext ctx) { if (ctx.INT() != null) return Integer.parseInt(ctx.INT().getText()); if (ctx.FLOAT() != null) return Float.parseFloat(ctx.FLOAT().getText()); if (ctx.TRUE() != null) return true; if (ctx.FALSE() != null) return false; return null; }
    @Override public Object visitLvalue(LangParser.LvalueContext ctx) { Object current = resolve(ctx.ID(0).getText()); int idIndex = 1; int exprIndex = 0; for (int i = 1; i < ctx.getChildCount(); i++) { var child = ctx.getChild(i); if (child instanceof TerminalNode && ((TerminalNode)child).getSymbol().getType() == LangParser.DOT) { String fieldName = ctx.ID(idIndex++).getText(); checkAbstractAccess(((LangObject)current).getTypeName(), fieldName); current = ((LangObject)current).get(fieldName); } else if (child instanceof TerminalNode && ((TerminalNode)child).getSymbol().getType() == LangParser.OPEN_BRACKET) { int index = (Integer) visit(ctx.expr(exprIndex++)); current = ((LangArray)current).get(index); } } return current; }

    // CORREÇÃO APLICADA AQUI
    @Override
    public Object visitPterm(LangParser.PtermContext ctx) {
        // Caso: ( expr )
        if (ctx.expr() != null && ctx.OPEN_PAREN() != null) {
            return visit(ctx.expr());
        }
        
        if (ctx.lvalue() != null) return visit(ctx.lvalue());
        if (ctx.literal() != null) return visit(ctx.literal());

        // Caso: new type [ expr ] ou new type
        if (ctx.NEW() != null) {
            if (ctx.type().OPEN_BRACKET().isEmpty()) { // É um registro
                String typeName = ctx.type().btype().TYID().getText();
                LangParser.DataContext typeCtx = typeDefs.get(typeName);
                if (typeCtx == null) throw new RuntimeException("Erro: Tipo '" + typeName + "' não definido.");
                LangObject newObj = new LangObject(typeName);
                if (typeCtx.decls() != null) {
                    for (LangParser.DeclContext decl : typeCtx.decls().decl()) {
                        newObj.set(decl.ID().getText(), null);
                    }
                }
                return newObj;
            } else { // É um array
                int size = (Integer) visit(ctx.expr());
                return new LangArray(size);
            }
        }

        // Caso: exprFcall [ expr ]
        if (ctx.exprFcall() != null) {
            Object result = visit(ctx.exprFcall());
            if (ctx.OPEN_BRACKET() == null) {
                 if (result != null) throw new RuntimeException("Erro: Retorno de função deve ser indexado em uma expressão.");
                 return null;
            }
            if (!(result instanceof ReturnValue)) throw new RuntimeException("Erro: Tentativa de indexar uma função que não retornou valor.");
            
            int index = (Integer) visit(ctx.expr()); // Removido o índice (0)
            Object[] values = ((ReturnValue)result).values;
            if (index < 0 || index >= values.length) throw new RuntimeException("Erro: Índice de retorno fora dos limites.");
            return values[index];
        }

        return visitChildren(ctx);
    }
    
    // --- Funções ---
    @Override public Object visitRet(LangParser.RetContext ctx) { List<Object> values = new ArrayList<>(); if (ctx.exprList() != null) { ctx.exprList().expr().forEach(e -> values.add(visit(e))); } throw new ReturnValue(values.toArray()); }
    @Override public Object visitFunc(LangParser.FuncContext ctx) { try { visit(ctx.block()); } catch (ReturnValue rv) { return rv; } return null; }
    private Object executeFunction(String funcName, List<LangParser.ExprContext> argExprs, String parentType) { FunctionDefinition funcDef = functions.get(funcName); if (funcDef == null) throw new RuntimeException("Erro: Função '" + funcName + "' não definida."); List<Object> args = new ArrayList<>(); if (argExprs != null) argExprs.forEach(e -> args.add(visit(e))); memory.push(new HashMap<>()); String oldParent = this.currentFunctionParent; this.currentFunctionParent = parentType; try { if (funcDef.context.params() != null) { List<LangParser.ParamContext> params = funcDef.context.params().param(); for (int i = 0; i < params.size(); i++) { assign(params.get(i).ID().getText(), args.get(i)); } } visit(funcDef.context.block()); } catch (ReturnValue rv) { return rv; } finally { this.currentFunctionParent = oldParent; memory.pop(); } return null; }
    @Override public Object visitCmdFcall(LangParser.CmdFcallContext ctx) { Object result = executeFunction(ctx.ID().getText(), ctx.exprList() != null ? ctx.exprList().expr() : null, null); if (result instanceof ReturnValue && ctx.lvalueList() != null) { Object[] values = ((ReturnValue)result).values; List<LangParser.LvalueContext> lvalues = ctx.lvalueList().lvalue(); for(int i = 0; i < lvalues.size(); i++) { if (lvalues.get(i).getChildCount() == 1) { assign(lvalues.get(i).getText(), values[i]); } else { Object[] resolution = resolveLValueForAssignment(lvalues.get(i)); if (resolution[0] instanceof LangObject) ((LangObject) resolution[0]).set((String) resolution[1], values[i]); else if (resolution[0] instanceof LangArray) ((LangArray) resolution[0]).set((Integer) resolution[1], values[i]); } } } return null; }
    @Override public Object visitExprFcall(LangParser.ExprFcallContext ctx) { return executeFunction(ctx.ID().getText(), ctx.exprList() != null ? ctx.exprList().expr() : null, null); }
}