
package billing.project;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    int order_id;
    
    void clientNameDisplay(){
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            Statement st = con.createStatement();
            
            String q = "select o.order_id, c.name as client_name, o.event_Date as Event_date from client c join orders o on c.client_id = o.client_id where o.event_date > curdate();";
            ResultSet rs = st.executeQuery(q);
            while(rs.next()){
                displayClientName.addItem(rs.getString("client_name")+"("+rs.getString("Event_date")+")"+"("+rs.getString("order_id")+")");
            }
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Exception at modify(178)"+e);
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
            
            PreparedStatement pstm;
            String Name = (String) displayClientName.getSelectedItem();
            try{
                String[] result = Name.split("\\(");
                order_id = Integer.parseInt(result[2].substring(0,result[2].length()-1));

                String getClient_id  = "select client_id from orders where order_id = ?";
                pstm = con.prepareStatement(getClient_id);
                pstm.setInt(1,order_id);
                ResultSet rs = pstm.executeQuery();
                
                if(rs.next()){
                    client_id = rs.getInt(1);
                    System.out.println("client_id"+client_id);
                } else{
                    System.out.println("client_id not exists");
                    return;
                }
                
                String q1 = "select item_id, item_Qnt from items_booked where order_id=?";
                String q2 = "select item_name, Amount from items where item_id = ?";
                String q3 = "select transport_and_human_capital_cost from orders where order_id = ?";
                pstm = con.prepareStatement(q1);
                pstm.setInt(1,order_id);
                rs = pstm.executeQuery();
                
                while(rs.next()){
                    PreparedStatement pstm2 = con.prepareStatement(q2);
                    pstm2.setInt(1,rs.getInt("item_id"));
                    ResultSet rs2 =  pstm2.executeQuery();
                    rs2.next();
                    double amt = rs.getInt("item_Qnt") * rs2.getFloat("Amount");
                    tableModel.addRow(new Object[]{rs.getInt("item_id"), rs2.getString("item_name"), rs.getInt("item_Qnt"), amt });
                }
                pstm = con.prepareStatement(q3);
                pstm.setInt(1,order_id);
                rs = pstm.executeQuery();
                rs.next();
                double labour = rs.getFloat("transport_and_human_capital_cost");
                totalAmount = calculateTotal();
                labourCharge.setText(String.format("%.2f",labour));
                totalAmountLabel.setText("Rs. : " + String.format("%.2f", totalAmount ) + " + " +labourCharge.getText() + " (Transport & Human Capital Cost : )");
                panel5.setVisible(true);

                } catch (HeadlessException | SQLException e1) {
                    System.out.println("Exception at modify(233)"+e1);
                    JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }catch(HeadlessException | ClassNotFoundException | SQLException e){
                System.out.println("Exception at modify(237)"+e);
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
            try {
                removeItem();
            } catch (SQLException ex) {
                Logger.getLogger(modifyExistingOrder.class.getName()).log(Level.SEVERE, null, ex);
            }
            displayItems();
        }
        if(e.getSource() == addNewItem){
            panel6.setVisible(true);
            createComboBox();
        }
        if(e.getSource() == addItem){
            try {
                AddNewItem();
            } catch (SQLException ex) {
                Logger.getLogger(modifyExistingOrder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(e.getSource() == UpdateModified){
            try {
                UpdateModify();
            } catch (SQLException ex) {
                Logger.getLogger(modifyExistingOrder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(e.getSource() == modifyItem){
            try {
                modifyItem();
            } catch (SQLException ex) {
                Logger.getLogger(modifyExistingOrder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void removeItem() throws SQLException{    
            int selectedRowIndex = dataTable.getSelectedRow();
            Savepoint initializeRemoveItem = null;
            
                if(selectedRowIndex!=-1){
                    Object[] rowData = new Object[tableModel.getColumnCount()];


                    for(int i=0;i<tableModel.getColumnCount();i++){
                        rowData[i] = tableModel.getValueAt(selectedRowIndex,i);
                    }
                    
                    Connection con = null;
                    try{  
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        con = DriverManager.getConnection(url,uname,pass);
                        con.setAutoCommit(false); // start transaction
                        initializeRemoveItem = con.setSavepoint("initializeRemoveItem");

                        PreparedStatement pstm2;
                        PreparedStatement pstm3;

                        String q1 = "update items set Available=Available+? where Item_id=?";
                        PreparedStatement pstm1 = con.prepareStatement(q1);
                        pstm1.setInt(1, Integer.parseInt(rowData[2].toString()));
//                        System.out.println(rowData[2].toString());
                        pstm1.setInt(2, Integer.parseInt(rowData[0].toString()));


                        String q2 = "update orders set items_amt = items_amt-? where order_id = ?";
                        pstm2 = con.prepareStatement(q2);
                        pstm2.setDouble(1, Double.parseDouble(rowData[3].toString()));
                        pstm2.setInt(2, order_id);

                        String q3 = "delete from items_booked where item_id=?";
                        pstm3 = con.prepareStatement(q3);
                        pstm3.setInt(1, Integer.parseInt(rowData[0].toString()));
                        
                        pstm1.executeUpdate();
                        pstm2.executeUpdate();
                        pstm3.executeUpdate();
                        con.commit(); //commit transaction
                        con.close();
                    }catch(ClassNotFoundException | NumberFormatException | SQLException e){
                        System.out.println(e+"at 320(modify) + rolled back");
                        if(initializeRemoveItem != null)
                            con.rollback(initializeRemoveItem);
                        JOptionPane.showMessageDialog(null, "OOPS! Please click 'Select' Button after using Box", "Not Found", JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Please select a row", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
            
    }
    
    void UpdateModify() throws SQLException{
        Connection con = null;
        Savepoint initializeUpdateModify = null;
        try{  
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url,uname,pass);
            con.setAutoCommit(false);
            initializeUpdateModify = con.setSavepoint("initializeUpdateModify");
            String q1 = "update orders set transport_and_human_capital_cost = ? where order_id = ?";
            
            PreparedStatement pstm1 = con.prepareStatement(q1);
            pstm1.setFloat(1,Float.parseFloat(labourCharge.getText()));
            pstm1.setInt(2, order_id);
            
            
            pstm1.executeUpdate();
            con.commit(); //commit
            displayItems();
        }catch(ClassNotFoundException | NumberFormatException | SQLException e){
            System.out.println(e+"At updateModify");
            if(initializeUpdateModify != null )
                con.rollback(initializeUpdateModify);
            JOptionPane.showMessageDialog(null, e, "Not Found( update item)", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    void AddNewItem() throws SQLException{
        String[] values =  displayItemName.getSelectedItem().toString().split("\\(");
        String itemName = values[0].trim();
        int itemId = Integer.parseInt(values[1].substring(0,values[1].length()-2));
        int itemQuantity = 0;
        if(displayItemMap.containsKey(itemName))
            itemQuantity = displayItemMap.get(itemName);
        Connection con = null;
        Savepoint initializeAddNewItem = null;
        if(itemQuantity < Integer.parseInt(quantity.getText()) ){
            JOptionPane.showMessageDialog(null, "Not enough quantity !", "Error", JOptionPane.INFORMATION_MESSAGE);
        }else
        try{
            String item_name = "";
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url,uname,pass);
            
            con.setAutoCommit(false);
            con.setSavepoint("initializeAddNewItem");
            
            
            String orders = "update orders set items_Amt = items_Amt + (select Amount*? from items where item_id=?) where order_id=?";
            String item_booked = "insert into items_booked values(?,?,?,?)";
            String reduceAvail = "update items set Available = Available-? where Item_id = ?";
            String getItemName = "Select item_name, amount from items where item_id = ?";
             
            PreparedStatement pstm1 = con.prepareStatement(item_booked);            
            
            pstm1.setInt(1,order_id);
            pstm1.setInt(2, client_id);
            pstm1.setInt(3,itemId);
            pstm1.setInt(4,Integer.parseInt(quantity.getText()));            
            
            PreparedStatement pstm2 = con.prepareStatement(orders);
            pstm2.setInt(1,Integer.parseInt(quantity.getText()));
            pstm2.setInt(2,itemId);
            pstm2.setInt(3,order_id);
            
            PreparedStatement pstm3 = con.prepareStatement(reduceAvail);
            pstm3.setInt(1, Integer.parseInt(quantity.getText()));
            pstm3.setString(2,Integer.toString(itemId));
            
            PreparedStatement getItemNamepm = con.prepareStatement(getItemName);
            getItemNamepm.setInt(1, itemId);
            ResultSet rs = getItemNamepm.executeQuery();
            int item_amt = 0;

            if(rs.next()) {
                item_name = rs.getString("item_name");   // Fetch the item name
                item_amt = rs.getInt("amount");          // Fetch the item amount
            } else {
                System.out.println("Item with ID " + itemId + " not found.");
            }

            // Close the resources to prevent memory leaks
            rs.close();
            getItemNamepm.close();

            
            pstm2.executeUpdate();
            pstm1.executeUpdate();
            pstm3.executeUpdate();
            displayItemMap.put(itemName, displayItemMap.getOrDefault(itemName,0) - Integer.parseInt(quantity.getText()));
            updateDisplayItems();
            displayItems();
            
            con.commit();
            tableModel.addRow(new Object[]{itemId, item_name , Integer.parseInt(quantity.getText()) , item_amt });
            UpdateModify();
            
        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e+"At addNewItem");
            if(initializeAddNewItem != null)
                con.rollback(initializeAddNewItem);
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
    
    void modifyItem() throws SQLException {
    int selectedRowIndex = dataTable.getSelectedRow();

    if (selectedRowIndex != -1) {
        Object[] rowData = new Object[tableModel.getColumnCount()];

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowData[i] = tableModel.getValueAt(selectedRowIndex, i);
        }

        Connection con = null;
        Savepoint initiateModify = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, uname, pass);
            con.setAutoCommit(false);
            initiateModify = con.setSavepoint("initiateModify");

            String getAvailability = "SELECT Available FROM items WHERE item_id = ?";
            try (PreparedStatement pstm = con.prepareStatement(getAvailability)) {
                pstm.setInt(1, Integer.parseInt(rowData[0].toString()));
                try (ResultSet rs = pstm.executeQuery()) {
                    int newQuantity = 0;
                    if (rs.next()) {
                        int eQuant = rs.getInt("Available");
                        newQuantity = Integer.parseInt(JOptionPane.showInputDialog("Enter new quantity for " + rowData[1] + " Available : " + eQuant));
                    }
                    int diff = newQuantity - Integer.parseInt(rowData[2].toString());

                    // Update items availability
                    String updateItems = "UPDATE items SET Available = Available - ? WHERE item_id = ?";
                    try (PreparedStatement pstm1 = con.prepareStatement(updateItems)) {
                        pstm1.setInt(1, diff);
                        pstm1.setInt(2, Integer.parseInt(rowData[0].toString()));
                        pstm1.executeUpdate();
                    }

                    // Update items booked table
                    String updateItemBooked = "UPDATE items_booked SET item_qnt = ? WHERE item_id = ? AND order_id = ?";
                    try (PreparedStatement pstm2 = con.prepareStatement(updateItemBooked)) {
                        pstm2.setInt(1, newQuantity);
                        pstm2.setInt(2, Integer.parseInt(rowData[0].toString()));
                        pstm2.setInt(3, order_id);
                        pstm2.executeUpdate();
                    }

                    // Calculate the total price for updated order
                    int total = 0;
                    String getItemQnts = "SELECT item_id, item_qnt FROM items_booked WHERE order_id = ?";
                    try (PreparedStatement getQnts = con.prepareStatement(getItemQnts)) {
                        getQnts.setInt(1, order_id);
                        try (ResultSet getQntsRes = getQnts.executeQuery()) {
                            while (getQntsRes.next()) {
                                String getItemAmount = "SELECT amount * ? AS Total FROM items WHERE item_id = ?";
                                try (PreparedStatement getAmount = con.prepareStatement(getItemAmount)) {
                                    getAmount.setInt(1, getQntsRes.getInt("item_qnt"));
                                    getAmount.setInt(2, getQntsRes.getInt("item_id"));
                                    try (ResultSet getAmountRes = getAmount.executeQuery()) {
                                        if (getAmountRes.next()) {
                                            total += getAmountRes.getInt("Total");
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Update the orders table with the new total amount
                    String updatePriceInfo = "UPDATE orders SET items_amt = ? WHERE order_id = ?";
                    try (PreparedStatement pstm3 = con.prepareStatement(updatePriceInfo)) {
                        pstm3.setInt(1, total);
                        pstm3.setInt(2, order_id);
                        pstm3.executeUpdate();
                    }
                }
            }
            con.commit();
            displayItems();
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
            System.out.println(e + " at modifyItem");
            if (con != null && initiateModify != null) {
                con.rollback(initiateModify);
            }
            JOptionPane.showMessageDialog(null, e, "Not Found(modify item)", JOptionPane.INFORMATION_MESSAGE);
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
}

    void createComboBox() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DriverManager.getConnection(url, uname, pass)) {
            String q = "SELECT item_id, item_name, Available FROM items WHERE item_id NOT IN (SELECT item_id FROM items_booked WHERE order_id = ?)";
            PreparedStatement pstm1 = con.prepareStatement(q);
            pstm1.setInt(1, order_id);
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
