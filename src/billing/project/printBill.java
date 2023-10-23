
package billing.project;

import java.awt.FlowLayout;
import java.sql.*;
import javax.swing.*;


public class printBill {
    
    JFrame frame = new JFrame();
    JComboBox displayOrder;
    JPanel panel1;
    
    
    
    public printBill(){
        
        panel1 = new JPanel();
        
        displayOrder = new JComboBox();
        panel1.add(displayOrder);
        
        frame.add(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        frame.setSize(1150,650);
        frame.setVisible(true);
        
       createComboBox();
        
    }
    
    String url = "jdbc:mysql://localhost:3306/BillProject";
    String uname = "root";
    String pass = "Yash@123";
    
    public void createComboBox(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            Statement st = con.createStatement();
            
            String q = "select client_name,Event_date from client_details where Event_date >= curdate()";
            ResultSet rs = st.executeQuery(q);
            while(rs.next()){
                displayOrder.addItem(rs.getString("client_name")+"("+rs.getString("Event_date")+")");
            }
            
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Error"+e, "Error Occured", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        }
        
    }
    
    public static void main(String[] args){
        new printBill();
    }
}
