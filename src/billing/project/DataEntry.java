
package billing.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class DataEntry implements ActionListener {
 
    JFrame frame = new JFrame();
    JLabel label1 = new JLabel();
    JLabel label2 = new JLabel();
    JLabel label3 = new JLabel();
    
    JPanel panel = new JPanel();
    JPanel panel2 = new JPanel();
    
    JTextField textField1 = new JTextField();
    JTextField textField2 = new JTextField();
    JTextField textField3 = new JTextField();
    
    JButton button = new JButton();
    
    DefaultTableModel tableModel;
    JTable dataTable;
    
    
    public DataEntry(){
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.setSize(1200,600);
       
        label1.setText("Item : ");
        label1.setFont(new Font("MS Mincho",Font.BOLD, 18));
        label2.setText("Amount : ");
        label2.setFont(new Font("MS Mincho",Font.BOLD, 18));
        label3.setText("Available : ");
        label3.setFont(new Font("MS Mincho",Font.BOLD, 18));
        
        textField1.setPreferredSize(new Dimension(220,50));
        textField2.setPreferredSize(new Dimension(220,50));
        textField3.setPreferredSize(new Dimension(220,50));
        
        button.setText("ADD");
        button.addActionListener(this);
        button.setBackground(Color.LIGHT_GRAY);
        button.setPreferredSize(new Dimension(150, 40));
        
        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        
        tableModel.addColumn("ItemNo");
        tableModel.addColumn("Item_Name");
        tableModel.addColumn("Item_Amount");
        tableModel.addColumn("Item_Available");

       
        
        
        
        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);
        panel.add(label3);
        panel.add(textField3);
        panel.add(button);
              
        frame.add(panel);
        frame.add(new JScrollPane(dataTable)); 
        frame.setVisible(true);
    }
    
    public static int item_id;
        String url = Main.url;
        String uname = Main.uname;
        String pass = Main.pass;
    
    public void addItem(String item_name,float amt, int avail){
       
        String q="insert into items values(?,?,?,?,?)";
        String retriveId = "SELECT * FROM items ORDER BY item_id DESC LIMIT 1;";
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            try (Statement st = con.createStatement()) {
                ResultSet rs = st.executeQuery(retriveId);
                
                
                if(!rs.next())
                    item_id = 1001;
                else
                    item_id = Integer.parseInt(rs.getString("item_id")) + 1;
                
                PreparedStatement pstm = con.prepareStatement(q); //prepare statement
                //setting values
                
                pstm.setInt(1, item_id);
                pstm.setString(2, item_name);
                pstm.setFloat(3, amt);
                pstm.setInt(4, avail);
                pstm.setInt(5, avail);
                
                pstm.executeUpdate();
            }
            con.close();
        }catch(ClassNotFoundException | NumberFormatException | SQLException e){
            JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
        }
        
        
        //JDBC CODE ENDS
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource()==button){
            String item_name = textField1.getText();
            float amt = Float.parseFloat(textField2.getText());
            int avail = Integer.parseInt(textField3.getText());  
            addItem(item_name,amt,avail);
            
            if (!item_name.isEmpty() && !textField2.getText().isEmpty() && !textField3.getText().isEmpty() ) {
                    tableModel.addRow(new Object[]{item_id,item_name,amt,avail});
                    textField1.setText("");
                    textField2.setText("");
                    textField3.setText("");
            }
            
        }
    }
}
