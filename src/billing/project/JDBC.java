
package billing.project;

import java.awt.FlowLayout;
import java.sql.*;
import javax.swing.*;

public class JDBC extends JFrame {
    
    JComboBox comboBox = new JComboBox();
    public JDBC(){
        int count = 1;
        String itemName[] = new String[count];
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.setSize(300,300);
        
        
        String url = "jdbc:mysql://localhost:3306/BillProject";
        String uname = "root";
        String pass = "Yash@123";
        String q="select * from items";
        String q2="insert into items values(?,?,?,?)";
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            Statement st = con.createStatement();
            
            //PreparedStatement pstm = con.prepareStatement(q2); //prepare statement
            //setting values
            
            //pstm.setInt(1,004);
            //pstm.setString(2,"Sofa");
            //pstm.setInt(3,100);
            //pstm.setFloat(4, 200);
            
            //pstm.executeUpdate();
            

            ResultSet rs = st.executeQuery(q);
            while(rs.next()){
                
                String item = rs.getString("item_name");
                comboBox.addItem(item);
            }
            st.close();
            con.close();
        }catch(Exception e){
            System.out.println("Exception"+e);
        }
        
        
        
        this.add(comboBox);
        this.setVisible(true);
    }
}
