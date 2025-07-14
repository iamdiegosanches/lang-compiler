package lang.nodes;


public abstract class CNode{
     private int l,c;

     public CNode(int line, int col){
          l = line;
          c = col;
     }

     public int getLine(){return l;}
     public int getCol(){return c;}

     public abstract void accept(LangVisitor v);

}
