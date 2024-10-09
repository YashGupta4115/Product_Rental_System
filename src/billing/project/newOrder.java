package billing.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.*;

public class newOrder implements ActionListener {
    
    JFrame frame = new JFrame();
    float grandTotal = 0;
    
    String addName;
    String url = Main.url;
    String uname = Main.uname;
    String pass = Main.pass;

    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JPanel panel4;
    JPanel panel5;

    JTextField addQuantity;
    JTextField externalCosts;
    
    JComboBox<String> itemList;
    
    JButton confirmOrder;
    JButton addButton;
    JButton removeButton;
    JButton finalSubmit;
    JButton newOrder;

    JLabel totalAmountLabel;

    DefaultTableModel tableModel;
    JTable dataTable;
    
    HashMap<String, Integer> itemQuantityMap;

    public void openNewOrder() {
        itemQuantityMap = new HashMap<>();

        panel1 = new JPanel();
        panel1.setBounds(150, 0, 800, 50);
        panel1.add(new JLabel("BILLING SYSTEM"));

        panel3 = new JPanel();
        panel3.setBounds(150, 250, 800, 200);
       
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Item");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Amount");
        tableModel.addColumn("Total Amount");

        dataTable = new JTable(tableModel);
        panel3.add(new JScrollPane(dataTable));

        // Setting column widths
        TableColumnModel columnModel = dataTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(550);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(500);
        
        panel4 = new JPanel();
        panel4.setBounds(150, 450, 800, 60);
        
        externalCosts = new JTextField();
        externalCosts.setPreferredSize(new Dimension(150, 20));
        panel4.add(new JLabel("Extra Costs (Labour + Delivery):"));
        panel4.add(externalCosts);
        
        finalSubmit = new JButton("Confirm Order");
        finalSubmit.setBackground(Color.LIGHT_GRAY);
        finalSubmit.setPreferredSize(new Dimension(150, 40));
        finalSubmit.addActionListener(this);
        panel4.add(finalSubmit);
        
        totalAmountLabel = new JLabel("Total Amount: Rs. 0.00");
        panel4.add(totalAmountLabel);
        
        newOrder = new JButton("New Order");
        newOrder.setBackground(Color.LIGHT_GRAY);
        newOrder.setPreferredSize(new Dimension(150, 40));
        newOrder.addActionListener(this);
        panel5 = new JPanel();
        panel5.setBounds(150, 510, 800, 50);
        panel5.add(newOrder);
        
        frame.add(panel1);
        addItems(); // This will create panel2
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(1150, 1000);
        frame.setVisible(true);
    }
    
    public final void addItems() {
        panel2 = new JPanel();
        panel2.setBounds(150, 100, 800, 100);

        itemList = new JComboBox<>();
        addButton = new JButton("ADD ITEM");
        addButton.setBackground(Color.LIGHT_GRAY);
        addButton.setPreferredSize(new Dimension(150, 40));
        removeButton = new JButton("REMOVE ITEM");
        removeButton.setBackground(Color.LIGHT_GRAY);
        removeButton.setPreferredSize(new Dimension(150, 40));
        
        // Remove button action
        removeButton.addActionListener((ActionEvent e) -> {
            int selectedRow = dataTable.getSelectedRow();
            if (selectedRow != -1) {
                String itemName = (String) dataTable.getValueAt(selectedRow, 0);
                int quantityRemoved = (int) dataTable.getValueAt(selectedRow, 1);
                
                // Update the local quantity
                itemQuantityMap.put(itemName, itemQuantityMap.get(itemName) + quantityRemoved);
                
                // Remove the row from the table
                tableModel.removeRow(selectedRow);
                
                // Update the combo box
                updateComboBox();
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to remove.");
            }
        });
        
        addQuantity = new JTextField();
        addQuantity.setPreferredSize(new Dimension(150, 40));
        addButton.addActionListener(this);

        createComboBox();
        
        panel2.add(itemList);
        panel2.add(new JLabel("Quantity: "));
        panel2.add(addQuantity);
        panel2.add(addButton);
        panel2.add(removeButton);
        frame.add(panel2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            if (validateInput()) {
                displayList();
            }
        }
        if (e.getSource() == finalSubmit) {
            handleFinalSubmit();
        }
        if (e.getSource() == newOrder) {
            frame.dispose();
            newOrder newOrder = new newOrder();
            newOrder.openNewOrder();
        }
    }

    public void displayList() {
        String name = (String) itemList.getSelectedItem();
        int index = name.indexOf("(");
        String itemName = name.substring(0, index).trim();
        int availableQuantity = itemQuantityMap.get(itemName);  // Get available quantity from the map

        try {
            int quantityToAdd = Integer.parseInt(addQuantity.getText());
            if (availableQuantity >= quantityToAdd) {
                // Simulate getting the price from the database
                double price = getItemPrice(itemName);
                double totalAmount = price * quantityToAdd;
                
                // Add the item to the table
                tableModel.addRow(new Object[]{itemName, quantityToAdd, price, totalAmount});
                
                // Decrease the available quantity in the local map
                itemQuantityMap.put(itemName, availableQuantity - quantityToAdd);
                
                // Update the total amount label
                double grandTotal = calculateTotal() + Double.parseDouble(externalCosts.getText());
                totalAmountLabel.setText("Total Amount: Rs. " + String.format("%.2f", grandTotal));
                
                // Update the combo box
                updateComboBox();
            } else {
                JOptionPane.showMessageDialog(null, "Not enough Quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid quantity", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double getItemPrice(String itemName) {
        double price = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, uname, pass);
                 PreparedStatement pstmt = con.prepareStatement("SELECT Amount FROM items WHERE item_name = ?")) {
                pstmt.setString(1, itemName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    price = rs.getDouble("Amount");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error fetching price: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return price;
    }

    private boolean validateInput() {
        try {
            int quantity = Integer.parseInt(addQuantity.getText());
            double externalCost = Double.parseDouble(externalCosts.getText());
            return quantity > 0 && externalCost >= 0;  // Ensure quantity is positive and cost is non-negative
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers for quantity and costs.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void createComboBox() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, uname, pass);
                 Statement st = con.createStatement()) {
                String query = "SELECT item_id, item_name, Available FROM items";
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    int availableQuantity = rs.getInt("Available");
                    itemQuantityMap.put(itemName, availableQuantity);
                    itemList.addItem(itemName + "(" + availableQuantity + ")");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateComboBox() {
        // Clear the current combo box items and repopulate it with updated quantities
        itemList.removeAllItems();
        itemQuantityMap.entrySet().forEach(entry -> {
            String itemName = entry.getKey();
            int quantity = entry.getValue();
            itemList.addItem(itemName + " (" + quantity + ")");
        });
    }

    private void handleFinalSubmit() {
    try {
        // Parse external costs and calculate total amount
        double externalCost = Double.parseDouble(externalCosts.getText());
        double totalAmountWithExtras = calculateTotal() + externalCost;
        totalAmountLabel.setText("Total Amount: Rs. " + String.format("%.2f", totalAmountWithExtras));

        // Confirm payment with user
        int option = JOptionPane.showConfirmDialog(null, "Proceed to Payment?", "Payment Gateway", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            // Retrieve or generate a new client ID
            int client_id = getNewClientId();  // Call the method to get the client ID

            // Create a Payment object and check payment status
            Payment py = new Payment(totalAmountWithExtras, client_id);
            boolean status = py.isPaymentCompleted();
            if (status) {
                insertDBMS(client_id);  // If payment is successful, update the database
            } else {
                JOptionPane.showMessageDialog(null, "Payment Cancelled.. Booking not done", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Payment Cancelled", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Please Enter Labour + Transport cost!", "INVALID", JOptionPane.ERROR_MESSAGE);
    }
}

// Method to get a new client ID
private int getNewClientId() {
    int newClientId = 2001;  // Default ID
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DriverManager.getConnection(url, uname, pass);
             Statement st = con.createStatement()) {
            // Retrieve the last client ID from the client_details table
            String query = "SELECT client_id FROM client_details ORDER BY client_id DESC LIMIT 1;";
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                newClientId = rs.getInt("client_id") + 1;  // Increment the last ID by 1
            }
        }
    } catch (ClassNotFoundException | SQLException e) {
        JOptionPane.showMessageDialog(null, "Error fetching client ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return newClientId;  // Return the new client ID
}


    public void insertDBMS(int client_id){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            Statement st = con.createStatement();
            
//            String retriveId = "SELECT * FROM client_details ORDER BY client_id DESC LIMIT 1;";
            String item_booked = "insert into item_booked values(?,?,?,?)";
            String allOrders = "insert into allOrders values(?,?,?,?)";
            String price_info = "insert into price_info values(?,?,?,?)";
            String reduceAvail = "update items set Available = Available-? where item_name = ?";
            String retriveItemId = "Select Item_id from items where item_name = ?";
            
            ResultSet rs2;
            PreparedStatement pstm2 = con.prepareStatement(reduceAvail);
            PreparedStatement pstm = con.prepareStatement(item_booked);
            PreparedStatement pstmall = con.prepareStatement(allOrders);
            PreparedStatement pstm3 = con.prepareStatement(retriveItemId);
            for(int i = 0; i < this.dataTable.getRowCount(); i++)
            {
                
                pstm3.setString(1,this.dataTable.getValueAt(i,0).toString());
                rs2 = pstm3.executeQuery();
                rs2.next();
                pstm.setInt(1,client_id);             
                pstm.setInt(2,Integer.parseInt(rs2.getString("Item_id")));
                pstm.setString(3,this.dataTable.getValueAt(i,0).toString());
                pstm.setInt(4,Integer.parseInt(this.dataTable.getValueAt(i, 1).toString()));
                
                pstmall.setInt(1,client_id);             
                pstmall.setInt(2,Integer.parseInt(rs2.getString("Item_id")));
                pstmall.setString(3,this.dataTable.getValueAt(i,0).toString());
                pstmall.setInt(4,Integer.parseInt(this.dataTable.getValueAt(i, 1).toString()));
                
                pstm2.setInt(1,Integer.parseInt(this.dataTable.getValueAt(i, 1).toString()));
                pstm2.setString(2,this.dataTable.getValueAt(i,0).toString());
                pstm.executeUpdate();
                pstmall.executeUpdate();
                pstm2.executeUpdate();
            }
            
            pstm = con.prepareStatement(price_info);
            
                pstm.setInt(1,client_id);        
                double total = calculateTotal();
                pstm.setDouble(2, total);
                pstm.setDouble(3,Double.parseDouble(externalCosts.getText()));
                pstm.setDouble(4,total + Double.parseDouble(externalCosts.getText()));
                pstm.executeUpdate();
            
            
            
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }
    
    public double calculateTotal() {
        double sum = 0;
        for (int i = 0; i < dataTable.getRowCount(); i++) {
            sum += Double.parseDouble(dataTable.getValueAt(i, 3).toString());
        }
        return sum;
    }

    public static void main(String[] args) {
        new newOrder().openNewOrder();
    }
}
