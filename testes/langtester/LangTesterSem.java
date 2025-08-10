package langtester;

import java.io.*;
import java.util.List;
import java.util.LinkedList;





public class LangTesterSem{
   // Recupera o nome base (sem extensão) de um arquivo.

   private static File[] list_files(String path){
        File fs = new File(path);
        return fs.listFiles();
   }
   private static File[] list_files(String path, String ext){
        File fs = new File(path);
        FileFilter ff = new FileFilter(){
              public boolean accept(File f){
                   return f.isFile() && f. getName().endsWith("."+ext);
              }
        };
        return fs.listFiles(ff);
   }

   private static byte[] dotedArr(int n, char c){
        byte[] b = new byte[n];
        for(int i = 0; i <n; i++){
             b[i] = (byte)c;
        }
        return b;
   }

   private static String mkPath(String[] spath){
        String pth = "";
        for(String s : spath){
           pth = pth + s + File.separator;
        }
        return pth;
   }

   private static int maxNameLen(int min, File[] fs){
        int mx = min;
        for(File f : fs ){
           if(f.getName().length() > mx){
               mx =  f.getName().length();
            }
        }
        return mx;
   }

   private static LinkedList<TestInstance> readInstance(String fn) throws IOException {
        LinkedList<TestInstance> inst = new LinkedList<TestInstance>();
        TestInstance ti;
        LinkedList<String> di;
        BufferedReader r = null;
        try{
            r = new BufferedReader(new InputStreamReader(new FileInputStream(fn) ));
            String s = r.readLine();
            while(s != null){
               ti = new TestInstance();
               if(s.equals("---in----")){
                  s = r.readLine();
                  di = new LinkedList<String>();
                  while(s != null && !s.equals("---out---")){
                      di.add(s);
                      s = r.readLine();
                  }
                  if(di.size() > 0){ ti.inData = di;}
               }
               if(s.equals("---out---")){
                  s = r.readLine();
                  di = new LinkedList<String>();
                  while(s != null && !s.equals("---in----")){
                      di.add(s);
                      s = r.readLine();
                  }
                  if(di.size() > 0){ ti.outData = di;}
               }
               inst.add(ti);
            }
            r.close();
        }catch(FileNotFoundException e){
             System.err.println(e.getMessage());
             System.err.println("Arquivo correspondente de instancia nao encontrado");
             r.close();
             inst = null;
        }catch(IOException e){
             System.err.println(e.getMessage());
             r.close();
             inst = null;
        }

        return inst;
   }


   private static boolean runSemTest(File f, String cmd, InstanceResult fails) throws IOException{
        boolean res = true;
        LinkedList<TestInstance> inst = new LinkedList<TestInstance>();
        LinkedList<TestInstance> ltest = readInstance(f.getAbsolutePath().replaceFirst("\\.[^\\.]+$",".inst"));
        if(ltest != null){
            Runtime rn = Runtime.getRuntime();
            Process p;
            BufferedReader procr;
            BufferedWriter procw;
            fails.fails = new LinkedList<TestInstance>();
            for(TestInstance t : ltest){
                 p = rn.exec(cmd +" " + f.getAbsolutePath());
                 procr = new BufferedReader(new InputStreamReader(p.getInputStream())); // the process outputs
                 procw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream())); //the process inputs
                 if(t.inData != null){
                     for(String din : t.inData){
                         procw.write(din, 0, din.length());
                         procw.newLine();
                         procw.flush();
                     }
                 }if(t.outData != null){
                     LinkedList<String> dout = new LinkedList<String>();
                     String x =  procr.readLine();
                     while(x != null && (dout.size() < t.outData.size()) ){
                         dout.add(x);
                         x =  procr.readLine();
                     }
                     if(t.testOutput(dout)){
                        res = res && true;
                     }else{
                        fails.fails.add(t);
                        fails.fname = f.getName();
                        res = res && false;
                     }
                 }
            }
        }
        return res;
}



   public static void main(String[] args) throws IOException {
       String cmd = "./lang.sh -i";
       String spth = mkPath(new String[]{"testes","semantica","certo"});
       LinkedList<InstanceResult> tr = new LinkedList<InstanceResult>();
       try{
          int passed = 0, failed = 0, maxfname;

          File[] accfs = list_files(spth,"lan");
          if(accfs == null){
              System.out.println("Nao ha arquivos para processar em " + spth);
              System.exit(0);//
          }
          maxfname = maxNameLen(5,accfs);
          byte[] sepbuff  = dotedArr(maxfname,'-');

          System.out.println("Processando casos de teste semanticos em " + spth);
          for(File fsrc : accfs){
              InstanceResult ir = new InstanceResult();
              System.out.print(fsrc.getName());
              System.out.write(sepbuff,0,maxfname - fsrc.getName().length());
              if(runSemTest(fsrc,cmd,ir)){
                  System.out.println("[ OK ]");
                  passed++;
              }else{
                  System.out.println("[FAIL]");
                  tr.add(ir);
                  failed++;
              }
          }
          report(passed, failed, tr);
      }catch(Exception e){
          e.printStackTrace();
      }
   }

   public static void report(int pass, int fails, LinkedList<InstanceResult> sfail){
        if(sfail.size() > 0){
            System.out.println("Os seguintes arquivos produziram resultados incosistentes com o esperado:");
            int k = 0;
            for(InstanceResult s : sfail){
                k = 1;
                System.out.println(s.fname + " falhou em " +  s.fails.size() + " instancias.");
                for(TestInstance tif : s.fails){
                    System.out.println("==== [Caso " + k + " ] ====");
                    System.out.println("Entrada:");
                    if(tif.inData == null){
                        System.out.println("N/A");
                    }else{
                         for(String s1 : tif.inData){
                             System.out.println(s1);
                         }
                    }
                    System.out.println("Saida esperada:");
                    if(tif.outData == null || tif.outData.size() == 0){
                        System.out.println("N/A");
                    }else{
                        for(String s1 : tif.outData){
                             System.out.println(s1);
                        }
                    }
                    System.out.println("Saida obtida:");
                    if(tif.out == null || tif.out.size() == 0){
                        System.out.println("N/A");
                    }else{
                         for(String s1 : tif.out){
                             System.out.println(s1);
                         }
                    }
                    System.out.println("==== [Fim do caso " + k + " ] ====");
                    k++;
                }
                System.out.println(" ----------x-x-x----------");
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
            System.out.println(fails + " casos de testes falharam");
        }else if(fails == 1){
            System.out.println("1 caso de teste falhou");
        }else{
            System.out.println("Nenhum caso de teste falhou :-)");
        }
   }
}
 
