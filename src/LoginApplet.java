
import asw1009.HTTPClient;
import asw1009.ManageXML;
import asw1009.models.request.LoginRequestViewModel;
import asw1009.models.response.BaseResponseViewModel;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Applet used to manage Login.
 *
 * @author Andrea
 */
public class LoginApplet extends JApplet {

    HTTPClient hc = new HTTPClient();
    boolean logged = false;

    JLabel lbl_user = new JLabel("Username: ");
    JTextField txt_user = new JTextField(10);

    JLabel lbl_password = new JLabel("Password: ");
    JPasswordField txt_password = new JPasswordField(10);

    JButton btn_login = new JButton("Login");
    JLabel lbl_result = new JLabel("not logged");

    class LoginListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            LoginRequestViewModel viewModel = new LoginRequestViewModel();
            BaseResponseViewModel responseViewModel = new BaseResponseViewModel();

            viewModel.username = txt_user.getText();
            viewModel.password = new String(txt_password.getPassword());
            try {

                //Code below: JSON over HTTP
                
//                JSONObject jsonData = new JSONObject();
//                jsonData.put("username", viewModel.username);
//                jsonData.put("password", viewModel.password);
//                String answer = hc.execute("users/login/", jsonData.toString());
//
//                JSONObject jsonAnswer = new JSONObject(answer);
//                responseViewModel.hasError = jsonAnswer.getBoolean("hasError");
//                responseViewModel.errorMessage = jsonAnswer.getString("errorMessage");

                //Code below: XML over HTTP
                
                ManageXML mngXML = new ManageXML();

                Document data = mngXML.newDocument();
                Element root = data.createElement("login");

                Element username = data.createElement("username");
                username.setTextContent(viewModel.username);

                Element password = data.createElement("password");
                password.setTextContent(viewModel.password);

                root.appendChild(username);
                root.appendChild(password);

                data.appendChild(root);

                Document answer = hc.execute("users", data);
                responseViewModel.hasError = Boolean.parseBoolean(answer.getElementsByTagName("hasError").item(0).getTextContent());
                responseViewModel.errorMessage = answer.getElementsByTagName("errorMessage").item(0).getTextContent();

                lbl_result.setText(responseViewModel.hasError ? responseViewModel.errorMessage : "logged-in");

            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException | SAXException | TransformerException ex) {
                Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    LoginListener loginListener = new LoginListener();

    @Override
    public void init() {
        try {
            hc.setSessionId(getParameter("sessionId"));
            hc.setBase(getDocumentBase());

            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {

                    Container cp = getContentPane();
                    cp.setLayout(new GridLayout(3, 2));

                    //JPanel main = new JPanel();  
                    //        In main:
                    //        JPanel User (lbl+txt)->FlowLayout(center)
                    //        JPanel Password (lbl+txt)->FlowLayout(center)
                    //        JPanel Button (btn)->FlowLayout(right)
                    btn_login.addActionListener(loginListener);

                    cp.add(lbl_user);
                    cp.add(txt_user);

                    cp.add(lbl_password);
                    cp.add(txt_password);

                    cp.add(btn_login);
                    cp.add(lbl_result);
                }
            });

        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
