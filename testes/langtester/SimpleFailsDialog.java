package langtester;

import java.util.List;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

public class SimpleFailsDialog extends JDialog  implements ActionListener{

    private JPanel top, results ,controls;
    private JCheckBox[] testOpts;
    private JTable list;
    private JButton btOk;
    private JLabel jtitle;

    private ResultsFail rf;

    private void buildTopPanel(String title){
         top = new JPanel(new FlowLayout(FlowLayout.CENTER));
         jtitle = new JLabel(title);
         top.add(jtitle);
    }

    private void buildResultsPanel(LinkedList<InstanceResult> l){
         rf = new ResultsFail(l.size());
         int row = 0;
         for(InstanceResult i : l){
             rf.setValueAt(i.fname,row,0);
             row++;
         }
         list = new JTable(rf);
         JScrollPane scr = new JScrollPane(list);
         list.setFillsViewportHeight(true);
         results =  new JPanel();
         results.setLayout(new BoxLayout(results, BoxLayout.X_AXIS));
         TitledBorder resultstTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Instâncias ");
         results.add(scr);
         results.setBorder(resultstTitle);
    }

    private void buildControlsPanel(){
         controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
         btOk = new JButton("OK");
         btOk.addActionListener(this);
         controls.add(btOk);
    }


    public SimpleFailsDialog(JFrame owner, String title,String testName, LinkedList<InstanceResult> l){
         super(owner,"Diálogo de Instâncis Rejeitadas");


         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
         this.setLayout(new BoxLayout( getContentPane(), BoxLayout.Y_AXIS));

         // Criando o painel de resultados
         buildTopPanel("Os seguintes arquivos de teste falharam no teste \"" + testName + "\"");
         buildResultsPanel(l);
         buildControlsPanel();


         // Adicionado cada painel ao frame principal.
         getContentPane().add(top);
         getContentPane().add(results);
         getContentPane().add(controls);

         pack();
         setVisible(true);
    }


    public void actionPerformed(ActionEvent e) {
         if(e.getSource() == btOk){
              setVisible(false);
              dispose();
         }
    }

}
