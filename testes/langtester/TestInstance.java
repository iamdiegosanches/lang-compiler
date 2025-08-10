package langtester;

import java.util.LinkedList;


public class TestInstance{
     public LinkedList<String> inData;
     public LinkedList<String> outData;
     public LinkedList<String> out;

     public boolean testOutput(LinkedList<String> progOut){
          boolean r = progOut.size() == outData.size();
          int k =0;
          if(r){
              for(String s : outData){
                  r = r && (s.compareTo(progOut.get(k++)) == 0);
              }
          }
          if(!r){ this.out = progOut;}
          return r;
     }
}
