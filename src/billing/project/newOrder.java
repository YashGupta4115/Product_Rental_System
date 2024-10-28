package billing.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class newOrder extends JFrame implements ActionListener {
    
    JFrame frame = new JFrame();
    float grandTotal = 0;
    
    String addName;
    String url = Main.url;
    String uname = Main.uname;
    String pass = Main.pass;

    JPanel topPanel;
    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JPanel panel4;
    JPanel panel5;
    JPanel bottomPanel;

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
    
    int order_id = 3001;
    
    public int getOrderId(){       
        try(
            Connection conn = DriverManager.getConnection(url,uname,pass);
            Statement stmt = conn.createStatement()
        ){
            String getPrevOrderId = "select MAX(order_id) from orders";
            ResultSet rs = stmt.executeQuery(getPrevOrderId);
            
            if(rs.next()){
                System.out.println(rs.getInt(1));
                order_id = rs.getInt(1) + 1;
                System.out.println(order_id);
            }
            
            return order_id;
        }catch(Exception e){
            System.out.println("Exception caught at 61 ( new Orders )  : "+e);
        }
        
        return order_id;
    }
    
    public JButton createStyledButton(String text){
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150,40));
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                button.setBackground(Color.GRAY);
                button.setForeground(Color.BLACK);
            }
            
            @Override
            public void mouseExited(MouseEvent e){
                button.setBackground(Color.BLACK);
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    public void openNewOrder() {
        
        setExtendedState(this.MAXIMIZED_BOTH);
        this.setLayout(new BorderLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        order_id = getOrderId();
        itemQuantityMap = new HashMap<>();
      
        panel1 = new JPanel( new BorderLayout()  );
        panel2 = new JPanel( new GridBagLayout()  );
        panel3 = new JPanel( new BorderLayout()  );
        panel4 = new JPanel( new GridBagLayout() );
        panel5 = new JPanel( new BorderLayout()  );
        topPanel = new JPanel( new BorderLayout() ); //panel 1 and panel 2;
        bottomPanel = new JPanel( new BorderLayout() ); //panel 4 and panel 5;
        
        //panel 1
        JLabel titleLabel = new JLabel("BILLING SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Optional: set a larger font for the title
        panel1.add(titleLabel, BorderLayout.CENTER);     
        panel1.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
          
        //panel 2
        itemList = new JComboBox<>();
        itemList.setPreferredSize(new Dimension(250,40));
        itemList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        itemList.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                list.setFixedCellHeight(25);  // Set the fixed height for each cell
                return c;
            }
        });
        
        
        addButton = createStyledButton("ADD ITEM");
        
        removeButton = createStyledButton("REMOVE ITEM");
              
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
        
        gbc.insets = new Insets(0, 10, 0, 10); // Adds padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components stretch horizontally
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel2.add(itemList, gbc);
        
        gbc.gridx = 1;
        panel2.add(new JLabel("Quantity: "), gbc);
        
        gbc.gridx = 2;
        panel2.add(addQuantity, gbc);
        
        gbc.gridx = 3;
        panel2.add(addButton, gbc);
        
        gbc.gridx = 4;
        panel2.add(removeButton, gbc);    
        panel2.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
 
        topPanel.add(panel1, BorderLayout.NORTH);
        topPanel.add(panel2, BorderLayout.SOUTH);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        this.add(topPanel, BorderLayout.NORTH);

        //panel 3
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Item");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Amount");
        tableModel.addColumn("Total Amount");
        
        dataTable = new JTable(tableModel);
        
        dataTable.setRowHeight(30);
        dataTable.setFont(new Font("MS Mincho", Font.PLAIN, 18));
        
        TableCellRenderer customeCellRender = (JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) -> {
            JLabel cell = new JLabel(value.toString());
            cell.setFont(new Font("MS Mincho", Font.ITALIC, 16));
            cell.setOpaque(true);
            cell.setBackground(row%2 == 0 ? new Color(45,45,45) : new Color(55,55,55));
            cell.setForeground(Color.WHITE);
            return cell;
        };
        
        for(int i = 0 ; i < dataTable.getColumnCount() ; i++){
            dataTable.getColumnModel().getColumn(i).setCellRenderer(customeCellRender);
        }
        
        JTableHeader header = dataTable.getTableHeader();
        header.setFont( new Font("SansSerif", Font.BOLD, 18) );
        header.setBackground(new Color(60, 60, 60));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        panel3.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        panel3.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
         
        this.add(panel3, BorderLayout.CENTER);
        
        // Setting column widths
        TableColumnModel columnModel = dataTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(550);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(500);
        
        //panel 4
        externalCosts = new JTextField();
        externalCosts.setPreferredSize(new Dimension(150, 40));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel4.add(new JLabel("Extra Costs (Human capital + Delivery):"), gbc);
        
        gbc.gridx = 1;
        panel4.add(externalCosts, gbc);
        
        finalSubmit = createStyledButton("Confirm Order");
        finalSubmit.addActionListener(this);
        
        gbc.gridx = 2;
        panel4.add(finalSubmit, gbc); 
        
        totalAmountLabel = new JLabel("Total Amount: Rs. 0.00");
        
        gbc.gridx = 3;
        panel4.add(totalAmountLabel, gbc);
        panel4.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        bottomPanel.add(panel4, BorderLayout.NORTH);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        
        //panel 5
        newOrder = createStyledButton("New Order");
      
        newOrder.addActionListener(this);
        panel5.add(newOrder, BorderLayout.SOUTH);
        panel5.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        bottomPanel.add(panel5, BorderLayout.SOUTH);
        
        this.add(bottomPanel, BorderLayout.SOUTH);        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
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
                System.out.println("Exception at newOrder(220)");
                JOptionPane.showMessageDialog(null, "Not enough Quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            System.out.println("Exception at newOrder(224)");
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
            System.out.println("Exception at newOrder(253)");
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
            System.out.println("Exception at newOrder(275)");
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
            System.out.println(order_id + "at 305(newOrder)");
            Payment py = new Payment(order_id, Double.parseDouble(externalCosts.getText()),totalAmountWithExtras , client_id);
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
        System.out.println("Exception at newOrder(315)");
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
            String query = "SELECT client_id FROM client ORDER BY client_id DESC LIMIT 1;";
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                newClientId = rs.getInt("client_id") + 1;  // Increment the last ID by 1
            }
        }
    } catch (ClassNotFoundException | SQLException e) {
        System.out.println("Exception at newOrder(335)");
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
            
            String items_booked = "insert into items_booked values(?,?,?,?)";
            String reduceAvail = "update items set Available = Available-? where item_name = ?";
            String retriveItemId = "Select Item_id from items where item_name = ?";
            
            ResultSet rs2;
            PreparedStatement pstm2 = con.prepareStatement(reduceAvail);
            PreparedStatement pstm = con.prepareStatement(items_booked);
            PreparedStatement pstm3 = con.prepareStatement(retriveItemId);
            
            for(int i = 0; i < this.dataTable.getRowCount(); i++)
            {
                pstm3.setString(1,this.dataTable.getValueAt(i,0).toString());
                rs2 = pstm3.executeQuery();
                rs2.next();
                pstm.setInt(1,order_id);   
                pstm.setInt(2,client_id);
                pstm.setInt(3,Integer.parseInt(rs2.getString("Item_id")));
                pstm.setInt(4,Integer.parseInt(this.dataTable.getValueAt(i, 1).toString()));
                
                pstm2.setInt(1,Integer.parseInt(this.dataTable.getValueAt(i, 1).toString()));
                pstm2.setString(2,this.dataTable.getValueAt(i,0).toString());
                pstm.executeUpdate();
                pstm2.executeUpdate();
            }           
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Exception at newOrder(373)");
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
