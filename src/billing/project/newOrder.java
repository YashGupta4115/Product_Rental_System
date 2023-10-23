package billing.project;




import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class newOrder implements ActionListener {
    
    
    JFrame frame = new JFrame();
    
    float GrandTotal =0 ;
   
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
    
    JComboBox itemList;
    
    JButton confirmOrder;
    JButton addButton;
    JButton removeButton;
    JButton finalSubmit;
    JButton NewOrder;
    
    
    JLabel totalAmountLabel;
    
    DefaultTableModel tableModel;
    JTable dataTable;
    TableColumnModel columnModel;

    public void openNewOrder() {

        panel1 = new JPanel();
        panel1.setBounds(150, 0, 800, 50);
        
        panel1.add(new JLabel("BILLING SYSTEM"));

        panel3 = new JPanel();
        panel3.setBounds(150, 250, 800, 200);
       
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Item");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Amount");
        tableModel.addColumn("Total_Amount");

        this.dataTable = new JTable(tableModel);
        

        panel3.add(new JScrollPane(this.dataTable));
        columnModel = this.dataTable.getColumnModel();

        columnModel.getColumn(0).setPreferredWidth(550);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(500);
        
        JLabel totalLabel = new JLabel("Total Amount:");
        totalAmountLabel = new JLabel("Rs: 0.00");
        
        panel4 = new JPanel();
        panel4.setBounds(150, 450, 800, 60);
        
        externalCosts = new JTextField();
        externalCosts.setPreferredSize(new Dimension(150, 20));
        panel4.add(new JLabel("ExtraCosts(Labour+Delivery)"));
        panel4.add(externalCosts);
        finalSubmit = new JButton("ConfirmOrder");
        finalSubmit.setBackground(Color.LIGHT_GRAY);
        finalSubmit.setPreferredSize(new Dimension(150, 40));
        finalSubmit.addActionListener(this);
        panel4.add(finalSubmit);
        panel4.add(totalAmountLabel);
        panel4.add(finalSubmit);
        
        NewOrder = new JButton("New Order");
        NewOrder.setBackground(Color.LIGHT_GRAY);
        NewOrder.setPreferredSize(new Dimension(150, 40));
        NewOrder.addActionListener(this);
        panel5 = new JPanel();
        panel5.setBounds(150, 510, 800, 50);
        
        panel5.add(NewOrder);
        
        frame.add(panel1);
        addItems(); //added panel2
        frame.add(panel3);
        frame.add(panel4);
        frame.add(panel5);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(1150, 1000);
        frame.setVisible(true);

    }
    
    double totalAmount;
    int client_id;
    int item_id;
    
    public final void addItems() {
        panel2 = new JPanel();
        panel2.setBounds(150, 100, 800, 100);
        

        itemList = new JComboBox();
        addButton = new JButton("ADD ITEM");
        addButton.setBackground(Color.LIGHT_GRAY);
        addButton.setPreferredSize(new Dimension(150, 40));
        removeButton = new JButton("REMOVE ITEM");
        removeButton.setBackground(Color.LIGHT_GRAY);
        removeButton.setPreferredSize(new Dimension(150, 40));
        removeButton.addActionListener((ActionEvent e) -> {
            int selectedRow = this.dataTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to remove.");
            }
        });
        
        addQuantity = new JTextField();
          
        
        addQuantity.setPreferredSize(new Dimension(150, 40));
        addButton.addActionListener(this);

        createComboBox();
        
        panel2.add(itemList);
        panel2.add(new JLabel(" "));
        panel2.add(new JLabel("Quantity : "));
        panel2.add(addQuantity);
        panel2.add(addButton);
        panel2.add(removeButton);
        frame.add(panel2);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        String Name = (String) itemList.getSelectedItem();
        int index = Name.indexOf("(");
        
        addName = Name.substring(0, index);
        
        if (e.getSource() == addButton) {
            displayList();
        }
        if(e.getSource() == finalSubmit){
            try{
                totalAmountLabel.setText("Rs. : " + String.format("%.2f", totalAmount + Double.parseDouble(externalCosts.getText())));
                int option = JOptionPane.showConfirmDialog(null, "Proceed to Payment?", "Payment GateWay", JOptionPane.OK_CANCEL_OPTION);
                if(option==0){
                    insertDBMS();
                    new Payment(totalAmount+Double.parseDouble(externalCosts.getText()),client_id);
                    
                }
                else
                JOptionPane.showMessageDialog(null,"Payment Cancelled","Warning",JOptionPane.WARNING_MESSAGE);
            }catch(Exception e4){
                JOptionPane.showMessageDialog(null,"Please Enter Labour+Transport cost!","INVALID",JOptionPane.OK_OPTION);
            }  
        }
        if(e.getSource() == NewOrder){
            frame.dispose();
            newOrder no = new newOrder();
            no.openNewOrder();
        }
    }
    
    public void displayList(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url, uname, pass);
            
            String q1 = "select Amount,Item_id from items where item_name=?";
            
            // q1 execution==================================================================
            
            PreparedStatement pstm = con.prepareStatement(q1);
            pstm.setString(1,addName);
            
            ResultSet rs = pstm.executeQuery();
            
            int addAmt;
            
            if (rs.next()) {
                addAmt = rs.getInt("Amount") * Integer.parseInt(addQuantity.getText());
                // Rest of the code
            } else {
                addAmt =0 ;
                // Handle case when no rows are returned
            }
            
            tableModel.addRow(new Object[]{addName, addQuantity.getText() , rs.getInt("Amount"), addAmt });
            
            double externalCost;
            
            try{
                externalCost = Double.parseDouble(externalCosts.getText());
            }catch(Exception e){
                externalCost = 0;
            }
            
            totalAmount = calculateTotal();
            totalAmountLabel.setText("Rs. : " + String.format("%.2f", totalAmount+externalCost ));
            //q1 executed==================================================================== 
            
        } catch (ClassNotFoundException | NumberFormatException | SQLException e1) {
            JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void createComboBox(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url, uname, pass);
            try (Statement st = con.createStatement()) {
                String q = "select item_id,item_name,Available from items";
                
                ResultSet rs = st.executeQuery(q);
                
                while (rs.next()) {
                    itemList.addItem(rs.getString("item_name") + "(" + rs.getString("item_id") + ")(" + rs.getString("Available") + ")");
                    
                }
            }
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    public void insertDBMS(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            Statement st = con.createStatement();
            
            String retriveId = "SELECT * FROM client_details ORDER BY client_id DESC LIMIT 1;";
            String item_booked = "insert into item_booked values(?,?,?,?)";
            String allOrders = "insert into allOrders values(?,?,?,?)";
            String price_info = "insert into price_info values(?,?,?,?)";
            String reduceAvail = "update items set Available = Available-? where item_name = ?";
            String retriveItemId = "Select Item_id from items where item_name = ?";
            String insertClientId = "Insert into client_details(client_id)values(?)";
            
            ResultSet rs = st.executeQuery(retriveId);
            ResultSet rs2;
            
            if(!rs.next())
                client_id = 2001;
            else
                client_id = Integer.parseInt(rs.getString("client_id")) + 1;
            
            PreparedStatement pstm0 = con.prepareStatement(insertClientId);
            pstm0.setInt(1, client_id);
            pstm0.executeUpdate();
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
                double temp = (double) totalAmount;
                pstm.setDouble(2, temp);
                pstm.setDouble(3,Double.parseDouble(externalCosts.getText()));
                pstm.setDouble(4,totalAmount + Double.parseDouble(externalCosts.getText()));
                pstm.executeUpdate();
            
            
            
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
    }
    
    public double calculateTotal(){
        double sum = 0;
        for(int i = 0; i < this.dataTable.getRowCount(); i++)
        {
            sum = sum + Integer.parseInt(this.dataTable.getValueAt(i, 3).toString());
        }
        return sum;
    }

}
