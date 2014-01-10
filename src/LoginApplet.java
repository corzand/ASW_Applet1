
import asw1009.HTTPClient;
import asw1009.ManageXML;
import asw1009.viewmodel.request.LoginRequestViewModel;
import asw1009.viewmodel.response.BaseResponseViewModel;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class LoginApplet extends JApplet {

    HTTPClient hc = new HTTPClient();
    boolean logged = false;

    JLabel lbl_user,
           lbl_password;

    JTextField txt_user = new JTextField(10);

    JPasswordField txt_password = new JPasswordField(10);

    JButton btn_login = new JButton("Login");

    JOptionPane errorPanel = new JOptionPane();

    public LoginApplet() {
        this.lbl_password = new JLabel("Password: ");
        this.lbl_user = new JLabel("Username: ");
        lbl_user.setFont(new Font("Arial",0,16));
        lbl_password.setFont(new Font("Arial",0,16));
        txt_user.setFont(new Font("Arial",0,14));
        btn_login.setFont(new Font("Arial",0,14));           
    }

    class LoginListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            LoginRequestViewModel viewModel = new LoginRequestViewModel();
            BaseResponseViewModel responseViewModel = new BaseResponseViewModel();

            viewModel.setUsername(txt_user.getText());
            viewModel.setPassword(new String(txt_password.getPassword()));
            try {

                //Code below: XML over HTTP
                ManageXML mngXML = new ManageXML();

                Document data = mngXML.newDocument();
                Element root = data.createElement("login");

                Element username = data.createElement("username");
                username.setTextContent(viewModel.getUsername());

                Element password = data.createElement("password");
                password.setTextContent(viewModel.getPassword());

                root.appendChild(username);
                root.appendChild(password);

                data.appendChild(root);

                Document answer = hc.execute("/users/", data);
                responseViewModel.setError(Boolean.parseBoolean(answer.getElementsByTagName("hasError").item(0).getTextContent()));
                responseViewModel.setErrorMessage(answer.getElementsByTagName("errorMessage").item(0).getTextContent());

                //lbl_result.setText(responseViewModel.hasError() ? responseViewModel.getErrorMessage() : "logged-in")
                if (!responseViewModel.hasError()) {
                    getAppletContext().showDocument(new URL(
                            getCodeBase().getProtocol(),
                            getCodeBase().getHost(),
                            getCodeBase().getPort(), "/application/tasks"), "_self");
                } else {
                    errorPanel.showMessageDialog(null, responseViewModel.getErrorMessage(), "ERRORE", JOptionPane.ERROR_MESSAGE);

                }
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

                    cp.setLayout(null);

                    cp.setBackground(Color.decode("#6DBCDB"));

                    btn_login.addActionListener(loginListener);

                    lbl_user.setBounds(10, 10, 80, 25);
                    cp.add(lbl_user);

                    txt_user.setBounds(100, 10, 160, 25);
                    cp.add(txt_user);

                    lbl_password.setBounds(10, 40, 80, 25);
                    cp.add(lbl_password);

                    txt_password.setBounds(100, 40, 160, 25);
                    cp.add(txt_password);

                    btn_login.setBounds(10, 80, 80, 25);
                    cp.add(btn_login);

                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
