import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginApplet extends JApplet {

    JLabel lbl_user = new JLabel("Username: ");
    JTextField txt_user = new JTextField(10);
    
    JLabel lbl_password = new JLabel("Password: ");
    JPasswordField txt_password = new JPasswordField(10);
    
    JButton btn_login = new JButton("Login");
       
     
    class AL_Login implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
           /* send to*/
        }
    }
    
    AL_Login al1 = new AL_Login();
    
    @Override
    public void init() {

        JPanel main = new JPanel();  
        
//        In main:
//        JPanel User (lbl+txt)->FlowLayout(center)
//        JPanel Password (lbl+txt)->FlowLayout(center)
//        JPanel Button (btn)->FlowLayout(right)
        
        main.add(lbl_user);
        main.add(txt_user);
        
        main.add(lbl_password);
        main.add(txt_password);
        
        main.add(btn_login);  
        btn_login.addActionListener(al1);
    } 
}
