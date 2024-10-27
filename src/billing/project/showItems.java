
package billing.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

public class showItems extends JFrame implements ActionListener {
    
    DefaultTableModel tableModel;
    JTable dataTable;
 
    JPanel ButtonPanel;
    JButton deleteItem;
    
    TableColumnModel columnModel;
    
    JPanel panel1;
    
    public showItems(){
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        setExtendedState(showItems.MAXIMIZED_BOTH);
        
        ButtonPanel = new JPanel(new GridBagLayout());  
        ButtonPanel.setBackground(Color.BLACK);
        ButtonPanel.setForeground(Color.WHITE);
        
        ButtonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Item_No");
        tableModel.addColumn("Item_Name");
        tableModel.addColumn("Item_Amount");
        tableModel.addColumn("Item_Total");
        tableModel.addColumn("Item_Available");
        this.dataTable = new JTable(tableModel);
        
        dataTable.setRowHeight(30);
        dataTable.setFont(new Font("MS Mincho", Font.PLAIN, 18));
        
        TableCellRenderer customRenderer = (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
            JLabel cell = new JLabel(value.toString());
            cell.setFont(new Font("MS Mincho", Font.ITALIC, 16));
            cell.setOpaque(true);
            cell.setBackground(row % 2 == 0 ? new Color(45, 45, 45) : new Color(55, 55, 55));
            cell.setForeground(Color.WHITE);
            return cell;
        };
        
        for(int i = 0 ; i < dataTable.getColumnCount() ; i++){
            dataTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }
        
        JTableHeader header = dataTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 18)); // Header font style
        header.setBackground(new Color(60, 60, 60)); // Header background color
        header.setForeground(Color.WHITE); // Header text color
        header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Header height
        
        
        columnModel = this.dataTable.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(300);
        columnModel.getColumn(1).setPreferredWidth(500);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(500);
        columnModel.getColumn(4).setPreferredWidth(500);
        
        deleteItem = new JButton("Remove Item");
        deleteItem.setBackground(Color.WHITE);
        deleteItem.setForeground(Color.BLACK);
        deleteItem.addActionListener(this);
        ButtonPanel.add(deleteItem);
        deleteItem.setBackground(Color.LIGHT_GRAY);
        deleteItem.setPreferredSize(new Dimension(150, 40));
        deleteItem.setFocusable(false);
        
        
        
        
        panel1 = new JPanel(new BorderLayout()); // Change layout to BorderLayout
        panel1.add(new JScrollPane(dataTable), BorderLayout.CENTER); // Add table to center
        
        this.add(ButtonPanel, BorderLayout.NORTH);
        this.add(panel1, BorderLayout.CENTER);
        this.setVisible(true);
        
        displayItems();
   
    }
        String url = Main.url;
        String uname = Main.uname;
        String pass = Main.pass;
    
    public void displayItems(){
        
        tableModel.setRowCount(0);
        
        String q="select * from items";
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            Statement st = con.createStatement();
            
            ResultSet rs = st.executeQuery(q);
            
            while(rs.next()){
                String itemId = rs.getString("Item_id");
                String itemName = rs.getString("item_name");
                String itemAmt = rs.getString("Amount");
                String itemTotal = rs.getString("Total_Available");
                String itemAvail = rs.getString("Available");
                
                tableModel.addRow(new Object[]{itemId,itemName,itemAmt,itemTotal,itemAvail});
            }
            st.close();
            con.close();
        }catch(Exception e){
           JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeItem(){
        
        int selectedRowIndex = dataTable.getSelectedRow();
            
                if(selectedRowIndex!=-1){
                    Object[] rowData = new Object[tableModel.getColumnCount()];


                    for(int i=0;i<tableModel.getColumnCount();i++){
                        rowData[i] = tableModel.getValueAt(selectedRowIndex,i);
                    }

                    try{  
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con;
                        con = DriverManager.getConnection(url,uname,pass);
                        
                        String getId = "select Item_id from items where item_name = ?";
                        PreparedStatement pstm1 = con.prepareStatement(getId);
                        pstm1.setString(1, rowData[1].toString());
                        ResultSet rs = pstm1.executeQuery();
                        rs.next();
                        System.out.println();
                        PreparedStatement pstm2;

                        String q1 = "delete from items where Item_id=?";
                        pstm2 = con.prepareStatement(q1);
                        pstm2.setInt(1, rs.getInt("Item_id"));
                        pstm2.executeUpdate();
                        
                        displayItems();
                        con.close();
                    }catch(ClassNotFoundException | NumberFormatException | SQLException e){
                        JOptionPane.showMessageDialog(null, "OOPS! item might be booked!", "Not Found", JOptionPane.ERROR_MESSAGE);
                        //e.printStackTrace();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Please select a row", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == deleteItem){
            removeItem();
        }
    }
}
