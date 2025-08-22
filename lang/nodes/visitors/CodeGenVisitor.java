package lang.nodes.visitors;

import lang.nodes.*;
import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.visitors.tychkvisitor.*;
import lang.nodes.visitors.codegen.CodeGen;
import lang.nodes.visitors.codegen.DeclarationVisitor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;

public class CodeGenVisitor extends LangVisitor {

    private final Hashtable<CNode, VType> typeMap;
    private final CodeGen cg;
    private final Stack<String> currentFunction;
    private int loopCounter = 0;
    private final Hashtable<String, String> arraySizes;

    private final Hashtable<String, FunDef> funDefs = new Hashtable<>();
    private final ArrayList<String> helperFunctions;
    private final HashSet<String> createdHelpers;

    public CodeGenVisitor(Hashtable<CNode, VType> typeMap) {
        this.typeMap = typeMap;
        this.helperFunctions = new ArrayList<>();
        this.createdHelpers = new HashSet<>();
        this.cg = new CodeGen(this.helperFunctions, this.createdHelpers);
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
        if (t instanceof VTyUser)  return "struct " + ((VTyUser) t).getName() + "*";
        if (t instanceof VTyArr) {
            return toCType(((VTyArr) t).getTyArg()) + "*";
        }
        return "void";
    }
    
    private String generateFunctionPrototype(FunDef d) {
        String fname = d.getFname().equals("main") ? "inicio" : d.getFname();
        
        ArrayList<String> params = new ArrayList<>();
        for (Bind b : d.getParams()) {
            params.add(toCType(typeOf(b.getType())) + " " + b.getVar().getName());
        }

        String retType = "void";
        if (d.getRet() != null && d.getRet().size() == 1) {
            retType = toCType(typeOf(d.getRet().get(0)));
        } else if (d.getRet() != null && d.getRet().size() > 1) {
            retType = "void";
            int retCount = 0;
            for(CType ret : d.getRet()){
                String ptrType = toCType(typeOf(ret)) + "*";
                params.add(ptrType + " _ret" + retCount++);
            }
        }
        String paramsStr = params.isEmpty() ? "void" : String.join(", ", params);
        return retType + " " + fname + "(" + paramsStr + ")";
    }

    @Override
    public void visit(Program p) {
        for (Def d : p.getDefs()) {
            if (d instanceof FunDef) {
                FunDef fd = (FunDef) d;
                funDefs.put(fd.getFname(), fd);
                cg.addPrototype(generateFunctionPrototype(fd));
            } else if (d instanceof DataDef) {
                DataDef dataDef = (DataDef) d;
                if (dataDef.getFunctions() != null) {
                    for (FunDef fd : dataDef.getFunctions()) {
                        funDefs.put(fd.getFname(), fd);
                        cg.addPrototype(generateFunctionPrototype(fd));
                    }
                }
            }
        }

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
            VType varType = typeOf((CNode)attr.getVar());
            String type = toCType(varType);
            String name = attr.getVar().getName();
            declarations.add(cg.decl(name, type));
        }

        d.getBody().accept(this);
        String body = cg.getLastValue();
        
        cg.setLastValue(cg.func(fname, params, retType, declarations, body));
        currentFunction.pop();
    }
    
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
        String varName = ((Var) i.getIterVar()).getName();
        VType condType = typeOf(i.getCondExp());
        
        if (condType instanceof VTyArr && i.getCondExp() instanceof Var) {
            String arrName = ((Var)i.getCondExp()).getName();
            String sizeVar = arraySizes.getOrDefault(arrName, "0");
            
            i.getBody().accept(this);
            String body = cg.getLastValue();
            String elemType = toCType(((VTyArr)condType).getTyArg());
            String loopIndexVar = "_idx_" + loopCounter++;
            cg.setLastValue(cg.iterateArray(varName, arrName, sizeVar, body, elemType, loopIndexVar));
        } else {
             i.getBody().accept(this);
             String body = cg.getLastValue();
             String loopIndexVar = "_idx_" + loopCounter++;
             cg.setLastValue(cg.iterateInt(varName, cond, body, loopIndexVar));
        }
    }
    
    @Override
    public void visit(Print p) {
        p.getExp().accept(this);
        String exp = cg.getLastValue();
        VType type = typeOf(p.getExp());

        if (type instanceof VTyBool) {
            String helperName = "_print_bool";
            if (!createdHelpers.contains(helperName)) {
                String helperFunc = "void " + helperName + "(int b) {\n\tif (b) {\n\t\tprintf(\"true\");\n\t} else {\n\t\tprintf(\"false\");\n\t}\n}";
                helperFunctions.add(helperFunc);
                createdHelpers.add(helperName);
            }
            cg.setLastValue(helperName + "(" + exp + ");");
        }
        else if (type instanceof VTyInt) {
            cg.setLastValue(cg.printIntCmd(exp));
        }
        else if (type instanceof VTyFloat) {
            cg.setLastValue(cg.printFloatCmd(exp));
        }
        else if (type instanceof VTyChar) {
            cg.setLastValue(cg.printCharCmd(exp));
        }
        else {
            cg.setLastValue("/* Print para tipo complexo não implementado */");
        }
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

    @Override
    public void visit(FCall f) {
        String fname = f.getID();
        FunDef funDef = funDefs.get(fname);

        if (funDef == null) {
            throw new RuntimeException("CodeGen Error: Função '" + fname + "' não encontrada.");
        }

        ArrayList<CType> returnTypes = funDef.getRet();
        ArrayList<String> args = new ArrayList<>();
        if (f.getArgs() != null) {
            for (Exp e : f.getArgs()) {
                e.accept(this);
                args.add(cg.getLastValue());
            }
        }

        if (returnTypes.size() <= 1) {
            String cFname = fname.equals("main") ? "inicio" : fname;
            cg.setLastValue(cg.fcall(cFname, args));
            return;
        }

        if (f.getReturnIndex() == null || !(f.getReturnIndex() instanceof IntLit)) {
            throw new RuntimeException("CodeGen Error (" + f.getLine() + "," + f.getCol() + "): Acesso indexado a função com múltiplos retornos requer um índice inteiro constante.");
        }
        
        int index = ((IntLit)f.getReturnIndex()).getValue();
        if (index < 0 || index >= returnTypes.size()) {
            throw new RuntimeException("CodeGen Error (" + f.getLine() + "," + f.getCol() + "): Índice de retorno fora dos limites.");
        }

        String helperName = "_" + fname + "_ret" + index;

        if (!createdHelpers.contains(helperName)) {
            ArrayList<String> tempVars = new ArrayList<>();
            ArrayList<String> tempAddrs = new ArrayList<>();
            String cFname = fname.equals("main") ? "inicio" : fname;

            for (int i = 0; i < returnTypes.size(); i++) {
                VType retVType = typeOf(returnTypes.get(i));
                String cType = toCType(retVType);
                String varName = "_tmp_ret" + i;
                tempVars.add(cType + " " + varName + ";");
                tempAddrs.add("&" + varName);
            }
            
            String callStmt = cFname + "(" + String.join(", ", tempAddrs) + ");";
            String returnStmt = "return _tmp_ret" + index + ";";
            
            VType targetReturnVType = typeOf(returnTypes.get(index));
            String helperRetType = toCType(targetReturnVType);

            String helperBody = String.join("\n\t", tempVars) + "\n\t" + callStmt + "\n\t" + returnStmt;
            String helperFunc = helperRetType + " " + helperName + "(void) {\n\t" + helperBody + "\n}";
            
            helperFunctions.add(helperFunc);
            createdHelpers.add(helperName);
        }

        cg.setLastValue(helperName + "()");
    }
    
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
    @Override
    public void visit(DotAccess d) {
        d.getRecord().accept(this);
        String rec = cg.getLastValue();
        cg.setLastValue(rec + "->" + d.getFieldName());
    }
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
}