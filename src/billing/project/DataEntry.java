package billing.project;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class DataEntry extends JFrame implements ActionListener {    
    JLabel label1 = new JLabel("Items : ");
    JLabel label2 = new JLabel("Amount : ");
    JLabel label3 = new JLabel("Available : ");
    
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    
    JTextField textField1 = new JTextField();
    JTextField textField2 = new JTextField();
    JTextField textField3 = new JTextField();
    
    JButton button = createStyledButton("ADD");
    
    DefaultTableModel tableModel;
    JTable dataTable;
    
    public JButton createStyledButton(String text){
        JButton btn = new JButton(text);
        btn.setFocusable(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setBorderPainted(false); // Remove button border
        btn.setOpaque(true); // Make sure background color is shown
        
        // Change cursor to pointer
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
            
            @Override
            public void mouseExited(MouseEvent e){
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
        });
        
        return btn;
    }
    
    public DataEntry(){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        setExtendedState(DataEntry.MAXIMIZED_BOTH);
        
        // Set frame background color
        this.getContentPane().setBackground(Color.WHITE);
        
        label1.setFont(new Font("MS Mincho", Font.BOLD, 18));
        label1.setForeground(Color.WHITE);
        label2.setFont(new Font("MS Mincho", Font.BOLD, 18));
        label2.setForeground(Color.WHITE);
        label3.setFont(new Font("MS Mincho", Font.BOLD, 18));
        label3.setForeground(Color.WHITE);
        
        textField1.setPreferredSize(new Dimension(180, 40));
        textField1.setBorder(null);
        
        textField2.setPreferredSize(new Dimension(180, 40));
        textField2.setBorder(null);
        
        textField3.setPreferredSize(new Dimension(180, 40));  
        textField3.setBorder(null);
        
        button.addActionListener(this);
        
        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(label1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(textField1, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(label2, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        panel.add(textField2, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        panel.add(label3, gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;
        panel.add(textField3, gbc);

        gbc.gridx = 6;
        gbc.gridy = 1;
        panel.add(button, gbc);
        
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            BorderFactory.createEmptyBorder(25, 0, 25, 0) 
        ));
        
        panel.setPreferredSize(new Dimension(100, 120));
        panel.setBackground(Color.BLACK);
        panel.setForeground(Color.WHITE);
        
        this.add(panel, BorderLayout.NORTH);
        
        tableModel.addColumn("ItemNo");
        tableModel.addColumn("Item_Name");
        tableModel.addColumn("Item_Amount");
        tableModel.addColumn("Item_Available");   
        
        dataTable.setRowHeight(30);
        dataTable.setFont(new Font("MS Mincho", Font.PLAIN, 18));

        // Customize cell appearance using a custom cell renderer
        TableCellRenderer customRenderer = (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
            JLabel cell = new JLabel(value.toString());
            cell.setFont(new Font("MS Mincho", Font.ITALIC, 16));
            cell.setOpaque(true);
            cell.setBackground(row % 2 == 0 ? new Color(45, 45, 45) : new Color(55, 55, 55));
            cell.setForeground(Color.WHITE);
            return cell;
        };

        // Apply custom renderer to each column
        for (int i = 0; i < dataTable.getColumnCount(); i++) {
            dataTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Customize table header appearance
        JTableHeader header = dataTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 18)); // Header font style
        header.setBackground(new Color(60, 60, 60)); // Header background color
        header.setForeground(Color.WHITE); // Header text color
        header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Header height

        this.add(new JScrollPane(dataTable), BorderLayout.CENTER); 
        this.setVisible(true);
    }
    
    public static int item_id;
    String url = Main.url;
    String uname = Main.uname;
    String pass = Main.pass;
    
    public void addItem(String item_name,float amt, int avail){
        String q = "insert into items values(?,?,?,?,?)";
        String retriveId = "SELECT * FROM items ORDER BY item_id DESC LIMIT 1;";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(url, uname, pass);
            try (Statement st = con.createStatement()) {
                ResultSet rs = st.executeQuery(retriveId);
                
                if (!rs.next())
                    item_id = 1001;
                else
                    item_id = Integer.parseInt(rs.getString("item_id")) + 1;
                
                PreparedStatement pstm = con.prepareStatement(q); // Prepare statement
                // Setting values
                
                pstm.setInt(1, item_id);
                pstm.setString(2, item_name);
                pstm.setFloat(3, amt);
                pstm.setInt(4, avail);
                pstm.setInt(5, avail);
                
                pstm.executeUpdate();
            }
            con.close();
        } catch (ClassNotFoundException | NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            String item_name = textField1.getText();
            float amt = Float.parseFloat(textField2.getText());
            int avail = Integer.parseInt(textField3.getText());  
            addItem(item_name, amt, avail);
            
            if (!item_name.isEmpty() && !textField2.getText().isEmpty() && !textField3.getText().isEmpty()) {
                tableModel.addRow(new Object[]{item_id, item_name, amt, avail});
                textField1.setText("");
                textField2.setText("");
                textField3.setText("");
            }
        }
    }
}
