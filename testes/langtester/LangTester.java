package langtester;


import java.io.*;
import java.util.List;
import java.util.LinkedList;




public class LangTester{
   // Recupera o nome base (sem extensão) de um arquivo.


   private InstanceRepository[] insts;
   private boolean  hasSections;
   private int numFiles, current, currentCMD,procFiles;
   private LinkedList<String> failedInstaceFiles;
   private String[] cmd;
   //public static final short SEM_REJ = 5;
   //public static final short COMP_ACC = -1;
   public static final short SYN = 1;
   public static final short TY = 2;
   public static final short SEM = 3;
   public static final short COM = 4;

   private static String mkPath(String[] spath){
        String pth = "";
        for(String s : spath){
           pth = pth + s + File.separator;
        }
        return pth;
   }

   public LangTester(short[] types, String[] cmd) throws IOException {
         insts = new InstanceRepository[9];
         failedInstaceFiles = new LinkedList<String>();
         int k = 0;
         hasSections  = false;
         numFiles = 0;
         procFiles = 0;
         current = -1;
         currentCMD = -1;
         this.cmd = cmd;
         for(short s : types){
             switch(s){
                 case SYN : insts[k] = new InstanceRepository(s,"Sintaxe Correto",mkPath(new String[]{"sintaxe","certo"}),"lan","accepted",true,false);
                            insts[k+1] = new InstanceRepository(s,"Sintaxe Errado",mkPath(new String[]{"sintaxe","errado"}),"lan","rejected",true,false);
                            k += 2;
                            break;
                 case SEM : insts[k] =   new InstanceRepository(s,"Intepretador Simple",mkPath(new String[]{"semantica","certo","simple"}),"lan",null,false,true);
                            insts[k+1] = new InstanceRepository(s,"Intepretador Function",mkPath(new String[]{"semantica","certo","function"}),"lan",null,false,true);
                            insts[k+2] = new InstanceRepository(s,"Intepretador Full",mkPath(new String[]{"semantica","certo","full"}),"lan",null,false,true);
                            k += 3;
                            break;


                 case TY : insts[k] =   new InstanceRepository(s,"Tipos Simple",mkPath(new String[]{"types","simple"}),"lan","well-typed",true,false);
                           insts[k+1] = new InstanceRepository(s,"Tipos Function",mkPath(new String[]{"types","function"}),"lan","well-typed",true,false);
                           insts[k+2] = new InstanceRepository(s,"Tipos Full",mkPath(new String[]{"types","full"}),"lan","well-typed",true,false);
                           insts[k+3] = new InstanceRepository(s,"Tipos Errado",mkPath(new String[]{"types","errado"}),"lan","ill-typed",true,false);
                           k += 4;
                           break;


             }
         }
         short sec = 0;
         for(int i = 0; i < insts.length; i++){
              if(insts[i] != null){
                 if(current < 0){
                     current = i;
                     if(sec !=  insts[current].getTag()){currentCMD ++;}
                     insts[current].loadInstances();
                     hasSections = true;
                 }
                 numFiles += insts[i].size();
              }
         }
   }

   public void nextSection() throws IOException {
       int i = current+1;
       hasSections = false;
       short sec = insts[current].getTag();
       while(i < insts.length){
           if(insts[i] != null){
               current = i;
               if(sec !=  insts[current].getTag()){currentCMD++;}
               insts[current].loadInstances();
               i = insts.length;
               hasSections = true;
           }
           i++;
       }
   }

   public boolean hasFilesToProcess(){
        return hasSections;
   }

   public String getSectionName(){ return insts[current].getDescription();}

   public String getSectionName(int sec){
      if(sec < insts.length){
           if(insts[sec] != null){
               return insts[sec].getDescription();
           }
       }
       return null;
   }

   public int getSectionTag(int sec){
       if(sec < insts.length){
           if(insts[sec] != null){
               return insts[sec].getTag();
           }
       }
       return 0;
   }

   public int getTotalFiles(){return numFiles;}

   public int getProcessedFiles(){return procFiles;}

   public int getFailsCount(){return insts[current].rejected(); }

   public int processedCurrentRepo(){ return insts[current].processedFiles(); }

   public LinkedList<InstanceResult> getFailsOfSection(int sec){
       if(sec < insts.length){
           if(insts[sec] != null){
               return insts[sec].getFails();
           }
       }
       return null;
    }

   public boolean advanceTest() throws IOException {
         switch(insts[current].getTag()){
               case SYN :    return processSimpleOutputFile();
               case SEM:     return processInputDependentFile();
               case TY:      return processSimpleOutputFile();
         }
         return false;
   }


   public String debugCmd(){
        String toRun, cmds;
        toRun = "Rodar em: ";
        for(int i =0; i < insts.length; i++){
          if(insts[i] != null){
             toRun += insts[i].getPath() + "\n";
          }
        }
        cmds = "Comandos para rodar: ";
        for(String c : cmd){
            cmds += c +  "\n";
        }
        return cmds + "________________________\n" + toRun;
   }


   // Processa um único arquivo e retorna true, se ainda há aqruivos para processar.
   private boolean processSimpleOutputFile() throws IOException{
       Process p;
       String out;
       BufferedReader procr;
       if(insts[current] != null){
            if( insts[current].current() != null){
                p = Runtime.getRuntime().exec(cmd[currentCMD] + " " + insts[current].current().getAbsolutePath());
                procr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                boolean found = false;
                out = procr.readLine();
                if(insts[current].isMultiLine()){
                    while(out != null && !found){
                        found = out.equals(insts[current].getExpectedOutput());
                        out = procr.readLine();
                    }
                }else{ found = out.equals(insts[current].getExpectedOutput());}
                if(found){
                    insts[current].acceptCurrent();
                }else{
                    insts[current].rejectCurrent();
                }
                procFiles++;
            }
       }
       return insts[current].hasNext();
   }


   private LinkedList<TestInstance> LoadInstance(String fn) throws IOException {
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
        }catch(IOException e){
             failedInstaceFiles.add(fn);
             r.close();
             inst = null;
        }
        return inst;
   }


   private boolean processInputDependentFile() throws IOException{
        boolean res = true;
        LinkedList<TestInstance> fails = new LinkedList<TestInstance>();
        LinkedList<TestInstance> ltest = LoadInstance(insts[current].current().getAbsolutePath().replaceFirst("\\.[^\\.]+$",".inst"));
        if(insts[current] != null){
            if( insts[current].current() != null){
                if(ltest != null){
                    Process p;
                    BufferedReader procr;
                    BufferedWriter procw;
                    for(TestInstance t : ltest){
                         p = Runtime.getRuntime().exec(cmd[currentCMD] + " " + insts[current].current().getAbsolutePath());
                         procr = new BufferedReader(new InputStreamReader(p.getInputStream())); // the process outputs
                         procw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream())); //the process inputs
                         if(t.inData != null){
                             for(String din : t.inData){
                                 procw.write(din, 0, din.length());
                                 procw.newLine();
                                 procw.flush();
                             }
                         }
                         LinkedList<String> dout = new LinkedList<String>();
                         String x =  procr.readLine();
                         while(x != null && (dout.size() < 2*t.outData.size()) ){
                              dout.add(x);
                              x = procr.readLine();
                         }
                         if(t.outData != null){
                             if(t.testOutput(dout)){
                                res = res && true;
                             }else{
                                fails.add(t);
                                res = res && false;
                             }
                         }else if(dout.size() > 0){
                                fails.add(t);
                                res = res && false;
                         }
                    }
                }
                procFiles++;
            }
        }
        if(res){
            insts[current].acceptCurrent();
        }else{
            insts[current].rejectCurrent(fails);
        }
        return insts[current].hasNext();
   }

   /*
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






   public static void semTests(String cmd) throws IOException {
       // String cmd = "./lang.sh -i";
       String pthSimple = mkPath(new String[]{"semantica","certo","simple"});
       String pthFunc   = mkPath(new String[]{"semantica","certo","function"});
       String pthFull   = mkPath(new String[]{"semantica","certo","full"});
       String[] pths    = new String[]{pthSimple,pthFunc,pthFull};
       LinkedList<InstanceResult> tr = new LinkedList<InstanceResult>();
       try{
          int passed = 0, failed = 0, maxfname;
          for(String spth : pths){
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
          }
          reportSem(passed, failed, tr);
      }catch(Exception e){
          e.printStackTrace();
      }
   }


   public static void reportSem(int pass, int fails, LinkedList<InstanceResult> sfail){
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
                             System.out.println("\""+s1+"\"");
                        }
                    }
                    System.out.println("Saida obtida:");
                    if(tif.out == null || tif.out.size() == 0){
                        System.out.println("N/A");
                    }else{
                         for(String s1 : tif.out){
                             System.out.println("\"" + s1 + "\"");
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


   public static void main(String[] args) throws IOException {
       BufferedReader confs = new BufferedReader( new FileReader("conf.txt"));
       String cmd1 = confs.readLine();
       String cmd2 = confs.readLine();
       String cmd3 = confs.readLine();
       confs.close();
       //System.out.println(cmd1);
       //System.out.println(cmd2);
       //String cmd1 = "java -cp ..:../lang/tools/java-cup-11b-runtime.jar lang/LangCompiler -syn";
       //String cmd2 = "java -cp ..:../lang/tools/java-cup-11b-runtime.jar lang/LangCompiler -i";
       if(args.length  == 0){
         synTests(cmd1);
         semTests(cmd2);
       }else if(args[0].equals("-syn")){
         synTests(cmd1);
       }else if(args[0].equals("-sem")){
         semTests(cmd2);
       }else if(args[0].equals("-type")){
         typeTests(cmd3);
       }else{
          System.out.println("Chame com -syn ou -sem ou sem parâmetros.");
       }
   }*/


}

