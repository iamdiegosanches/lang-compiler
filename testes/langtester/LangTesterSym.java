package langtester;

import java.io.*;
import java.util.List;
import java.util.LinkedList;


public class LangTesterSym{
   // Recupera o nome base (sem extensão) de um arquivo.

   public static File[] list_files(String path){
        File fs = new File(path);
        return fs.listFiles();
   }

   public static byte[] dotedArr(int n, char c){
        byte[] b = new byte[n];
        for(int i = 0; i <n; i++){
             b[i] = (byte)c;
        }
        return b;
   }

   public static int maxNameLen(int min, File[] fs){
        int mx = min;
        for(File f : fs ){
           if(f.getName().length() > mx){
               mx =  f.getName().length();
            }
        }
        return mx;
   }

   public static void main(String[] args){
       String cmd = "./lang.sh -syn";
       List<String> accfails = new LinkedList<String>();
       List<String> rejfails = new LinkedList<String>();
       try{
          int passed = 0, failed = 0, maxfname;
          Runtime rn = Runtime.getRuntime();
          File[] accfs = list_files("testes/sintaxe/certo/");
          File[] rejfs = list_files("testes/sintaxe/errado/");
          maxfname = maxNameLen(5,accfs);
          byte[] sepbuff  = dotedArr(maxfname,'.');
          Process p;
          String out;
          BufferedReader procr;
          System.out.println("Processando casos de teste em testes/sintaxe/certo/");
          for(File fsrc : accfs){
              System.out.print(fsrc.getName());
              System.out.write(sepbuff,0,maxfname - fsrc.getName().length());
              System.out.print("[");
              p = rn.exec(cmd +" " + fsrc.getAbsolutePath());
              out = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
              if(out.equals("accepted")){
                  System.out.println(" OK ]");
                  passed++;
              }else{
                  System.out.println("FAIL]");
                  accfails.add(fsrc.getName());
                  failed++;
              }
          }
          System.out.println("Processando casos de teste em testes/sintaxe/errado/");
          for(File fsrc : rejfs){
              System.out.print(fsrc.getName());
              System.out.write(sepbuff,0,maxfname - fsrc.getName().length());
              System.out.print("[");
              p = rn.exec(cmd +" " + fsrc.getAbsolutePath());
              out = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
              if(out.equals("rejected")){
                  System.out.println(" OK ]");
                  passed++;
              }else{
                  System.out.println("FAIL]");
                  rejfails.add(fsrc.getName());
                  failed++;
              }
          }
          report(passed, failed, accfails, rejfails);
      }catch(Exception e){
          e.printStackTrace();
      }
   }

   public static void report(int pass, int fails, List<String> accf, List<String> rejf ){
        if(accf.size() > 0){
            System.out.println("Os seguintes arquivos deviam ter sido aceitos:");
            for(String s : accf){
                 System.out.println("    " + s);
            }
        }
        if(rejf.size() > 0){
            System.out.println("Os seguintes arquivos deviam ter sido rejeitados:");
            for(String s : rejf){
                 System.out.println("    " + s);
            }
        }
        if(pass > 1){
            System.out.println(pass + " casos de testes passaram");
        }else if(pass == 1){
            System.out.println("1 caso de teste passou");
        }else{
            System.out.println("Nenhum caso de teste passou :-(");
        }

        if(fails > 1){
            System.out.println(pass + " casos de testes falharam");
        }else if(fails == 1){
            System.out.println("1 caso de teste falhou");
        }else{
            System.out.println("Nenhum caso de teste falhou :-)");
        }
   }
}
 
