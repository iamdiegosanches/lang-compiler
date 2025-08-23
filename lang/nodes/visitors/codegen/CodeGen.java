///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.visitors.codegen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

public class CodeGen {
    private StringBuilder code = new StringBuilder();
    private String lastValue;
    private final ArrayList<String> helperFunctions;
    private final HashSet<String> createdHelpers;
    private final ArrayList<String> functionPrototypes = new ArrayList<>();

    public CodeGen(ArrayList<String> helperFunctions, HashSet<String> createdHelpers) {
        this.helperFunctions = helperFunctions;
        this.createdHelpers = createdHelpers;
    }

    public String getCode() { return code.toString(); }
    public void addCode(String s) { code.append(s); }
    public String getLastValue() { return lastValue; }
    public void setLastValue(String s) { lastValue = s; }
    
    public void addPrototype(String prototype) {
        this.functionPrototypes.add(prototype);
    }

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
    public String iterateInt(String var, String cond, String body, String loopIndexVar) {
         String bodyWithDecl = var + " = " + loopIndexVar + ";\n\t" + body;
         return "for (int " + loopIndexVar + " = 0; " + loopIndexVar + " < (" + cond + "); " + loopIndexVar + "++) {\n\t" + bodyWithDecl.replace("\n","\n\t") + "\n}";
    }
    public String iterateArray(String var, String arr, String size, String body, String type, String loopIndexVar) {
        String bodyWithDecl = type + " " + var + " = " + arr + "[" + loopIndexVar + "];\n\t" + body;
        return "for (int " + loopIndexVar + " = 0; " + loopIndexVar + " < (" + size + "); " + loopIndexVar + "++) {\n\t" + bodyWithDecl.replace("\n","\n\t") + "\n}";
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
        String helpers = String.join("\n\n", this.helperFunctions);
        String prototypes = String.join(";\n", this.functionPrototypes) + ";\n\n";

        return "#include <stdio.h>\n#include <stdlib.h>\n\n" +
               String.join("\n", dataDefs) + "\n\n" +
               prototypes +
               helpers + "\n\n" +
               String.join("\n\n", flist) +
               "\n\nint main() {\n\tinicio();\n\treturn 0;\n}\n";
    }
}