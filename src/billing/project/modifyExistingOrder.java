
package billing.project;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class modifyExistingOrder implements ActionListener{
    
    JFrame frame = new JFrame();
    
    JComboBox displayClientName;
    JComboBox displayItemName;
    
    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JPanel panel4;
    JPanel panel5;
    JPanel panel6;
    JPanel MainPanel1;
    JPanel MainPanel2;
    
    JTextField labourCharge;
    JTextField quantity;
    
    DefaultTableModel tableModel;
    JTable dataTable;
    TableColumnModel columnModel;
    
    JLabel totalLabel;
    JLabel totalAmountLabel;
    
    JButton selectClient;
    JButton addNewItem;
    JButton removeItem;
    JButton modifyItem;
    JButton UpdateModified;
    JButton addItem;
    
    
    String url = Main.url;
    String uname = Main.uname;
    String pass = Main.pass;
    
    HashMap<String, Integer> displayItemMap;
    
    public modifyExistingOrder() {
        
        addNewItem = new JButton("addNewItem");
        addNewItem.addActionListener(this);
        addNewItem.setBackground(Color.LIGHT_GRAY);
        addNewItem.setPreferredSize(new Dimension(150, 40));
        removeItem = new JButton("removeItem");
        removeItem.addActionListener(this);
        removeItem.setBackground(Color.LIGHT_GRAY);
        removeItem.setPreferredSize(new Dimension(150, 40));
        modifyItem = new JButton("modifyItem");
        modifyItem.addActionListener(this);
        modifyItem.setBackground(Color.LIGHT_GRAY);
        modifyItem.setPreferredSize(new Dimension(150, 40));
        
        totalLabel = new JLabel("Total Amount:");
        totalAmountLabel = new JLabel("Rs: 0.00");
        selectClient = new JButton("Select");
        selectClient.setBackground(Color.LIGHT_GRAY);
        selectClient.setPreferredSize(new Dimension(150, 40));
        selectClient.addActionListener(this);
        displayClientName = new JComboBox();
        displayClientName.setPreferredSize(new Dimension(150, 40));
       
        panel1 = new JPanel();
        panel1.add(new JLabel("Select ClientName : "));
        panel1.add(displayClientName);
        panel1.add(selectClient);
        
        panel2 = new JPanel();
        panel2.setBounds(0,60,580,50);
        panel2.add(addNewItem);
        panel2.add(removeItem);
        panel2.add(modifyItem);
        panel2.setVisible(false);
        
        panel3 = new JPanel();
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Item_id");
        tableModel.addColumn("Item_name");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Amount");
        dataTable = new JTable(tableModel);
        panel3.add(new JScrollPane(this.dataTable));
        columnModel = this.dataTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(550);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(500);
        panel3.add(new JScrollPane(this.dataTable));
        panel3.setVisible(false);
        
        panel4 = new JPanel();
        labourCharge = new JTextField();
        labourCharge.setPreferredSize(new Dimension(150, 20));
        panel4.add(new JLabel("Labour&Transport :  "));
        panel4.add(labourCharge);
        labourCharge.setPreferredSize(new Dimension(150, 40));
        panel4.add(totalAmountLabel);
        panel4.setVisible(false);
        
        panel5 = new JPanel();
        UpdateModified = new JButton("Update");
        UpdateModified.setBackground(Color.LIGHT_GRAY);
        UpdateModified.setPreferredSize(new Dimension(150, 40));
        UpdateModified.addActionListener(this);
        panel5.add(UpdateModified);
        panel5.setVisible(false);
                
        panel6 = new JPanel();
        displayItemName = new JComboBox();
        displayItemName.setPreferredSize(new Dimension(150, 40));
        quantity = new JTextField();
        quantity.setPreferredSize(new Dimension(200,30));
        panel6.add(displayItemName);
        panel6.add(new JLabel("Quantity : "));
        panel6.add(quantity);
        quantity.setPreferredSize(new Dimension(150, 40));
        addItem = new JButton("Add");
        addItem.setBackground(Color.LIGHT_GRAY);
        addItem.setPreferredSize(new Dimension(150, 40));
        addItem.addActionListener(this);
        panel6.add(addItem);
        panel6.setVisible(false);
        
        MainPanel1 = new JPanel();
        MainPanel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        MainPanel1.add(panel1);
        MainPanel1.add(panel2);
        MainPanel1.add(panel3);
        MainPanel1.add(panel4);
        MainPanel1.add(panel5);
        
        MainPanel2 = new JPanel();
        MainPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        MainPanel2.add(panel6);
        
        frame.add(MainPanel1);
        frame.add(MainPanel2);
        //frame.addWindowListener(listener);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(1200,730);
        frame.setLayout(new GridLayout(0,2));
        frame.setVisible(true);
        clientNameDisplay();
        
        displayItemMap = new HashMap<>();
        
    }
    int client_id;
    
    void clientNameDisplay(){
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            Statement st = con.createStatement();
            
            String q = "select client_name,Event_date from client_details where Event_date >= curdate()";
            ResultSet rs = st.executeQuery(q);
            while(rs.next()){
                displayClientName.addItem(rs.getString("client_name")+"("+rs.getString("Event_date")+")");
            }
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Error"+e, "Error Occured", JOptionPane.ERROR_MESSAGE);
            //frame.dispose();
        }
    }
    
    double totalAmount; 
    
    void OperationsOnselectedClient(){         
            panel2.setVisible(true);
            panel3.setVisible(true);
            panel4.setVisible(true);
            displayItems();
    }
       
    void displayItems(){
        tableModel.setRowCount(0);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url, uname, pass);
            String getClientId = "Select client_id from client_details where client_name = ? and Event_date = ?";
            
            PreparedStatement pstm = con.prepareStatement(getClientId);
            String Name = (String) displayClientName.getSelectedItem();
            
            try{
                String[] result = Name.split("\\(");
                String getName = result[0];

                String  getDate = result[1].substring(0,result[1].length()-1);

                pstm.setString(1,getName);
                try{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(getDate);
                    java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

                    pstm.setDate(2,sqlDate);
                    ResultSet rS = pstm.executeQuery();
                    rS.next();
                    client_id = rS.getInt("client_id") ;
                }catch(SQLException | ParseException e2){
                    JOptionPane.showMessageDialog(null,e2,"Error",JOptionPane.ERROR_MESSAGE);
                }

                String q1 = "select ib.client_id,ib.item_id,ib.item_name,ib.item_Qnt,pi.total_items_Amount from item_booked ib JOIN price_info pi on ib.client_id = pi.client_id where ib.client_id=?";
                String q2 = "select Amount from items where item_id = ?";
                String q3 = "select labour from price_info where client_id =?";
                pstm = con.prepareStatement(q1);
                pstm.setInt(1,client_id);
                ResultSet rs = pstm.executeQuery();
                
                while(rs.next()){
                    PreparedStatement pstm2 = con.prepareStatement(q2);
                    pstm2.setInt(1,rs.getInt("item_id"));
                    ResultSet rs2 =  pstm2.executeQuery();
                    rs2.next();
                    double amt = rs.getInt("item_Qnt") * rs2.getFloat("Amount");
                    tableModel.addRow(new Object[]{rs.getInt("item_id"), rs.getString("item_name"), rs.getInt("item_Qnt"), amt });
                }
                pstm = con.prepareStatement(q3);
                pstm.setInt(1,client_id);
                rs = pstm.executeQuery();
                rs.next();
                double labour = rs.getFloat("labour");
                totalAmount = calculateTotal();
                labourCharge.setText(String.format("%.2f",labour));
                totalAmountLabel.setText("Rs. : " + String.format("%.2f", totalAmount ) + " + " +labourCharge.getText() + " (labour&transport)");
                panel5.setVisible(true);

                } catch (HeadlessException | SQLException e1) {
                    JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }catch(HeadlessException | ClassNotFoundException | SQLException e){
                System.out.println(e+"At displayItem");
                JOptionPane.showMessageDialog(null, "Empty columns", "Error", JOptionPane.WARNING_MESSAGE);
            }
            
            
    }
    
    public double calculateTotal(){
        double sum = 0;
        for(int i = 0; i < this.dataTable.getRowCount(); i++)
        {
            sum = sum + Double.parseDouble(dataTable.getValueAt(i, 3).toString());
        }
        return sum;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == selectClient){
            OperationsOnselectedClient();
        }
        if(e.getSource() == removeItem){
            removeItem();
            displayItems();
        }
        if(e.getSource() == addNewItem){
            panel6.setVisible(true);
            createComboBox();
        }
        if(e.getSource() == addItem){
            AddNewItem();
        }
        if(e.getSource() == UpdateModified){
            UpdateModify();
        }
        if(e.getSource() == modifyItem){
            modifyItem();
        }
    }
    
    public void removeItem(){    
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

                        PreparedStatement pstm2;
                        PreparedStatement pstm3;
                        PreparedStatement pstm4;
                        PreparedStatement pstm5;

                        String q1 = "update items set Available=Available+? where Item_id=?";
                        PreparedStatement pstm1 = con.prepareStatement(q1);
                        pstm1.setInt(1, Integer.parseInt(rowData[2].toString()));
                        System.out.println(rowData[2].toString());
                        pstm1.setInt(2, Integer.parseInt(rowData[0].toString()));


                        String q2 = "update price_info set total_items_Amount = total_items_Amount-?,grand_total = total_items_Amount+labour where client_id = ?";
                        pstm2 = con.prepareStatement(q2);
                        pstm2.setDouble(1, Double.parseDouble(rowData[3].toString()));
                        pstm2.setInt(2, client_id);



                        String q3 = "delete from item_booked where item_id=?";
                        pstm3 = con.prepareStatement(q3);
                        pstm3.setInt(1, Integer.parseInt(rowData[0].toString()));


                        String q4 = "update client_details set Amount_payable = Amount_payable-?,Amount_due = Amount_payable-Amount_paid where client_id=?";
                        pstm4 = con.prepareStatement(q4);
                        pstm4.setDouble(1, Double.parseDouble(rowData[3].toString()));
                        
                        String q5 = "delete from allorders where item_id=?";
                        pstm5 = con.prepareStatement(q5);
                        pstm5.setInt(1, Integer.parseInt(rowData[0].toString()));
                        
                        pstm4.setInt(2, client_id);
                        pstm1.executeUpdate();
                        pstm2.executeUpdate();
                        pstm3.executeUpdate();
                        pstm4.executeUpdate();
                        pstm5.executeUpdate();

                        con.close();
                    }catch(ClassNotFoundException | NumberFormatException | SQLException e){
                        JOptionPane.showMessageDialog(null, "OOPS! Please click 'Select' Button after using Box", "Not Found", JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Please select a row", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
            
    }
    
    void UpdateModify(){
        try{  
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            String q1 = "update price_info set labour = ?,grand_total=total_items_Amount+labour where client_id = ?";
            String q2 = "update client_details set Amount_payable=(select grand_total from price_info where client_id = ?),Amount_due = Amount_payable - Amount_paid where client_id=?";
            
            PreparedStatement pstm1 = con.prepareStatement(q1);
            pstm1.setFloat(1,Float.parseFloat(labourCharge.getText()));
            pstm1.setInt(2, client_id);
            
            PreparedStatement pstm2 = con.prepareStatement(q2);
            pstm2.setFloat(1,client_id);
            pstm2.setInt(2, client_id);
            
            pstm1.executeUpdate();
            pstm2.executeUpdate();
            displayItems();
            
        }catch(ClassNotFoundException | NumberFormatException | SQLException e){
            System.out.println(e+"At updateModify");
            JOptionPane.showMessageDialog(null, e, "Not Found( update item)", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    void AddNewItem(){
        String[] values =  displayItemName.getSelectedItem().toString().split("\\(");
        String itemName = values[0].trim();
        int itemId = Integer.parseInt(values[1].substring(0,values[1].length()-2));
        int itemQuantity = 0;
        if(displayItemMap.containsKey(itemName))
            itemQuantity = displayItemMap.get(itemName);
        
        if(itemQuantity < Integer.parseInt(quantity.getText()) ){
            JOptionPane.showMessageDialog(null, "Not enough quantity !", "Error", JOptionPane.INFORMATION_MESSAGE);
        }else
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url,uname,pass);
            String item_booked = "insert into item_booked values(?,?,?,?)";
            String price_info = "update price_info set total_items_Amount=total_items_Amount + (select Amount*(select item_Qnt from item_booked where item_id=?) from items where item_id=?),grand_total=total_items_Amount+labour where client_id=?";
            String reduceAvail = "update items set Available = Available-? where Item_id = ?";
            String client_details = "update client_details set Amount_payable=(select grand_total from price_info where client_id = ?),Amount_due = Amount_payable - Amount_paid where client_id=?";
            String allOrders = "insert into allorders values (?,?,?,?)";
            
            PreparedStatement pstm1 = con.prepareStatement(item_booked);            
            
            pstm1.setInt(1,client_id);
            pstm1.setInt(2, itemId);
            pstm1.setString(3,itemName);
            pstm1.setInt(4,Integer.parseInt(quantity.getText()));            
            
            PreparedStatement pstm2 = con.prepareStatement(price_info);
            pstm2.setInt(1,itemId);
            pstm2.setInt(2,itemId);
            pstm2.setInt(3,client_id);
            
            PreparedStatement pstm3 = con.prepareStatement(reduceAvail);
            pstm3.setInt(1, Integer.parseInt(quantity.getText()));
            pstm3.setString(2,Integer.toString(itemId));
            
            PreparedStatement pstm4 = con.prepareStatement(client_details);
            pstm4.setInt(1,client_id);
            pstm4.setInt(2,client_id);
            
            PreparedStatement pstm5 = con.prepareStatement(allOrders);
            pstm5.setInt(1,client_id);
            pstm5.setInt(2,itemId);
            pstm5.setString(3,itemName);
            pstm5.setInt(4,Integer.parseInt(quantity.getText()));
            
            pstm1.executeUpdate();
            pstm2.executeUpdate();
            pstm3.executeUpdate();
            displayItemMap.put(itemName, displayItemMap.getOrDefault(itemName,0) - Integer.parseInt(quantity.getText()));
            updateDisplayItems();
            pstm4.executeUpdate();
            pstm5.executeUpdate();
            displayItems();
            
        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e+"At addNewItem");
            JOptionPane.showMessageDialog(null, e, "Not Found((add new item))", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    void updateDisplayItems(){
        displayItemName.removeAllItems();
        displayItemMap.entrySet().forEach(entry -> {
            String itemName = entry.getKey();
            int q1 = entry.getValue();
            displayItemName.addItem(itemName + " (" + q1 + ")");
        });
    }
    
    void modifyItem(){
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
                String getAvailability = "select Available from items where item_id = ?";
                PreparedStatement pstm = con.prepareStatement(getAvailability);
                pstm.setInt(1, Integer.parseInt(rowData[0].toString()));
                ResultSet rs = pstm.executeQuery();
                int newQauntity = 0;
                if(rs.next()){
                    int eQuant = rs.getInt("Available");
                    newQauntity = Integer.parseInt(JOptionPane.showInputDialog("Enter new qauntity for "+rowData[1]+" Available : "+eQuant));
                }

                int diff = (newQauntity - Integer.parseInt(rowData[2].toString()));
                
                String updateAllOrders = "update allorders set item_Qnt=? where item_id=?";
                PreparedStatement pstm0 = con.prepareStatement(updateAllOrders);
                pstm0.setInt(1,newQauntity);
                pstm0.setInt(2,Integer.parseInt(rowData[0].toString()));
                        
                
                String updateItems = "update items set Available = Available-? where item_id=?";
                PreparedStatement pstm1 = con.prepareStatement(updateItems);
                pstm1.setInt(1,diff);
                pstm1.setInt(2,Integer.parseInt(rowData[0].toString()));
                
                String updateItemBooked = "update item_booked set item_Qnt=? where item_id=? and client_id=?";
                PreparedStatement pstm2 = con.prepareStatement(updateItemBooked);
                pstm2.setInt(1,newQauntity);
                pstm2.setInt(2, Integer.parseInt(rowData[0].toString()));
                pstm2.setInt(3,client_id);
                
                int total = 0;
                String getItemQnts = "SELECT item_id, item_Qnt FROM item_booked WHERE client_id=?";
                PreparedStatement getQnts = con.prepareStatement(getItemQnts);
                getQnts.setInt(1, client_id); // get Item Id
                ResultSet getQntsRes = getQnts.executeQuery();

                while (getQntsRes.next()) {
                    String getItemAmount = "SELECT Amount * ? AS Total FROM items WHERE item_id=?";
                    PreparedStatement getAmount = con.prepareStatement(getItemAmount);
                    getAmount.setInt(1, getQntsRes.getInt("item_Qnt"));
                    getAmount.setInt(2, getQntsRes.getInt("item_id"));

                    ResultSet getAmountRes = getAmount.executeQuery();
                    if (getAmountRes.next()) {
                        total += getAmountRes.getInt("Total");
                    }

                    // Close the ResultSet and PreparedStatement for the inner query
                    getAmountRes.close();
                    getAmount.close();
                }

                // Close the ResultSet and PreparedStatement for the outer query
                getQntsRes.close();
                getQnts.close();

                
                String updatePriceInfo = "update price_info set total_items_Amount=?,grand_total= total_items_Amount+labour where client_id=?";
                PreparedStatement pstm3 = con.prepareStatement(updatePriceInfo);
                
                pstm3.setInt(1,total);
                pstm3.setInt(2,client_id);
                
                String UpdateClientDetails = "update client_details set Amount_payable=(select grand_total from price_info where client_id = ?),Amount_due = Amount_payable - Amount_paid where client_id=?";
                PreparedStatement pstm4 = con.prepareStatement(UpdateClientDetails);
                pstm4.setInt(1,client_id);
                pstm4.setInt(2,client_id);
                
                
                pstm0.executeUpdate();
                pstm1.executeUpdate();
                pstm2.executeUpdate();
                pstm3.executeUpdate();
                pstm4.executeUpdate();
                displayItems();
                
                
            }catch(ClassNotFoundException | NumberFormatException | SQLException e){
                System.out.println(e+"At modifyItem");
                JOptionPane.showMessageDialog(null, e, "Not Found(modify item)", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    void createComboBox() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DriverManager.getConnection(url, uname, pass)) {
            String q = "SELECT item_id, item_name, Available FROM items WHERE item_id NOT IN (SELECT item_id FROM item_booked WHERE client_id = ?)";
            PreparedStatement pstm1 = con.prepareStatement(q);
            pstm1.setInt(1, client_id);
            ResultSet rs = pstm1.executeQuery();

            // Clear existing items in combo box and map
            displayItemName.removeAllItems();
            displayItemMap.clear();

            // Process the result set and populate the map and combo box
            while (rs.next()) {
                String itemName = rs.getString("item_name").trim();  // Use trim to avoid whitespace issues
                int itemId = rs.getInt("item_id");
                int itemAvail = rs.getInt("Available");
                
                displayItemName.addItem(itemName + " (" + itemId + ") (" + itemAvail + ")");
                displayItemMap.put(itemName, itemAvail);  // Store the item name and its available quantity in the map
            }

            // Close resources
            pstm1.close();
        }
    } catch (ClassNotFoundException | SQLException e) {
        System.out.println(e + " At createComboBox");
        JOptionPane.showMessageDialog(null, e, "Not Found (createComboBox)", JOptionPane.INFORMATION_MESSAGE);
    }
}

}
