package langtester;

import java.util.List;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IDFailsDialog extends JDialog  implements ActionListener{

    private JPanel top, results ,controls;
    private JCheckBox[] testOpts;
    private JTable list;
    private JButton btOk;
    private JLabel jtitle;
    private JTextArea exp, found;

    private ResultsFail rf;

    private void buildTopPanel(String title){
         top = new JPanel(new FlowLayout(FlowLayout.CENTER));
         jtitle = new JLabel(title);
         top.add(jtitle);
    }

    private void unlines(LinkedList<String> xs, JTextArea a){
        for(String x : xs){
            a.append(x+"\n");
        }
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
         list.addMouseListener(new MouseAdapter() {
              public void mousePressed(MouseEvent mouseEvent) {
                    JTable table =(JTable) mouseEvent.getSource();
                    Point point = mouseEvent.getPoint();
                    int grow = table.rowAtPoint(point);
                    exp.setText("");
                    found.setText("");
                    if ( table.getSelectedRow() != -1 && grow != -1) {
                        int row = table.convertRowIndexToModel(grow);
                        if(l.get(row) != null){
                             for(TestInstance ti : l.get(row).fails){
                                  unlines(ti.outData,exp);
                                  unlines(ti.out,found);
                                  found.append("-x-x-x-x-x-x-x-x-x-x-x-x-x\n");
                                  exp.append("-x-x-x-x-x-x-x-x-x-x-x-x-x\n");
                             }
                        }
                    }
              }
         });

         JPanel compare = new JPanel(new FlowLayout(FlowLayout.LEFT));
         TitledBorder br1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Saída Esperada");
         TitledBorder br2 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Saída Registrada");
         exp = new JTextArea(12,35);
         exp.setEditable(false);
         exp.setBorder(br1);
         found = new JTextArea(12,35);
         found.setEditable(false);
         found.setBorder(br2);
         compare.add(exp);
         compare.add(found);

         results =  new JPanel();
         results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
         TitledBorder resultstTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Instâncias ");
         results.add(scr);
         results.add(compare);
         results.setBorder(resultstTitle);

    }

    private void buildControlsPanel(){
         controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
         btOk = new JButton("OK");
         btOk.addActionListener(this);
         controls.add(btOk);
    }


    public IDFailsDialog(JFrame owner, String title,String testName, LinkedList<InstanceResult> l){
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
