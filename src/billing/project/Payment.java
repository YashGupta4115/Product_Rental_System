package billing.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Payment implements ActionListener {

    JDialog dialog;  // Use JDialog instead of JFrame for a modal behavior

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

    // Variable to hold payment status
    private boolean paymentCompleted = false;  // Default to false
    private static int order_id;
    private static double externalCosts;

    public Payment(int order_id, double externalCosts, double ta, int id) {
        Payment.order_id = order_id;
        System.out.println(order_id + "at 48(payment)");
        Payment.externalCosts = externalCosts;
        dialog = new JDialog();  // Create a JDialog instance
        dialog.setModal(true);   // Set dialog as modal to block other windows until closed

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
        totalAmtLabel.setBounds(0, 60, 100, 100);
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

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                functionIfFrameClosed();  // Handle closing event when user manually closes the window
            }
        });

        dialog.add(panel1);
        dialog.add(panel2);

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(null);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(null);  // Center the dialog on screen
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            try {
                double amtDue = totalAmount - Double.parseDouble(clientPaid.getText());
                boolean paymentStatus = dbms(amtDue);
                paymentCompleted = paymentStatus;  // Update the status variable

                if (paymentStatus) {
                    JOptionPane.showMessageDialog(null, "Payment completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Payment failed or was cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                System.out.println("Exception at payment(125)" + ex);
                JOptionPane.showMessageDialog(null, "Invalid amount entered!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dialog.dispose();  // Close the dialog after submission
        }
    }

    // Method that returns the payment status to the calling class
    public boolean isPaymentCompleted() {
        return paymentCompleted;
    }
    Savepoint InitialClientDetails = null;
    // Update the dbms method to return a boolean value indicating success or failure
    public boolean dbms(double amtDue) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, uname, pass);
            
            con.setAutoCommit(false); //start transaction
            InitialClientDetails = con.setSavepoint("InitialClientDetails"); // save savepoint;
            
            String insertClient = "insert into client values(?,?)"; //client_id, name;
            String insertPhone = "insert into phone values(?,?)"; //phone, client_id;
            String insertOrders = "insert into orders values(?,?,?,?,?,?,?)";//order_id, client_id, order_data, event_data, items_amt, trans, amt paid ;

            PreparedStatement prepareClient = con.prepareStatement(insertClient);
            PreparedStatement preparePhone = con.prepareStatement(insertPhone);
            PreparedStatement prepareOrders = con.prepareStatement(insertOrders);
            LocalDate today = LocalDate.now();

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                java.util.Date parsedDate = dateFormat.parse(clientEvent.getText());
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                
                prepareClient.setInt(1, client_id);
                prepareClient.setString(2, clientName.getText());
                
                preparePhone.setInt(1, Integer.parseInt(clientPhone.getText()));
                preparePhone.setInt(2, client_id);
                
                prepareOrders.setInt(1, order_id);
                prepareOrders.setInt(2,client_id);
                prepareOrders.setDate(3, java.sql.Date.valueOf(today));
                prepareOrders.setDate(4,sqlDate);
                prepareOrders.setDouble(5, totalAmount - externalCosts);
                prepareOrders.setDouble(6, externalCosts);
                prepareOrders.setDouble(7, Double.parseDouble(clientPaid.getText()));
                
                prepareClient.executeUpdate();
                preparePhone.executeUpdate();
                prepareOrders.executeUpdate();
                con.commit(); // commit changes;
                JOptionPane.showMessageDialog(null, "Payment SUCCESS\nAmount Due: " + amtDue, "Message", JOptionPane.INFORMATION_MESSAGE); 
                return true;  // Return true if the update is successful
            } catch (SQLException | ParseException e2) {
                con.rollback(InitialClientDetails);
                System.out.println("Exception at payment(181) + rolled back"+e2);
                return false;  // Return false if there's a parsing or SQL exception
            } finally{
                prepareClient.close();
                preparePhone.close();
                prepareOrders.close();
                
                con.close();
            }
            

        } catch (ClassNotFoundException | NumberFormatException | SQLException e1) {
            System.out.println("Exception at payment(187)" + e1);
            JOptionPane.showMessageDialog(null, "Payment Cancelled!", "Error", JOptionPane.ERROR_MESSAGE);
            functionIfFrameClosed();
            return false;  // Return false if an exception occurs
        }
    }

    public boolean functionIfFrameClosed() {
        paymentCompleted = false;  // Set status to false if window is closed without submitting
        return paymentCompleted;
    }

    public static void main(String[] args) {
        Payment paymentInstance = new Payment(order_id, externalCosts, 1001, 2001);
    }
}
