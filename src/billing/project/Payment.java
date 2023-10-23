
package billing.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;


public class Payment implements ActionListener{
  
    JFrame frame = new JFrame();
    
    JPanel panel1;
    JPanel panel2;
    
    JLabel totalAmt;
    JLabel totalAmtLabel;
    
    JTextField clientName;
    JTextField clientPhone;
    JTextField clientEvent;
    JTextField clientPaid;
    
    JButton button;
    
    double totalAmount;
    int client_id;
    
    String url = Main.url;
    String uname = Main.uname;
    String pass = Main.pass;
    
    
    
    public Payment(double ta,int id){
        
        
        
        WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                evt.getSource();
                functionIfFrameClosed();
            }
        };
        
        frame.setLocation(200, 200);
        
        totalAmount = ta;
        client_id = id;
        
        
        panel1 = new JPanel();
        panel1.setBounds(0, 0, 800, 40);
       
        
        
        clientName = new JTextField();
        clientPhone = new JTextField();
        clientEvent = new JTextField();
        clientPaid = new JTextField();

        clientName.setPreferredSize(new Dimension(150, 20));
        clientPhone.setPreferredSize(new Dimension(150, 20));
        clientEvent.setPreferredSize(new Dimension(150, 20));

        panel1.add(new JLabel("Client's Name : "));
        panel1.add(clientName);
        panel1.add(new JLabel("Client's Phone No : "));
        panel1.add(clientPhone);
        panel1.add(new JLabel("Event Date : "));
        panel1.add(clientEvent);
        
        panel2 = new JPanel();
        panel2.setBounds(0, 60, 800, 40);
        clientPaid.setPreferredSize(new Dimension(150, 20));
        
        totalAmtLabel = new JLabel();
        totalAmtLabel.setText("Rs. : " + String.format("%.2f/- only  |", totalAmount));
        totalAmtLabel.setBounds(0,60,100,100);
        panel2.add(new JLabel("Total Amount"));
        panel2.add(totalAmtLabel);
        panel2.add(new JLabel("             "));
        
        panel2.add(new JLabel("Amount Paid : "));
        panel2.add(clientPaid);
        panel2.add(new JLabel("          "));
        
        button = new JButton("SUBMIT");
        button.setBackground(Color.LIGHT_GRAY);
        button.setPreferredSize(new Dimension(150, 40));
        panel2.add(button);
        button.addActionListener(this);      
        
        frame.addWindowListener(listener);
        frame.add(panel1);
        frame.add(panel2);
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(800, 400);
        frame.setVisible(true);
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == button){
            double amtDue = totalAmount - Double.parseDouble(clientPaid.getText());
            dbms(amtDue);
            frame.dispose();
        }
        
    }
    
    public void dbms(double amtDue){
        try{
            
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            String q2="update client_details set client_name=?,client_ph=?,Event_date=?,Amount_payable=?,Amount_paid=?,Amount_due=? where client_id=?;";
            PreparedStatement pstm = con.prepareStatement(q2);
            
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                java.util.Date parsedDate = dateFormat.parse(clientEvent.getText());
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                pstm.setDate(3,sqlDate);
            }catch(SQLException | ParseException e2){
            }

            pstm.setInt(7, client_id);
            pstm.setString(1,clientName.getText());
            pstm.setString(2,clientPhone.getText());
            
            pstm.setDouble(4,totalAmount);
            pstm.setDouble(5,Double.parseDouble(clientPaid.getText()));
            pstm.setDouble(6,amtDue);
            
            pstm.executeUpdate();
            JOptionPane.showMessageDialog(null, "Payment SUCCESS\nAmount Due : "+amtDue , "Message", JOptionPane.INFORMATION_MESSAGE);
            
            
        }catch(ClassNotFoundException | NumberFormatException | SQLException e1){
            JOptionPane.showMessageDialog(null,"Payment Cancelled !","Error",JOptionPane.ERROR_MESSAGE);
            functionIfFrameClosed();
        }
    }
    
    public void functionIfFrameClosed(){
        try{
            
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            System.out.println("After con - OK" );
            String q1="delete from price_info where client_id = ?";
            String q5="delete from client_details where client_id = ?";
            String q3="select item_name,item_Qnt from item_booked where client_id =?";
            String q4="update items set Available=Available+? where item_name=?";
            String q2="delete from item_booked where client_id = ?";
            
            
            PreparedStatement pstm1 = con.prepareStatement(q1);
            pstm1.setInt(1, client_id);
            pstm1.executeUpdate();
            
            
            PreparedStatement pstm2 = con.prepareStatement(q2);
            pstm2 = con.prepareStatement(q2);
            pstm2.setInt(1, client_id);
            pstm2.executeUpdate();
            
            
            PreparedStatement pstm3 = con.prepareStatement(q3);
            pstm3 = con.prepareStatement(q3);
            pstm3.setInt(1, client_id);
            ResultSet rs = pstm3.executeQuery();
            
            
            while(rs.next()){
                PreparedStatement pstm4 = con.prepareStatement(q4);
                pstm4.setInt(1,Integer.parseInt(rs.getString("item_Qnt")));
                pstm4.setString(2,rs.getString("item_name"));
                pstm4.executeUpdate();
                pstm4.close();
                
            }
            
            PreparedStatement pstm5 = con.prepareStatement(q5);
            pstm5.setInt(1, client_id);
            pstm5.executeUpdate();
           
            JOptionPane.showMessageDialog(null, "Payment Cancelled" , "Message", JOptionPane.INFORMATION_MESSAGE);
            pstm1.close();
            pstm2.close();
            pstm3.close();
            
            pstm5.close();
            con.close();
            
            }catch(ClassNotFoundException | NumberFormatException | SQLException e1){
                JOptionPane.showMessageDialog(null,e1,"Error",JOptionPane.ERROR_MESSAGE);
            }finally{
                
        }
    }
    
    public static void main(String[] args){
        new Payment(1001,2001);
    }

}
