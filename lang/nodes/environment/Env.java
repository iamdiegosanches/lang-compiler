///////////////////////////////////////////////////////////////////////
/// Álvaro Braz Cunha - 21.1.8163                                   ///
/// Diego Sanches Nere dos Santos - 21.1.8003                       ///
///////////////////////////////////////////////////////////////////////
package lang.nodes.environment;

import java.util.HashMap;
import java.util.Map;
public class Env {

     private HashMap<String, Object> m;

     public Env(){
         m = new HashMap<String,Object>(100);
     }

     public void store(String vname, Object value){
          m.put(vname,value);
     }

     public Object read(String vname){
         Object i = m.get(vname);
         if(i == null){
            throw new RuntimeException("Unknow variable " + vname);
         }
         return i;
     }

     public HashMap<String, Object> getMap() {
         return m;
     }

     private String repeatStr(int n, String c){
         String s= "";
         for(;n>0;n--){
            s += c;
         }
         return s;
     }

     public void dumpTable(){
          String title = "Variavel";

          System.out.println(repeatStr(6 - title.length()/2," ") +
                             title +
                             repeatStr(6 - title.length()/2," ") + "|  Valor");
          System.out.println(repeatStr(22,"-"));
          for(Map.Entry<String,Object> e : m.entrySet()){
              System.out.println(e.getKey() + repeatStr(12- e.getKey().length()," ") + "| " + e.getValue().toString());
              System.out.println(repeatStr(22,"-"));
          }

     }

}
