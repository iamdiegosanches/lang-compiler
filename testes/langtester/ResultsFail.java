package langtester;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ResultsFail extends AbstractTableModel {
    private String[] columnNames = {"Instância de teste"};
    private Object[][] data;

    public ResultsFail(int files){
        data = new Object[files][1];
        for(int i = 0; i < files; i++){
               data[i][0] = "";
        }
    }

    public void clear(){
        for(int i = 0; i < data.length; i++){
               data[i][0] = "";
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
            return false;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}
