package billing.project;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class OptionMain extends JFrame implements ActionListener {
    JPanel panel;
    JLabel label;
    JPanel display;
    
    JButton addItemButton;
    JButton showItems;
    JButton newOrder;
    JButton modifyOrder;
    JButton showOrders;

    public OptionMain() {
        // Set up the main frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());  // Use BorderLayout for simpler placement
        setExtendedState(OptionMain.MAXIMIZED_BOTH);

        // Top black strip panel using GridBagLayout for button alignment
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.BLACK);
//        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0)); // Add 15px gap at the top
        topPanel.setPreferredSize(new Dimension(getWidth(), 70)); //
        
        // Constraints for button layout in the top panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; //mizimize width;
        gbc.weightx = 1.0; //spread horizontally;   
        gbc.insets = new Insets(0,0,0,0);  // Padding around buttons

        // Initialize buttons with styling and add hover effects
        addItemButton = createStyledButton("ADD ITEMS");
        showItems = createStyledButton("SHOW ITEMS");
        newOrder = createStyledButton("NEW ORDER");
        modifyOrder = createStyledButton("MODIFY ORDERS");
        showOrders = createStyledButton("SHOW ORDERS");
        
        // Add action listeners for buttons
        addItemButton.addActionListener(this);
        showItems.addActionListener(this);
        newOrder.addActionListener(this);
        modifyOrder.addActionListener(this);
        showOrders.addActionListener(this);

        // Add buttons to the top panel with constraints
        topPanel.add(addItemButton, gbc);
        topPanel.add(showItems, gbc);
        topPanel.add(newOrder, gbc);
        topPanel.add(modifyOrder, gbc);
        topPanel.add(showOrders, gbc);
        
        // Add the top panel to the top of the frame
        this.add(topPanel, BorderLayout.NORTH);

        // Set frame visibility
        this.setVisible(true);
    }

    // Method to create a styled button with hover effect
    public JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorderPainted(false); // Remove button border
        button.setOpaque(true); // Make sure background color is shown
        
        // Mouse listener for hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.BLACK);
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addItemButton) {
            new DataEntry();
        } else if (e.getSource() == showItems) {
            new showItems();
        } else if (e.getSource() == newOrder) {
            newOrder NewOrder = new newOrder();
            NewOrder.openNewOrder();
        } else if (e.getSource() == modifyOrder) {
            new modifyExistingOrder();
        } else if (e.getSource() == showOrders) {
            new showOrders();
        }
    }
}
