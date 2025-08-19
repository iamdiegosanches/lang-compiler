///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////

package lang.nodes.visitors;

import lang.nodes.*;
import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.visitors.tychkvisitor.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Set;

/**
 * CodeGenVisitor percorre a AST da linguagem 'lang' e gera código C
 * correspondente (source-to-source).
 */
public class CodeGenVisitor extends LangVisitor {

    private final Hashtable<CNode, VType> typeMap;
    private final CodeGen cg;
    private final Stack<String> currentFunction;
    private int loopCounter = 0;
    private final Hashtable<String, String> arraySizes;

    public CodeGenVisitor(Hashtable<CNode, VType> typeMap) {
        this.typeMap = typeMap;
        this.cg = new CodeGen();
        this.currentFunction = new Stack<>();
        this.arraySizes = new Hashtable<>();
    }

    public String getCode() {
        return cg.getCode();
    }

    private VType typeOf(CNode node) {
        return (node != null) ? typeMap.get(node) : null;
    }

    private String toCType(VType t) {
        if (t == null) return "void";
        if (t instanceof VTyInt) return "int";
        if (t instanceof VTyFloat) return "float";
        if (t instanceof VTyBool) return "int";
        if (t instanceof VTyChar) return "char";
        if (t instanceof VTyUser) return "struct " + ((VTyUser) t).getName();
        if (t instanceof VTyArr) {
            return toCType(((VTyArr) t).getTyArg()) + "*";
        }
        return "void";
    }

    // --- VISITANTES PRINCIPAIS ---

    @Override
    public void visit(Program p) {
        ArrayList<String> dataDefs = new ArrayList<>();
        ArrayList<String> funList = new ArrayList<>();

        for (Def d : p.getDefs()) {
            if (d instanceof DataDef) {
                d.accept(this);
                dataDefs.add(cg.getLastValue());
            }
        }
        
        for (Def d : p.getDefs()) {
            if (d instanceof FunDef) {
                d.accept(this);
                funList.add(cg.getLastValue());
            }
        }

        String programCode = cg.program(dataDefs, funList);
        cg.addCode(programCode);
    }

    @Override
    public void visit(DataDef d) {
        ArrayList<String> decls = new ArrayList<>();
        for (Decl attr : d.getAttributes()) {
            attr.accept(this);
            decls.add(cg.getLastValue());
        }
        cg.setLastValue(cg.dataDef(d.getTypeName(), decls));
    }
    
    @Override
    public void visit(FunDef d) {
        String fname = d.getFname().equals("main") ? "inicio" : d.getFname();
        currentFunction.push(fname);

        ArrayList<String> params = new ArrayList<>();
        for (Bind b : d.getParams()) {
            b.accept(this);
            params.add(cg.getLastValue());
        }

        String retType = "void";
        if (d.getRet() != null && d.getRet().size() == 1) {
            retType = toCType(typeOf(d.getRet().get(0)));
        } else if (d.getRet() != null && d.getRet().size() > 1) {
            int retCount = 0;
            for(CType ret : d.getRet()){
                String ptrName = "_ret" + retCount++;
                String ptrType = toCType(typeOf(ret)) + "*";
                params.add(cg.bind(ptrName, ptrType));
            }
        }

        DeclarationVisitor declVisitor = new DeclarationVisitor(this.typeMap);
        d.getBody().accept(declVisitor);
        ArrayList<String> declarations = new ArrayList<>();
        for (CAttr attr : declVisitor.getDeclarations()) {
            VType varType = typeOf(attr.getExp());
            String type = toCType(varType);
            String name = attr.getVar().getName();
            declarations.add(cg.decl(name, type));
        }

        d.getBody().accept(this);
        String body = cg.getLastValue();
        
        cg.setLastValue(cg.func(fname, params, retType, declarations, body));
        currentFunction.pop();
    }
    
    // --- VISITANTES DE COMANDOS ---
    
    @Override
    public void visit(CSeq s) {
        s.getLeft().accept(this);
        String left = cg.getLastValue();
        s.getRight().accept(this);
        String right = cg.getLastValue();
        cg.setLastValue(cg.seq(left, right));
    }
    
    @Override
    public void visit(CAttr a) {
        if (a.getExp() instanceof NewArray) {
            if (a.getVar() instanceof Var) {
                NewArray newArrayNode = (NewArray) a.getExp();
                newArrayNode.getSizeExp().accept(this);
                String sizeExpression = cg.getLastValue();
                arraySizes.put(a.getVar().getName(), sizeExpression);
            }
        }
        
        a.getVar().accept(this);
        String lval = cg.getLastValue();
        a.getExp().accept(this);
        String exp = cg.getLastValue();
        cg.setLastValue(cg.attr(lval, exp));
    }
    
    @Override
    public void visit(If i) {
        i.getCond().accept(this);
        String cond = cg.getLastValue();
        i.getThn().accept(this);
        String thn = cg.getLastValue();
        String els = null;
        if (i.getEls() != null) {
            i.getEls().accept(this);
            els = cg.getLastValue();
        }
        cg.setLastValue(cg.ifStmt(cond, thn, els));
    }
    
    @Override
    public void visit(Loop l) {
        l.getCond().accept(this);
        String cond = cg.getLastValue();
        l.getBody().accept(this);
        String body = cg.getLastValue();
        String counterVar = "_loop_count_" + (loopCounter++);
        cg.setLastValue(cg.loop(cond, body, counterVar));
    }

    @Override
    public void visit(IterateWithVar i) {
        i.getCondExp().accept(this);
        String cond = cg.getLastValue();
        String varName = i.getIterVar().getName();
        VType condType = typeOf(i.getCondExp());
        
        if (condType instanceof VTyArr && i.getCondExp() instanceof Var) {
            String arrName = ((Var)i.getCondExp()).getName();
            String sizeVar = arraySizes.getOrDefault(arrName, "0"); 
            
            i.getBody().accept(this);
            String body = cg.getLastValue();
            String elemType = toCType(((VTyArr)condType).getTyArg());
            cg.setLastValue(cg.iterateArray(varName, arrName, sizeVar, body, elemType));
        } else {
             i.getBody().accept(this);
             String body = cg.getLastValue();
             cg.setLastValue(cg.iterateInt(varName, cond, body));
        }
    }
    
    @Override
    public void visit(Print p) {
        p.getExp().accept(this);
        String exp = cg.getLastValue();
        VType type = typeOf(p.getExp());

        if (type instanceof VTyInt || type instanceof VTyBool) cg.setLastValue(cg.printIntCmd(exp));
        else if (type instanceof VTyFloat) cg.setLastValue(cg.printFloatCmd(exp));
        else if (type instanceof VTyChar) cg.setLastValue(cg.printCharCmd(exp));
        else cg.setLastValue("/* Print para tipo complexo não implementado */");
    }
    
    @Override
    public void visit(Return r) {
        ArrayList<Exp> exps = r.getExp();
        if (exps.isEmpty()) {
            cg.setLastValue("return;");
        } else if (exps.size() == 1) {
            exps.get(0).accept(this);
            cg.setLastValue(cg.returnCMD(cg.getLastValue()));
        } else {
            ArrayList<String> assignments = new ArrayList<>();
            for(int i = 0; i < exps.size(); i++){
                exps.get(i).accept(this);
                assignments.add("*_ret" + i + " = " + cg.getLastValue() + ";");
            }
            assignments.add("return;");
            cg.setLastValue(String.join("\n\t", assignments));
        }
    }

    @Override
    public void visit(FCallCommand f) {
        ArrayList<String> args = new ArrayList<>();
        for (Exp e : f.getArgs()) {
            e.accept(this);
            args.add(cg.getLastValue());
        }

        for(LValue lval : f.getReturnTargets()){
            args.add("&" + lval.getName());
        }

        String fname = f.getID().equals("main") ? "inicio" : f.getID();
        String call = cg.fcall(fname, args);
        cg.setLastValue(call + ";");
    }
    
    private void visitBinaryOperator(BinOp b, String op) {
        b.getLeft().accept(this);
        String left = cg.getLastValue();
        b.getRight().accept(this);
        String right = cg.getLastValue();
        cg.setLastValue(cg.binop(left, right, op));
    }
    @Override public void visit(And a) { visitBinaryOperator(a, "&&"); }
    @Override public void visit(Sub s) { visitBinaryOperator(s, "-"); }
    @Override public void visit(Plus p) { visitBinaryOperator(p, "+"); }
    @Override public void visit(Times t) { visitBinaryOperator(t, "*"); }
    @Override public void visit(Div d) { visitBinaryOperator(d, "/"); }
    @Override public void visit(Mod m) { visitBinaryOperator(m, "%"); }
    @Override public void visit(LessThan lt) { visitBinaryOperator(lt, "<"); }
    @Override public void visit(Equal eq) { visitBinaryOperator(eq, "=="); }
    @Override public void visit(NotEqual ne) { visitBinaryOperator(ne, "!="); }

    @Override public void visit(FCall f) { /* ... */ }
    @Override public void visit(IntLit i) { cg.setLastValue(String.valueOf(i.getValue())); }
    @Override public void visit(FloatLit fl) { cg.setLastValue(String.valueOf(fl.getValue())); }
    @Override public void visit(BoolLit b) { cg.setLastValue(b.getValue() ? "1" : "0"); }
    @Override public void visit(CharLit c) {
        char val = c.getValue();
        if (val == '\n') cg.setLastValue("'\\n'");
        else if (val == '\t') cg.setLastValue("'\\t'");
        else if (val == '\\') cg.setLastValue("'\\\\'");
        else if (val == '\'') cg.setLastValue("'\\''");
        else if (val == '\r') cg.setLastValue("'\\r'");
        else cg.setLastValue("'" + val + "'");
    }
    @Override public void visit(Var v) { cg.setLastValue(v.getName()); }
    @Override public void visit(NullLit n) { cg.setLastValue("NULL"); }
    @Override public void visit(Not n) { n.getRight().accept(this); cg.setLastValue("!(" + cg.getLastValue() + ")"); }
    @Override public void visit(UMinus u) { u.getRight().accept(this); cg.setLastValue("-(" + cg.getLastValue() + ")"); }
    @Override public void visit(ArrayAccess a) { a.getArrayVar().accept(this); String arr = cg.getLastValue(); a.getIndexExp().accept(this); String idx = cg.getLastValue(); cg.setLastValue(arr + "[" + idx + "]");}
    @Override public void visit(DotAccess d) { d.getRecord().accept(this); String rec = cg.getLastValue(); cg.setLastValue(rec + "->" + d.getFieldName());}
    @Override public void visit(NewObject n) {
        String typeName = n.getType().getName();
        String cType = "struct " + typeName;
        cg.setLastValue("(" + cType + "*)malloc(sizeof(" + cType + "))");
    }
    @Override public void visit(NewArray n) {
        n.getSizeExp().accept(this);
        String size = cg.getLastValue();
        VType arrVType = typeOf(n);
        String elemCType = toCType(((VTyArr)arrVType).getTyArg());
        cg.setLastValue("(" + elemCType + "*)malloc(" + size + " * sizeof(" + elemCType + "))");
    }
    
    @Override public void visit(Bind b) { String type = toCType(typeOf(b.getType())); String name = b.getVar().getName(); cg.setLastValue(cg.bind(name, type)); }
    @Override public void visit(Decl d) { String type = toCType(typeOf(d.getType())); String name = d.getVar().getName(); cg.setLastValue(type + " " + name + ";"); }
    @Override public void visit(CDecl d) {}
    @Override public void visit(CNull n) { cg.setLastValue("/* empty */"); }
    @Override public void visit(Read r) {
        r.getTarget().accept(this);
        String target = cg.getLastValue();
        VType type = typeOf((CNode)r.getTarget());
        String format = "";
        if (type instanceof VTyInt) format = "\"%d\"";
        else if (type instanceof VTyFloat) format = "\"%f\"";
        else if (type instanceof VTyChar) format = "\" %c\""; 
        if (!format.isEmpty()) {
            cg.setLastValue("scanf(" + format + ", &" + target + ");");
        } else {
            cg.setLastValue("/* read para este tipo não implementado */");
        }
    }
    @Override public void visit(BinOp b) {}
    @Override public void visit(UnOp u) {}
    @Override public void visit(TyBool t) {}
    @Override public void visit(TyInt t) {}
    @Override public void visit(TyFloat t) {}
    @Override public void visit(TyChar t) {}
    @Override public void visit(TyArr t) {}
    @Override public void visit(TyUser t) {}

    private class DeclarationVisitor extends SimpleVisitor {
        private final LinkedHashSet<CAttr> declarations = new LinkedHashSet<>();
        private final Set<String> declaredVars = new HashSet<>();
        private final Hashtable<CNode, VType> typeMap;

        public DeclarationVisitor(Hashtable<CNode, VType> typeMap) {
            this.typeMap = typeMap;
        }

        public ArrayList<CAttr> getDeclarations() {
            return new ArrayList<>(declarations);
        }

        @Override
        public void visit(CAttr d) {
            if (d.getVar() instanceof Var) {
                String varName = d.getVar().getName();
                if (!declaredVars.contains(varName)) {
                    declarations.add(d);
                    declaredVars.add(varName);
                }
            }
        }
        @Override
        public void visit(CSeq s){ s.getLeft().accept(this); s.getRight().accept(this); }
        @Override
        public void visit(If i){ i.getThn().accept(this); if(i.getEls() != null) i.getEls().accept(this); }
        @Override
        public void visit(Loop l){ l.getBody().accept(this); }
        @Override
        public void visit(IterateWithVar i){ i.getBody().accept(this); }
    }

    private class CodeGen {
        private StringBuilder code = new StringBuilder();
        private String lastValue;

        public String getCode() { return code.toString(); }
        public void addCode(String s) { code.append(s); }
        public String getLastValue() { return lastValue; }
        public void setLastValue(String s) { lastValue = s; }

        public String binop(String expe, String expd, String op) { return "(" + expe + " " + op + " " + expd + ")"; }
        public String attr(String lval, String exp) { return lval + " = " + exp + ";"; }
        public String fcall(String fname, ArrayList<String> args) { return fname + "(" + String.join(", ", args) + ")"; }
        public String seq(String lft, String rht) { return lft + "\n" + rht; }
        public String ifStmt(String e, String thn, String els) {
            String result = "if (" + e + ") {\n\t" + thn.replace("\n", "\n\t") + "\n}";
            if (els != null && !els.trim().isEmpty() && !els.trim().equals("/* empty */")) {
                result += " else {\n\t" + els.replace("\n", "\n\t") + "\n}";
            }
            return result;
        }
        public String loop(String e, String body, String counterVar) { 
            return "for (int " + counterVar + " = 0; " + counterVar + " < (" + e + "); " + counterVar + "++) {\n\t" + body.replace("\n", "\n\t") + "\n}";
        }
        public String iterateInt(String var, String cond, String body) {
            return "for (int " + var + " = 0; " + var + " < " + cond + "; " + var + "++) {\n\t" + body.replace("\n", "\n\t") + "\n}";
        }
        public String iterateArray(String var, String arr, String size, String body, String type) {
            String i = "_idx_" + loopCounter++;
            String bodyWithDecl = type + " " + var + " = " + arr + "[" + i + "];\n\t" + body;
            return "for (int " + i + " = 0; " + i + " < (" + size + "); " + i + "++) {\n\t" + bodyWithDecl.replace("\n","\n\t") + "\n}";
        }
        public String returnCMD(String e) { return "return " + e + ";"; }
        public String printIntCmd(String e) { return "printf(\"%d\", " + e + ");"; }
        public String printFloatCmd(String e) { return "printf(\"%f\", " + e + ");"; }
        public String printCharCmd(String e) { return "printf(\"%c\", " + e + ");"; }
        public String bind(String var, String type) { return type + " " + var; }
        public String decl(String v, String t) { return t + " " + v + ";"; }
        public String dataDef(String name, ArrayList<String> decls) { return "struct " + name + " {\n\t" + String.join("\n\t", decls) + "\n};"; }
        public String func(String id, ArrayList<String> params, String ret, ArrayList<String> decls, String body) {
            String paramsStr = params.isEmpty() ? "void" : String.join(", ", params);
            String declBlock = decls.isEmpty() ? "" : "\t" + String.join("\n\t", decls) + "\n";
            return ret + " " + id + "(" + paramsStr + ") {\n" + declBlock + "\t" + body.replace("\n", "\n\t") + "\n}";
        }
        public String program(ArrayList<String> dataDefs, ArrayList<String> flist) {
            return "#include <stdio.h>\n#include <stdlib.h>\n\n" + 
                   String.join("\n", dataDefs) + "\n\n" +
                   String.join("\n\n", flist) +
                   "\n\nint main() {\n\tinicio();\n\treturn 0;\n}\n";
        }
    }
}