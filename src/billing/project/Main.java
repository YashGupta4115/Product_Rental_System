
package billing.project;


public class Main{
    public static final String url,uname,pass;
    
    static{
        url = "jdbc:mysql://localhost:3306/BillProject";
        uname = "root";
        pass="Yash@123";
    }
    
    Main(){
        new OptionMain();
        new updateItems();
    }  
}
