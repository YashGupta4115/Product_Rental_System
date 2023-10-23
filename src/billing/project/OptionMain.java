
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
    JButton deleteOrders;
    
    public OptionMain(){
        
        
        
        panel = new JPanel();
        
        addItemButton = new JButton("ADD ITEMS");
        addItemButton.setFocusable(false);
        addItemButton.setBackground(Color.LIGHT_GRAY);
        addItemButton.setPreferredSize(new Dimension(150, 40));
        
        showItems = new JButton("SHOW ITEMS");
        showItems.setFocusable(false);
        showItems.setBackground(Color.LIGHT_GRAY);
        showItems.setPreferredSize(new Dimension(150, 40));
        
        newOrder  = new JButton("NEW ORDER");
        newOrder.setFocusable(false);
        newOrder.setBackground(Color.LIGHT_GRAY);
        newOrder.setPreferredSize(new Dimension(150, 40));
        
        
        
        modifyOrder = new JButton("MODIFY ORDERS");
        modifyOrder.setFocusable(false);
        modifyOrder.setBackground(Color.LIGHT_GRAY);
        modifyOrder.setPreferredSize(new Dimension(150, 40));
        
        showOrders = new JButton("SHOW ORDERS");
        showOrders.setFocusable(false);
        showOrders.setBackground(Color.LIGHT_GRAY);
        showOrders.setPreferredSize(new Dimension(150, 40));
        
        deleteOrders = new JButton("SHOW ORDERS");
        deleteOrders.setFocusable(false);
        deleteOrders.setBackground(Color.LIGHT_GRAY);
        deleteOrders.setPreferredSize(new Dimension(150, 40));
        
        
        addItemButton.addActionListener(this);
        showItems.addActionListener(this);
        newOrder.addActionListener(this);
        
        modifyOrder.addActionListener(this);
        showOrders.addActionListener(this);
        
        
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.setSize(500,400);
        
        
        
       
        this.add(addItemButton);
        this.add(showItems);
        this.add(newOrder);
        this.add(modifyOrder);
        this.add(showOrders);
        //this.add(duePayOrders);
        //this.add(deleteOrders);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addItemButton){
            new DataEntry();
        }
        else if (e.getSource() == showItems) {
            new showItems();
        }
        else if (e.getSource() == newOrder) {
            newOrder NewOrder = new newOrder();
            NewOrder.openNewOrder();
        }
        else if (e.getSource() == modifyOrder){
            new modifyExistingOrder();
        }
        else if (e.getSource() == showOrders){
            new showOrders();
        }
        
    }
    
}
