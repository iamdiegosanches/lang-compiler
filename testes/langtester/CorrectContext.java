package langtester;

import java.util.List;
import java.util.ArrayList;


public class CorrectContext{
     public int passed;
     public int failed;
     public List<ListEntry> rejs;

     public CorrectContext(){
           passed = 0;
           failed = 0;
           rejs = new ArrayList<ListEntry>();
     }
     public void createReport(String label){
          ListEntry le = new ListEntry();
          le.label = label;
          le.instances = new ArrayList<String>();
          rejs.add(le);
     }


     public void reject(String s){rejs.get(rejs.size()-1).instances.add(s); }

      public void report(){
        for(ListEntry le : rejs){
            System.out.println(le.label);
            for(String s : le.instances){
                System.out.println("    " + s);
            }
        }
        if(passed > 1){
            System.out.println(passed + " casos de testes passaram");
        }else if(passed == 1){
            System.out.println("1 caso de teste passou");
        }else{
            System.out.println("Nenhum caso de teste passou :-(");
        }

        if(failed > 1){
            System.out.println(failed + " casos de testes falharam");
        }else if(failed == 1){
            System.out.println("1 caso de teste falhou");
        }else{
            System.out.println("Nenhum caso de teste falhou :-)");
        }
   }

}
