package lang;

import lang.ast.*;
import lang.parser.LangBaseVisitor;
import lang.parser.LangParser;
// Precisamos de mais alguns imports para a nova lógica
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

public class BuildAstVisitor extends LangBaseVisitor<NoAST> {

    // As regras que já funcionavam continuam iguais...
    @Override
    public NoAST visitProg(LangParser.ProgContext ctx) {
        List<Funcao> funcoes = new ArrayList<>();
        for (LangParser.DefContext defCtx : ctx.def()) {
            funcoes.add((Funcao) visit(defCtx));
        }
        return new Prog(funcoes);
    }
    @Override
    public NoAST visitDef(LangParser.DefContext ctx) {
        if (ctx.func() != null) { return visit(ctx.func()); }
        return null;
    }
    @Override
    public NoAST visitFunc(LangParser.FuncContext ctx) {
        String funcId = ctx.ID().getText();
        Block body = (Block) visit(ctx.block());
        return new Funcao(funcId, body);
    }
    @Override
    public NoAST visitBlock(LangParser.BlockContext ctx) {
        List<Comando> comandos = ctx.stm().stream()
                                      .map(stmCtx -> (Comando) visit(stmCtx))
                                      .collect(Collectors.toList());
        return new Block(comandos);
    }
    @Override
    public NoAST visitStm(LangParser.StmContext ctx) {
        if (ctx.print() != null) { return visit(ctx.print()); }
        return null;
    }
    @Override
    public NoAST visitPrint(LangParser.PrintContext ctx) {
        Expr exp = (Expr) visit(ctx.expr());
        return new CmdPrint(exp);
    }
    @Override
    public NoAST visitPterm(LangParser.PtermContext ctx) {
        if (ctx.literal() != null) { return visit(ctx.literal()); }
        return null;
    }
    @Override
    public NoAST visitLiteral(LangParser.LiteralContext ctx) {
        if (ctx.INT() != null) {
            int valor = Integer.parseInt(ctx.INT().getText());
            return new LiteralInt(valor);
        }
        return null;
    }
    @Override
    public NoAST visitUterm(LangParser.UtermContext ctx) {
        if (ctx.pterm() != null) { return visit(ctx.pterm()); }
        return null;
    }

    // ==================================================================
    // ========= AQUI ESTÁ A LÓGICA CORRIGIDA E DEFINITIVA ==============
    // ==================================================================

    // Lógica genérica para lidar com expressões binárias de qualquer tipo.
    private Expr buildBinaryExpression(List<? extends ParseTree> terms, List<TerminalNode> ops) {
        // Pega o primeiro termo, que é a base da expressão.
        Expr result = (Expr) visit(terms.get(0));
        
        // Itera sobre os operadores. O número de operadores é sempre um a menos que o número de termos.
        for (int i = 0; i < ops.size(); i++) {
            // Pega o operador da vez
            String op = ops.get(i).getText();
            // Pega o termo da direita correspondente
            Expr dir = (Expr) visit(terms.get(i + 1));
            // Constrói a árvore binária, usando o resultado anterior como a sub-árvore da esquerda.
            result = new ExprBinaria(result, op, dir);
        }
        return result;
    }

    // expr: bterm (op=AND bterm)*;
    @Override
    public NoAST visitExpr(LangParser.ExprContext ctx) {
        // ctx.AND() retorna a lista de todos os tokens '&&'
        return buildBinaryExpression(ctx.bterm(), ctx.AND());
    }

    // bterm: cterm ( (op=EQUAL_EQUAL | op=NOT_EQUAL) cterm)*;
    @Override
    public NoAST visitBterm(LangParser.BtermContext ctx) {
        // Precisamos juntar os '==' e '!=' em uma única lista de operadores, na ordem em que aparecem.
        List<TerminalNode> ops = new ArrayList<>();
        ops.addAll(ctx.EQUAL_EQUAL());
        ops.addAll(ctx.NOT_EQUAL());
        // Ordena a lista de operadores pela sua posição no arquivo fonte.
        ops.sort(Comparator.comparingInt(s -> s.getSymbol().getTokenIndex()));
        
        return buildBinaryExpression(ctx.cterm(), ops);
    }
    
    // cterm: aterm ( (op=LESS_THAN | op=GREATER_THAN) aterm)*;
    @Override
    public NoAST visitCterm(LangParser.CtermContext ctx) {
        List<TerminalNode> ops = new ArrayList<>();
        ops.addAll(ctx.LESS_THAN());
        ops.addAll(ctx.GREATER_THAN());
        ops.sort(Comparator.comparingInt(s -> s.getSymbol().getTokenIndex()));
        
        return buildBinaryExpression(ctx.aterm(), ops);
    }

    // aterm: mterm ( (op=PLUS | op=MINUS) mterm)*;
    @Override
    public NoAST visitAterm(LangParser.AtermContext ctx) {
        List<TerminalNode> ops = new ArrayList<>();
        ops.addAll(ctx.PLUS());
        ops.addAll(ctx.MINUS());
        ops.sort(Comparator.comparingInt(s -> s.getSymbol().getTokenIndex()));
        
        return buildBinaryExpression(ctx.mterm(), ops);
    }

    // mterm: uterm ( (op=MULT | op=DIV | op=MOD) uterm)*;
    @Override
    public NoAST visitMterm(LangParser.MtermContext ctx) {
        List<TerminalNode> ops = new ArrayList<>();
        ops.addAll(ctx.MULT());
        ops.addAll(ctx.DIV());
        ops.addAll(ctx.MOD());
        ops.sort(Comparator.comparingInt(s -> s.getSymbol().getTokenIndex()));
        
        return buildBinaryExpression(ctx.uterm(), ops);
    }
}