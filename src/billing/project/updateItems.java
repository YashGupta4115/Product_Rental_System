
package billing.project;

import java.sql.*;

public class updateItems {
    String url = Main.url;
    String uname = Main.uname;
    String pass = Main.pass;
    
    public updateItems(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            
            
            
            String q1   = "update items set Available=Available+?  where Item_id = ?";
            String sq1  = "select item_Qnt from item_booked where item_id= ?";
            String sq2  = "select item_id from item_booked where client_id=?";
            String sq3  = "select client_id from client_details where Event_date < curdate()";
            String updateItemBooked = "delete from item_booked where client_id=?";
            PreparedStatement pstmU = con.prepareStatement(updateItemBooked);
            
            
            
            PreparedStatement pstm3 = con.prepareStatement(sq3);
            ResultSet rs3 = pstm3.executeQuery();
            PreparedStatement pstm0 = con.prepareStatement(q1);
            
            
            while(rs3.next()){
                PreparedStatement pstm2 = con.prepareStatement(sq2);
                pstm2.setInt(1, rs3.getInt("client_id"));
                pstmU.setInt(1,rs3.getInt("client_id"));
                ResultSet rs2 = pstm2.executeQuery();
                pstmU.executeUpdate();
                //System.out.println("sq3 - ok");
                
                while(rs2.next()){
                    
                    PreparedStatement pstm1 = con.prepareStatement(sq1);
                    pstm1.setInt(1,rs2.getInt("item_id"));
                    
                    ResultSet rs1 = pstm1.executeQuery();
                    
                    //System.out.println("sq2 - ok");
                    
                        while(rs1.next()){
                            
                            pstm0.setInt(1, rs1.getInt("item_Qnt"));
                            pstm0.setInt(2,rs2.getInt("item_id"));
                            pstm0.executeUpdate();
                            //System.out.println("sq1 - ok");
                        }
                }
            }
            
            
           
            
            pstm0.close();
            pstm3.close();
            con.close();
            //JOptionPane.showMessageDialog(null, "Items udpated successfully", "Update", JOptionPane.INFORMATION_MESSAGE);
        }catch(Exception e){
            System.out.println("Exception"+e);
        }
        
    
    }
}
