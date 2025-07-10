package lang.nodes.visitors;

import lang.nodes.decl.*;
import lang.nodes.expr.*;
import lang.nodes.command.*;
import lang.nodes.types.*;
import lang.nodes.*;
import lang.nodes.dotutils.DotFile;

public  class GVizVisitor extends LangVisitor{

     private DotFile gf;
     private int subNode;

     public GVizVisitor(){
          gf = new DotFile();
     }

     public void saveDot(String filename){
          gf.writeToFile(filename);
     }

     public void visit(Program p){
          int root = gf.addNode("Program");
          for(FunDef f : p.getFuncs()){
               f.accept(this);
               gf.addEdge(root,subNode);
          }
          subNode = root;
     }

     public void visit(FunDef d){
          int root = gf.addNode("FunDef : " + d.getFname());
          d.getRet().accept(this);
          gf.addEdge(root, subNode);
          for(Bind b: d.getParams()){
               b.accept(this);
               gf.addEdge(root, subNode);
          }
          d.getBody().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(Bind  d){
          int root = gf.addNode("Bind");
          d.getType().accept(this);
          gf.addEdge(root,subNode);

          d.getVar().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(CSeq d){
               int root = gf.addNode("Seq");
               d.getLeft().accept(this);
               gf.addEdge(root, subNode);
               d.getRight().accept(this);
               gf.addEdge(root,subNode);
               subNode = root;
     }

     public void visit(CAttr d){
               int root = gf.addNode("Attr");
               d.getVar().accept(this);
               gf.addEdge(root, subNode);
               d.getExp().accept(this);
               gf.addEdge(root,subNode);
               subNode=  root;
     }

     public void visit(CDecl d){
               int root = gf.addNode("Decl");
               d.getVar().accept(this);
               gf.addEdge(root, subNode);
               d.getExp().accept(this);
               gf.addEdge(root,subNode);
               subNode=  root;
     }

     public void visit(CNull d){
               int root = gf.addNode("NullCommand");
               gf.addEdge(root,subNode);
               subNode = root;
     }

     public void visit(Loop d){
               int root = gf.addNode("Loop");
               d.getCond().accept(this);
               gf.addEdge(root, subNode);
               d.getBody().accept(this);
               gf.addEdge(root,subNode);
               subNode=  root;
     }

     public void visit(IterateWithVar d){
               int root = gf.addNode("IterateWithVar");
               d.getIterVar().accept(this);
               gf.addEdge(root,subNode);
               d.getCondExp().accept(this);
               gf.addEdge(root,subNode);
               d.getBody().accept(this);
               gf.addEdge(root,subNode);
               subNode=  root;
     }

     public void visit(If d){
               int root = gf.addNode("If");
               d.getCond().accept(this);
               gf.addEdge(root,subNode);
               d.getThn().accept(this);
               gf.addEdge(root,subNode);
               if(d.getEls() != null){
               d.getEls().accept(this);
               gf.addEdge(root,subNode);
               }
               subNode = root;
     }

     public void visit(Return d){
          int root = gf.addNode("Return");
          d.getExp().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }
     public void visit(Print d){
          int root = gf.addNode("Print");
          d.getExp().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(And e) {
          int root = gf.addNode("&&");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(BinOp e){ }
     public void visit(UnOp e) { }
     public void visit(Sub  e){
          int root = gf.addNode("-");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }
     public void visit(Plus e){
          int root = gf.addNode("+");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }
     public void visit(Times e){
          int root = gf.addNode("*");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(Div e) {
          int root = gf.addNode("/");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(Mod e) {
          int root = gf.addNode("%");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(LessThan e) {
          int root = gf.addNode("<");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(Equal e) {
          int root = gf.addNode("==");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(NotEqual e) {
          int root = gf.addNode("!=");
          e.getLeft().accept(this);
          gf.addEdge(root,subNode);
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(Not e) {
          int root = gf.addNode("!");
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(UMinus e) {
          int root = gf.addNode("-");
          e.getRight().accept(this);
          gf.addEdge(root,subNode);
          subNode = root;
     }

     public void visit(Var e){
          subNode = gf.addNode(e.getName());
     }

     public void visit(FCall e){
          int root = gf.addNode("FCall: " + e.getID());
          for(Exp ex : e.getArgs()){
               ex.accept(this);
               gf.addEdge(root,subNode);
          }
          subNode = root;
     }

     public void visit(IntLit e){ subNode = gf.addNode(e.getValue()+""); }
     public void visit(BoolLit e){ subNode = gf.addNode(e.getValue()+""); }
     public void visit(FloatLit e){subNode = gf.addNode(e.getValue()+""); }

     public void visit(TyChar t){ subNode = gf.addNode("TyChar"); }
     public void visit(CharLit e){ subNode = gf.addNode("'"+e.getValue()+"'"); }

     public void visit(NullLit e){ subNode = gf.addNode("null"); }
     public void visit(TyBool t){ subNode = gf.addNode("TyBool"); }
     public void visit(TyInt t){ subNode = gf.addNode("TyInt");}
     public void visit(TyFloat t){subNode = gf.addNode("TyFloat"); }

}
