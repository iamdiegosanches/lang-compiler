package langtester;

import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class TesterUI extends JFrame  implements ActionListener{

    private JPanel tests, results, confs,progress,controls;
    private JCheckBox[] testOpts;
    private JTable tbResults;
    private JProgressBar jp;
    private JTextField appLocation, baseCmd, parseOpt, interpOpt, typeOpt, compOpt, paramextras;
    private JButton btRun, btStop, btAppSelect, btTestCompile,btSaveConf;
    private JList lresutl;
    private String[] chkOpts = {"Sintático", "Interpretador","Sistema de Tipos","Compilador"};
    private Integer[] chkMnc = {KeyEvent.VK_S, KeyEvent.VK_I,KeyEvent.VK_T,KeyEvent.VK_C};


    private ResultsTM rm;
    private LangTester ltt;
    private RunTests rt;


    private void buildTestsPanel(){
         testOpts = new JCheckBox[chkOpts.length];
         for(int i =0; i< chkOpts.length; i++){
             testOpts[i] = new JCheckBox(chkOpts[i],false);
             testOpts[i].setMnemonic(chkMnc[i]);
         }
         testOpts[chkOpts.length-1].setEnabled(false);
         tests  = new JPanel(new FlowLayout());
         TitledBorder testTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Testes Disponíveis");
         for(JCheckBox chkb : testOpts){ tests.add(chkb);}
         tests.setBorder(testTitle);
    }


    private void buildConfsPanel(){
         confs = new JPanel();
         confs.setLayout(new BoxLayout(confs, BoxLayout.Y_AXIS));
         TitledBorder confTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Configurações da execução dos testes");

         JPanel appDir = new JPanel(new FlowLayout(FlowLayout.LEFT));
         TitledBorder addbrd = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Arquivo exectutável compilador");
         appLocation = new JTextField(40);
         btAppSelect = new JButton("Procurar");
         btSaveConf = new JButton("Salvar Conf.");
         btSaveConf.addActionListener(this);
         appDir.add(appLocation);
         appDir.add(btAppSelect);
         appDir.add(btSaveConf);
         appDir.setBorder(addbrd);

         JPanel baseCmdPanel = new JPanel();
         baseCmdPanel.setLayout(new BoxLayout(baseCmdPanel,BoxLayout.Y_AXIS));
         TitledBorder bCmdBrd = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Comando para executar o compilador");
         JLabel lbcmd = new JLabel("Comando base:");
         baseCmd   = new JTextField(30);
         JLabel lbExtras = new JLabel("Parâmetros adicionais");
         paramextras = new JTextField(30);

         baseCmdPanel.add(lbcmd);
         baseCmdPanel.add(baseCmd);
         baseCmdPanel.add(lbExtras);
         baseCmdPanel.add(paramextras);
         baseCmdPanel.setBorder(bCmdBrd);

         JPanel appOpts = new JPanel(new FlowLayout(FlowLayout.LEFT));
         TitledBorder appOptsBrd = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Opções a serem passadas para o compilador");
         JLabel lbsyn = new JLabel("Análise sintática:");
         parseOpt  = new JTextField(10);
         JLabel lbinter = new JLabel("Interpretação:");
         interpOpt = new JTextField(10);
         JLabel lbty = new JLabel("Verificação de tipos:");
         typeOpt   = new JTextField(10);
         JLabel lbcmp = new JLabel("Compilação:");
         compOpt = new JTextField(10);
         appOpts.add(lbsyn);
         appOpts.add(parseOpt);
         appOpts.add(lbinter);
         appOpts.add(interpOpt);
         appOpts.add(lbty);
         appOpts.add(typeOpt);
         appOpts.add(lbcmp);
         appOpts.add(compOpt);
         appOpts.setBorder(appOptsBrd);

         confs.add(appDir);
         confs.add(baseCmdPanel);
         confs.add(appOpts);

         confs.setBorder(confTitle);
    }


    private void buildResultsPanel(){
         rm = new ResultsTM(9);
         tbResults = new JTable(rm);
         JScrollPane scr = new JScrollPane(tbResults);
         tbResults.setFillsViewportHeight(true);
         tbResults.addMouseListener(new MouseAdapter() {
              public void mousePressed(MouseEvent mouseEvent) {
                    JTable table =(JTable) mouseEvent.getSource();
                    Point point = mouseEvent.getPoint();
                    int row = table.rowAtPoint(point);
                    if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1 && row != -1) {
                        int modelRow = table.convertRowIndexToModel(row);
                        if(ltt.getFailsOfSection(modelRow) != null){
                           if(ltt.getSectionTag(modelRow) != ltt.SEM){
                              SimpleFailsDialog fd = new SimpleFailsDialog(TesterUI.this, "Arquivos Rejeitados",(String)rm.getValueAt(modelRow,0), ltt.getFailsOfSection(modelRow));
                              fd.show();
                           }else{
                              IDFailsDialog fd = new IDFailsDialog(TesterUI.this, "Arquivos Rejeitados",(String)rm.getValueAt(modelRow,0), ltt.getFailsOfSection(modelRow));
                           }
                        }
                    }
              }
         });
         results =  new JPanel();
         results.setLayout(new BoxLayout(results, BoxLayout.X_AXIS));
         TitledBorder resultstTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Resultados");
         results.add(scr);
         results.setBorder(resultstTitle);
    }


    private void buildProgressPanel(){
         progress = new JPanel();
         progress.setLayout(new BoxLayout(progress,BoxLayout.X_AXIS));
         TitledBorder progressoTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Progresso");
         jp = new JProgressBar(0,100);
         progress.add(jp);
         progress.setBorder(progressoTitle);
    }


    private void buildControlsPanel(){
         controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
         btRun = new JButton("Executar");
         btRun.addActionListener(this);
         btStop = new JButton("Parar");
         btStop.addActionListener(this);
         btStop.setEnabled(false);
         btTestCompile = new JButton("Testar Conf.");
         btTestCompile.addActionListener(this);
         controls.add(btRun);
         controls.add(btStop);
         controls.add(btTestCompile);
    }


    public TesterUI(){
         super("Lang Tester");


         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         this.setLayout(new BoxLayout( getContentPane(), BoxLayout.Y_AXIS));
         // Criando o painel de seleção de testes
         buildTestsPanel();
         // Criando Painel de configurações;
         buildConfsPanel();
         // Criando o painel de resultados
         buildResultsPanel();
         buildProgressPanel();
         buildControlsPanel();


         // Adicionado cada painel ao frame principal.
         getContentPane().add(tests);
         getContentPane().add(confs);
         getContentPane().add(results);
         getContentPane().add(progress);
         getContentPane().add(controls);


         pack();
         try{
             TesterConf t = TesterConf.getInstance();
             t.loadFromFile();
             appLocation.setText(t.prog);
             baseCmd.setText(t.preCmd);
             paramextras.setText(t.preOpts);
             parseOpt.setText(t.parseOpt);
             interpOpt.setText(t.interpOpt);
             typeOpt.setText(t.typeOpt);
             compOpt.setText(t.compOpt);
         }catch(IOException e){
         }
         setVisible(true);
    }


    private int selectedTests(){
        int k = 0;
        for(int i =0; i < testOpts.length; i++){
            if(testOpts[i].isSelected()){ k = k + 1; }
        }
        return k;
    }


    public void actionPerformed(ActionEvent e) {
         if(e.getSource() == btRun){
              btStop.setEnabled(true);
              btRun.setEnabled(false);
              rm.clear();
              update(this.getGraphics());
              runTests();
         }else if(e.getSource() == btStop) {
              rt.cancel(false);
         }else if(e.getSource() == btAppSelect) {
         }else if(e.getSource() == btTestCompile){
              testCompile();
         }else if(e.getSource() == btSaveConf){
              try{
                  TesterConf t = TesterConf.getInstance();
                  t.preCmd = baseCmd.getText();
                  t.preOpts =paramextras.getText();
                  t.prog = appLocation.getText();
                  t.parseOpt = parseOpt.getText();
                  t.interpOpt = interpOpt.getText();
                  t.typeOpt = typeOpt.getText();
                  t.compOpt = compOpt.getText();
                  t.sateToFile();
              }catch(IOException ex){
                  JOptionPane.showMessageDialog(this,"Erro ao tentar salvar configurações\n" + ex.getMessage(),"Erro salvando configurações",JOptionPane.ERROR_MESSAGE);
              }
         }
    }


    private short numToOpt(int i){
         switch(i){
            case 0 : return LangTester.SYN;
            case 1 : return LangTester.SEM;
            case 2 : return LangTester.TY;
            case 3 : return LangTester.COM;
         }
         return LangTester.SYN;
    }


    private void runTests(){

         String rnCmd[] = new String[selectedTests()];
         short opts[] = new short[selectedTests()];
         JTextField fields[] = {parseOpt,interpOpt,typeOpt,compOpt};
         int j = 0,k=0;
         String icmd = !baseCmd.getText().trim().isEmpty() ?  baseCmd.getText().trim() +
                      " " + paramextras.getText().trim() + " " : "";
         for(JCheckBox jck : testOpts){
            if(jck.isSelected()){
                 rnCmd[k] = icmd + appLocation.getText() + " " + fields[j].getText();
                 opts[k] = numToOpt(j);
                 k++;
            }
            j++;
         }
         try{
             rt = new RunTests(this,rm,opts,rnCmd);
             rt.addPropertyChangeListener(new PropertyChangeListener() {
                         public  void propertyChange(PropertyChangeEvent evt) {
                                   if ("progress".equals(evt.getPropertyName())) {
                                         jp.setValue((Integer)evt.getNewValue());
                                   }
                         }
             });
             jp.setValue(0);
             rt.execute();
         }catch(Exception ex){
              JOptionPane.showMessageDialog(this, ex.getMessage(),"Erro durante testes:",JOptionPane.ERROR_MESSAGE);
         }


    }


    private void testCompile(){
        String rnCmd[] = new String[selectedTests()+1];
        JTextField fields[] = {parseOpt,interpOpt,typeOpt,compOpt};
        int j = 0,k=0;
        String icmd = !baseCmd.getText().trim().isEmpty() ?  baseCmd.getText().trim() +
                      " " + paramextras.getText().trim() + " " : "";
        for(JCheckBox jck : testOpts){
            if(jck.isSelected()){
                 rnCmd[k] = icmd + appLocation.getText() + " " + fields[j].getText();
                 k++;
            }
            j++;
        }
        String cmdTest =  icmd + appLocation.getText() + " -v";
        Runtime rn = Runtime.getRuntime();
        try{
             Process p;
             BufferedReader procr,errcr;
             p = rn.exec(cmdTest);
             procr = new BufferedReader(new InputStreamReader(p.getInputStream())); // the process outputs
             errcr =  new BufferedReader(new InputStreamReader(p.getErrorStream()));
             String line = procr.readLine();
             if(line != null ){
                 JOptionPane.showMessageDialog(this,line,
                                                    "Teste do Compilador",JOptionPane.PLAIN_MESSAGE);
             }else {
                 line = errcr.readLine();
                 if(line != null){
                    JOptionPane.showMessageDialog(this,line,
                                                    "Teste do Compilador: Erro",JOptionPane.ERROR_MESSAGE);
                  }
             }


        }catch(Exception  ex){
             JOptionPane.showMessageDialog(this,cmdTest + "\n" + ex.getMessage(),"Exceção na execução do comando",JOptionPane.ERROR_MESSAGE);
        }
    }


    private class RunTests extends SwingWorker< LangTester, DRow> {

         private ResultsTM rm;
         private short[] opts;
         private String[] cmds;
         private JFrame jf;


         public RunTests(JFrame jf, ResultsTM rm, short[] opts,String[] cmds){
             this.rm = rm;
             this.opts = opts;
             this.cmds = cmds;
             this.jf = jf;
         }


         public LangTester doInBackground() {
                LangTester lngt = null;
                try{
                    int row = 0;
                    lngt = new LangTester(opts,cmds);
                    if(lngt.hasFilesToProcess()){
                        publish(new DRow (row,lngt.getSectionName(),null,-1));
                        while(lngt.hasFilesToProcess() && !isCancelled()){
                            if(lngt.advanceTest()){
                                firePropertyChange("progress",0, (100*lngt.getProcessedFiles())/lngt.getTotalFiles());
                                publish(new DRow (row,null,"Rodando..." + lngt.processedCurrentRepo() ,lngt.getFailsCount()));
                            }else{
                                publish(new DRow (row,lngt.getSectionName(),"Concluído " + lngt.processedCurrentRepo(),lngt.getFailsCount()));
                                row ++;
                                lngt.nextSection();
                            }
                        }
                        firePropertyChange("progress",0, (100*lngt.getProcessedFiles())/lngt.getTotalFiles());
                        if(isCancelled()) publish(new DRow (row,lngt.getSectionName(),"Cancelado" + lngt.processedCurrentRepo(),lngt.getFailsCount()));
                    }else{
                        JOptionPane.showMessageDialog(jf,"Nenhum arquivo para processar!","Executar Teste",JOptionPane.PLAIN_MESSAGE);
                    }
                    ltt = lngt;


                }catch(Exception ex){
                    JOptionPane.showMessageDialog(jf,ex.getMessage(),"Erro durante os testes",JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
                return lngt;
         }


         public void process(List<DRow> d){
              for(DRow r : d){
                 if(r.desc != null){rm.setValueAt(r.desc,r.row,0);}
                 if(r.status != null){rm.setValueAt(r.status,r.row,1);}
                 if(r.fail >= 0 ){rm.setValueAt(r.fail,r.row,2);}
              }
         }


         public void done(){
              btStop.setEnabled(false);
              btRun.setEnabled(true);
              firePropertyChange("progress",0, 0);
         }
    }

    private class DRow{
        public int row;
        public int fail;
        public String status;
        public String desc;


        public DRow(int r, String dsc, String sts, int f){
               row = r;
               desc = dsc;
               status = sts;
               fail = f;
        }
    }


    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TesterUI();
            }
        });
    }

}
