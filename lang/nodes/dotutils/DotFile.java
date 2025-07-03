package lang.nodes.dotutils;

import java.util.LinkedList;

import java.io.FileWriter;



public class DotFile {

    private LinkedList<String> edges;
    private LinkedList<String> nodes;

    public DotFile(){
          edges = new LinkedList<String>();
          nodes = new LinkedList<String>();
    }

    public int addNode(String name){
          int k = nodes.size();
          nodes.add("n" + k +" [ label = \""+ name + "\"] \n");
          return k;
    }

    public void addEdge(int o, int d){
          edges.add("n" + o +" -> "+ "n" + d + " \n");
    }

    public void writeToFile(String path){
        try{
            FileWriter f = new FileWriter(path);
            String start = "digraph {\n";
            String end = "}";
            String ident = "    ";
            f.write(start,0,start.length());
            for(;nodes.size() >0; nodes.removeFirst()){
               f.write(ident,0,ident.length());
               f.write(nodes.getFirst(),0,nodes.getFirst().length());
            }
            for(;edges.size() >0; edges.removeFirst()){
               f.write(ident,0,ident.length());
               f.write(edges.getFirst(),0,edges.getFirst().length());
            }
            f.write(end,0,end.length());
            f.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
