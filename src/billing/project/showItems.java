
package billing.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
        this.setLayout(null);
        this.setSize(800,500);
        
        ButtonPanel = new JPanel();
        ButtonPanel.setBounds(0,0,800,50);
        
        
        
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Item_No");
        tableModel.addColumn("Item_Name");
        tableModel.addColumn("Item_Amount");
        tableModel.addColumn("Item_Total");
        tableModel.addColumn("Item_Available");
        this.dataTable = new JTable(tableModel);
        
        columnModel = this.dataTable.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(300);
        columnModel.getColumn(1).setPreferredWidth(500);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(500);
        columnModel.getColumn(4).setPreferredWidth(500);
        
        deleteItem = new JButton("Remove Item");
        deleteItem.addActionListener(this);
        ButtonPanel.add(deleteItem);
        deleteItem.setBackground(Color.LIGHT_GRAY);
        deleteItem.setPreferredSize(new Dimension(150, 40));
        deleteItem.setFocusable(false);
        
        
        
        
        panel1 = new JPanel();
        panel1.setBounds(0,60,800,400);

        panel1.add(new JScrollPane(dataTable));
        this.add(ButtonPanel);
        this.add(panel1);
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
