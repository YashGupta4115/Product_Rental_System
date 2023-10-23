/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package billing.project;

import javax.swing.*;

public class test2 {
    public static void main(String[] args) {
        // Create a JFrame
        JFrame frame = new JFrame("Scroll Bar Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JPanel with your content
        JPanel contentPanel = new JPanel();
        
        // Add your components to the content panel
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
         contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii\n"));
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));
         contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));
        contentPanel.add(new JLabel("hiiiiii"));

        // Create a JScrollPane and add the content panel to it
        JScrollPane scrollPane = new JScrollPane(contentPanel);

        // Add the scroll pane to the frame
        frame.getContentPane().add(scrollPane);

        // Set the size of the frame and make it visible
        frame.setSize(300, 200);
        frame.setVisible(true);
    }
}

