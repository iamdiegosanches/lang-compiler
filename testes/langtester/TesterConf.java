package langtester;

import java.io.*;

public class TesterConf {

     public String preCmd, preOpts, prog, parseOpt, interpOpt , typeOpt, compOpt;
     public BufferedReader in;
     public BufferedWriter out;
     private String confFile;
     private static TesterConf instance;

     private TesterConf(){
        confFile = "testerconf.txt";
     }

     public static TesterConf getInstance(){
          if(instance == null){
             instance = new TesterConf();
          }
          return instance;
     }

     public void loadFromFile() throws IOException {
           in = new BufferedReader(new FileReader(confFile));
           preCmd = in.readLine();
           preOpts = in.readLine();
           prog = in.readLine();
           parseOpt = in.readLine();
           interpOpt = in.readLine();
           typeOpt = in.readLine();
           compOpt = in.readLine();
           in.close();
     }

     public void sateToFile() throws IOException{
           out = new BufferedWriter( new FileWriter(confFile));
           out.write(preCmd,0,preCmd.length());
           out.newLine();
           out.write(preOpts,0,preOpts.length());
           out.newLine();
           out.write(prog,0,prog.length());
           out.newLine();
           out.write(parseOpt,0,parseOpt.length());
           out.newLine();
           out.write(interpOpt,0,interpOpt.length());
           out.newLine();
           out.write(typeOpt,0,typeOpt.length());
           out.newLine();
           out.write(compOpt,0,compOpt.length());
           out.newLine();
           out.close();
     }

}
