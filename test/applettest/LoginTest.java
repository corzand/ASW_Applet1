package applettest;

import asw1009.HTTPClient;
import asw1009.ManageXML;
import asw1009.models.request.LoginRequestViewModel;
import asw1009.models.response.BaseResponseViewModel;
import java.awt.*;
import javax.swing.JFrame;
import java.awt.event.*;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class LoginTest {

    static final String BASE = "http://localhost:8080/WebApplication/";
    static HTTPClient hc = new HTTPClient();
    static boolean logged = false;

    static JLabel lbl_user = new JLabel("Username: ");
    static JTextField txt_user = new JTextField(10);

    static JLabel lbl_password = new JLabel("Password: ");
    static JPasswordField txt_password = new JPasswordField(10);

    static JButton btn_login = new JButton("Login");
    
    static class LoginListener implements ActionListener {

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

                System.out.println(responseViewModel.hasError);
                System.out.println(responseViewModel.errorMessage);

            } catch (ParserConfigurationException | DOMException | TransformerException | SAXException | IOException ex){
                System.out.println(ex);
            }
        }
    }

    static LoginListener loginListener = new LoginListener();

    
    public static void main(String[] args) throws Exception {

        hc.setBase(new URL(BASE));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("sender");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(220, 130);
                frame.setVisible(true);

                Container cp = frame.getContentPane();
                cp.setLayout(new FlowLayout());

                btn_login.addActionListener(loginListener);

                cp.add(lbl_user);
                cp.add(txt_user);

                cp.add(lbl_password);
                cp.add(txt_password);

                cp.add(btn_login);
            }
        });
    }
}
