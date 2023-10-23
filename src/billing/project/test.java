package billing.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import javax.swing.*;

public class test{
    
    JFrame frame;
    
    JPanel printPanel;
    JPanel headPanel;
    JPanel seperatePanel;
    
    JLabel label1;
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
    
    
    test(){
        frame = new JFrame();
        
        printPanel = new JPanel(null);
        double paperWidthInches = 8.27;
        double paperHeightInches = 11.69;
        int paperWidthPixels = (int) (paperWidthInches * Toolkit.getDefaultToolkit().getScreenResolution());
        int paperHeightPixels = (int) (paperHeightInches * Toolkit.getDefaultToolkit().getScreenResolution());
        printPanel.setBounds(0,0,paperWidthPixels-100, paperHeightPixels);
        printPanel.setBackground(Color.gray);
        JScrollPane scrollPane = new JScrollPane(printPanel);
        frame.getContentPane().add(scrollPane);
        
        
        headPanel = new JPanel(new GridBagLayout());
        headPanel.setBounds(0,0, paperWidthPixels-110 ,100);
        label1 = new JLabel("BILL");
        headPanel.setBackground(Color.pink);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        headPanel.add(label1, gbc);
        
        seperatePanel = new JPanel();
        seperatePanel.setBounds(0,110,paperWidthPixels-80,5);
        seperatePanel.setBackground(Color.pink);
        
        
        label2 = new JLabel("Name            :");
        label2.setBounds(10,130,100,50);
        
        label3 = new JLabel();
        label3.setBounds(110,130,100,50);
        
        label4 = new JLabel("Date  :   ");
        label4.setBounds(paperWidthPixels-330,130,100,50);
        
        LocalDate currentDate = LocalDate.now();
        
        label5 = new JLabel(currentDate.toString());
        label5.setBounds(paperWidthPixels-280,130,100,50);
        
        label6 = new JLabel("Event Date   : ");
        label6.setBounds(10,180,100,50);
        
        label7 = new JLabel();
        label7.setBounds(110,180,100,50);
        
        JPanel s1 = new JPanel();
        s1.setBounds(5,240,paperWidthPixels-180,1);
        s1.setBackground(Color.pink);
        
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
        s2.setBackground(Color.pink);
        
        int y = 285;
        
        for (int i = 1; i <= 5; i++) {
            JLabel label_sno = new JLabel("0"+i);
            JLabel label_name = new JLabel("0"+i);
            JLabel label_qnt = new JLabel("0"+i);
            JLabel label_cost = new JLabel("0"+i);
            JLabel label_amt = new JLabel("0"+i);
            
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
        s2.setBackground(Color.pink);
        
        label13 = new JLabel("  Labour & Transport   :   ");
        label13.setBounds(680,y+20,150,30);
        
        label14 = new JLabel("             demo          ");
        label14.setBounds(820,y+20,200,30);
        
        
        label15 = new JLabel("          Total                    :   ");
        label15.setBounds(680,y+50,150,30);
        
        label16 = new JLabel("             demo          ");
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
        
                
        frame.add(printPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setSize(3508,2480);
        frame.setVisible(true);
        //print();
    }
    
    public static void main(String args[]){
        new test();
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