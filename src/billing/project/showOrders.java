
package billing.project;

import java.awt.*;
import java.awt.print.*;
import javax.swing.*;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class showOrders extends JFrame implements ActionListener{
    
    DefaultTableModel tableModel;
    JTable dataTable;
    JButton getInfo;
    JButton printOrder;
    
    JComboBox displayOrders;
    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JPanel panel4;
    JPanel panel5;
    JPanel printPanel;
    
    JPanel headPanel;
    JPanel seperatePanel;
    
    JLabel label1;
    JLabel label1_2;
    JLabel label1_3;
    JLabel label2;
    JLabel label3;
    JLabel label4;
    JLabel label5;
    JLabel label6;
    JLabel label7;
    JLabel label8;
    JLabel label9;
    JLabel label10;
    JLabel label11;
    JLabel label12;
    JLabel label13;
    JLabel label14;
    JLabel label15;
    JLabel label16;
    
    public showOrders(){
        
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        this.setSize(1000, 600); // Adjusted for better visibility
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Item_No");
        tableModel.addColumn("Item_Name");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Cost");
        tableModel.addColumn("Total_Cost");
        
        panel1 = new JPanel();
        getInfo = new JButton("Get Info");
        getInfo.setFocusable(false);
        getInfo.addActionListener(this);
        
        displayOrders = new JComboBox();
        
        panel1.add(displayOrders);
        panel1.add(getInfo);
        
        panel2 = new JPanel();
        panel2.setVisible(false);
        
        panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());
        dataTable = new JTable(tableModel);
        panel3.add(new JScrollPane(dataTable), BorderLayout.CENTER);
       
        panel4 = new JPanel();
        panel4.add(new JLabel(""));
        panel4.setVisible(false);
        
        panel5 = new JPanel();
        printOrder = new JButton("Print");
        printOrder.addActionListener(this);
 
        panel5.add(printOrder);
        panel5.setVisible(false);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(panel1, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(panel2, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Allow table to expand
        this.add(panel3, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(panel4, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        this.add(panel5, gbc);
        this.setVisible(true);
        
        clientNameDisplay();
    }
    
    
    String url = Main.url;
    String uname = Main.uname;
    String pass = Main.pass;
    
    int client_id;
    int order_id;
    
    
    void clientNameDisplay(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            Statement st = con.createStatement();
            
            String q = "select c.name, o.event_date,o.order_id from client c left join orders o on c.client_id = o.client_id;";
            ResultSet rs = st.executeQuery(q);
            while(rs.next()){
                displayOrders.addItem(rs.getString("name")+"("+rs.getString("Event_date")+")("+rs.getInt("order_id")+")");
            }
            
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Error"+e, "Error Occured", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }
    double labour,total;
    String name,date; 
       
    public void displayItems(){
        
        tableModel.setRowCount(0);
        panel2.setVisible(false);
        panel2.removeAll();
        panel4.setVisible(false);
        panel4.removeAll();
        int i=1;
        
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url,uname,pass);
            String getClientInfo = "Select c.name,c.client_id,o.event_date,o.order_date from client c join orders o on c.client_id = o.client_id where order_id = ?";
            
            PreparedStatement pstm = con.prepareStatement(getClientInfo);
            
            
            String Name = (String) displayOrders.getSelectedItem();
            
            try{
                String[] result = Name.split("\\(");
                String getName = result[0];

                String  getDate = result[1].substring(0,result[1].length()-1);
                
                order_id = Integer.parseInt(result[2].substring(0,result[2].length()-1));

                pstm.setInt(1,order_id);
                try{
                    ResultSet rS = pstm.executeQuery();
                    rS.next();
                    client_id = rS.getInt("client_id");
                    
                    panel2.add(new JLabel(String.format("Order ID : %d      |     ",order_id )));
                    panel2.add(new JLabel(String.format("Client ID : %d      |     ",client_id )));
                    panel2.add(new JLabel("    Name : "+rS.getString("name")+"      |      Event_Date : "+rS.getDate("event_date") ));
                    name = rS.getString("name");
                    date = getDate;
                    panel2.setVisible(true);
                    
                    String q1 = "select ib.item_id,i.item_name,ib.item_Qnt from items_booked ib join items i on ib.item_id = i.item_id where order_id = ? ";
                    PreparedStatement pstm1 = con.prepareStatement(q1);
                    pstm1.setInt(1,order_id);
                    ResultSet rs1 = pstm1.executeQuery();
                    while(rs1.next()){
                        String q2 = "select Amount,Amount*? as Total from items where Item_id=?";
                        PreparedStatement pstm2 = con.prepareStatement(q2);
                        pstm2.setInt(1, rs1.getInt("item_Qnt"));
                        pstm2.setInt(2,rs1.getInt("item_id"));
                        ResultSet rs2 = pstm2.executeQuery();
                        while(rs2.next()){
                            tableModel.addRow(new Object[]{i++,rs1.getString("item_name"),rs1.getInt("item_Qnt"),rs2.getFloat("Amount"),rs2.getFloat("Total")});
                        }
                        
                    }
                    panel3.setVisible(true);
                    String q3 = "select items_amt, transport_and_human_capital_cost, amt_paid from orders where order_id=?";
                    PreparedStatement pstm3 = con.prepareStatement(q3);
                    pstm3.setInt(1,order_id);
                    ResultSet rs3 = pstm3.executeQuery();
                    rs3.next();
                    panel4.add(new JLabel(String.format("Transport And Human Capital Cost : %.2f      |     ",rs3.getFloat("transport_and_human_capital_cost") )));
                    panel4.add(new JLabel(String.format("GrandTotal : %.2f      |     ",rs3.getFloat("transport_and_human_capital_cost")+rs3.getFloat("items_amt") )));
                    panel4.add(new JLabel(String.format("Paid : %.2f      |     ", rs3.getFloat("amt_paid")  )));
                    panel4.add(new JLabel(String.format("Due : %.2f       ",rs3.getFloat("items_amt") - rs3.getFloat("transport_and_human_capital_cost"))));
                    labour = rs3.getFloat("transport_and_human_capital_cost");
                    total = rs3.getFloat("transport_and_human_capital_cost")+rs3.getFloat("items_amt");
                    panel4.setVisible(true);
                    panel5.setVisible(true);
                    
                    
                }catch(SQLException e2){
                    System.out.println("Exception at 229(showOrders)"+e2);
                    JOptionPane.showMessageDialog(null,e2,"Error",JOptionPane.ERROR_MESSAGE);
                   
                }
            }catch(HeadlessException | SQLException e){
               System.out.println("Exception at 234(showOrders)"+e);
            }
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Exception at 237(showOrders)"+e);
           JOptionPane.showMessageDialog(null,e,"Error",JOptionPane.ERROR_MESSAGE);
           
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==getInfo){
            
            displayItems(); 
            printPanel();
        }
        if(e.getSource()==printOrder){
            print();
        }
    }
    
    void printPanel(){
        
        
        printPanel = new JPanel(null);
        double paperWidthInches = 8.27;
        double paperHeightInches = 11.69;
        int paperWidthPixels = (int) (paperWidthInches * Toolkit.getDefaultToolkit().getScreenResolution());
        int paperHeightPixels = (int) (paperHeightInches * Toolkit.getDefaultToolkit().getScreenResolution());
        printPanel.setBounds(560,0,paperWidthPixels, paperHeightPixels);
        printPanel.setBackground(Color.gray);
        JScrollPane scrollPane = new JScrollPane(printPanel);
        this.getContentPane().add(scrollPane);
        
        
        headPanel = new JPanel(new GridBagLayout());
        headPanel.setBounds(0, 0, paperWidthPixels - 110, 100);

        label1 = new JLabel("XYZ");
        label1.setFont(new Font("Anton", Font.BOLD, 34));

        label1_2 = new JLabel("KIIT UNIVERSITY, PATIA, BHUBANESWAR");
        label1_2.setFont(new Font("Anton",Font.PLAIN, 24));

        label1_3 = new JLabel("Contact: 6201461718, XXXXXXXXXX");
        label1_3.setFont(new Font("Anton", Font.PLAIN, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        gbc.gridy = 0;
        headPanel.add(label1, gbc);

        gbc.gridy = 1;
        headPanel.add(label1_2, gbc);

        gbc.gridy = 2;
        headPanel.add(label1_3, gbc);

        
        seperatePanel = new JPanel();
        seperatePanel.setBounds(0,110,paperWidthPixels-80,5);
        seperatePanel.setBackground(Color.black);
        
        
        label2 = new JLabel("Name            :");
        label2.setBounds(10,130,100,50);
        
        label3 = new JLabel(name);
        label3.setBounds(110,130,100,50);
        
        label4 = new JLabel("Date  :   ");
        label4.setBounds(paperWidthPixels-330,130,100,50);
        
        LocalDate currentDate = LocalDate.now();
        
        label5 = new JLabel(currentDate.toString());
        label5.setBounds(paperWidthPixels-280,130,100,50);
        
        label6 = new JLabel("Rent Date   : ");
        label6.setBounds(10,180,100,50);
        
        label7 = new JLabel(date);
        label7.setBounds(110,180,100,50);
        
        JPanel s1 = new JPanel();
        s1.setBounds(5,240,paperWidthPixels-180,1);
        s1.setBackground(Color.black);
        
        label8 = new JLabel("            Sl. No.                 ");
        label8.setBounds(20,245,130,30);
        
        label9 = new JLabel("|                          Item_Name                     ");
        label9.setBounds(150,245,350,30);
        
        label10 = new JLabel("|              Quantity        ");
        label10.setBounds(530,245,130,30);
        
        label11 = new JLabel("|                Cost         ");
        label11.setBounds(680,245,130,30);
        
        label12 = new JLabel("|            Amount        ");
        label12.setBounds(820,245,200,30);
        
        JPanel s2 = new JPanel();
        s2.setBounds(5,280,paperWidthPixels-180,1);
        s2.setBackground(Color.black);
        
        int y = 285;
        int rowCount = tableModel.getRowCount();
        
        for (int i = 0; i < rowCount; i++) {
            JLabel label_sno = new JLabel("0"+i);
            JLabel label_name = new JLabel(tableModel.getValueAt(i,1).toString());
            JLabel label_qnt = new JLabel(tableModel.getValueAt(i,2).toString());
            JLabel label_cost = new JLabel(tableModel.getValueAt(i,3).toString());
            JLabel label_amt = new JLabel(tableModel.getValueAt(i,4).toString());
            
            label_sno.setBounds(70,y,130,20);
            label_name.setBounds(220,y,130,20);
            label_qnt.setBounds(600,y,130,20);
            label_cost.setBounds(740,y,130,20);
            label_amt.setBounds(880,y,130,20);
            
            printPanel.add(label_sno);
            printPanel.add(label_name);
            printPanel.add(label_qnt);
            printPanel.add(label_cost);
            printPanel.add(label_amt);
            y = y+20;
            
        }
        
        JPanel s3 = new JPanel();
        s2.setBounds(5,y+10,paperWidthPixels-180,1);
        s2.setBackground(Color.black);
        
        label13 = new JLabel("  Labour & Transport   :   ");
        label13.setBounds(680,y+20,150,30);
        
        label14 = new JLabel("             "+labour+"          ");
        label14.setBounds(820,y+20,200,30);
        
        
        label15 = new JLabel("          Total                    :   ");
        label15.setBounds(680,y+50,150,30);
        
        label16 = new JLabel("             "+total+"          ");
        label16.setBounds(820,y+50,200,30);
        
        
        printPanel.add(headPanel);
        printPanel.add(seperatePanel);
        printPanel.add(label2);
        printPanel.add(label3);
        printPanel.add(label4);
        printPanel.add(label5);
        printPanel.add(label6);
        printPanel.add(label7);
        printPanel.add(s1);
        printPanel.add(label8);
        printPanel.add(label9);
        printPanel.add(label10);
        printPanel.add(label11);
        printPanel.add(label12);
        printPanel.add(s2);
        printPanel.add(label13);
        printPanel.add(label14);
        printPanel.add(label15);
        printPanel.add(label16);
        printPanel.add(s3);
    }
    
    
    
    void print(){
        
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Print Record");
        
        job.setPrintable((Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }
            Graphics2D g2d = (Graphics2D) graphics;        
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.scale(0.6,0.6);
            
            
            
            
            
            printPanel.paint(g2d);
            
            
            return Printable.PAGE_EXISTS;
        });
        
        boolean returningResult = job.printDialog();
        if(returningResult){
            try{
                job.print();
            }catch(PrinterException e){
                e.printStackTrace();
            }
        }
        }
}

