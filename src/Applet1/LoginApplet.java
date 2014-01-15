package Applet1;


import asw1009.HTTPClient;
import asw1009.ManageXML;
import asw1009.requests.LoginRequest;
import asw1009.responses.BaseResponse;
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
import javax.swing.border.LineBorder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Applet utilizzata per effettuare il login nell'applicazione
 * @author ASW1009
 */
public class LoginApplet extends JApplet {
    
    //Utility HTTPClient per effettuare chiamate HTTP dalla applet verso la servlet
    HTTPClient hc = new HTTPClient();
    boolean logged = false;
    String errorMessage = "";
    
    JLabel lbl_user = new JLabel("Username: ");
    JLabel lbl_password = new JLabel("Password: ");
    JLabel lbl_remember = new JLabel("Ricordami");
    JLabel lbl_error = new JLabel("");
    JButton btn_login = new JButton("Login");
    
    JCheckBox chk_remember = new JCheckBox();
    JTextField txt_user = new JTextField(10);
    JPasswordField txt_password = new JPasswordField(10);
        
    Font font = new Font("Arial", Font.PLAIN, 16);
    
    /**
     * Costruttore di default, viene soltanto impostato il font
     */
    public LoginApplet() {
    }
    
    class LoginListener implements ActionListener {
        /**
         * nel metodo viene preparata la request da inviare al server, con i campi impostati dall'utente.
         * Una volta preparato l'oggetto da inviare, si scrive il file XML corrispondente, e si invia al server
         * attraverso il metodo execute di httpClient. Quando viene ricevuta risposta, se positiva si effettua un redirect
         * all'url dalla pagina tasks, altrimenti si modifica la view mostrando l'errore ricevuto, utilizzando sempre invokeLater .
         * @param e rappresenta l'evento di pressione del button login
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            LoginRequest viewModel = new LoginRequest();
            BaseResponse responseViewModel = new BaseResponse();
            
            viewModel.setUsername(txt_user.getText());
            viewModel.setPassword(new String(txt_password.getPassword()));
            viewModel.setRemember(chk_remember.isSelected());
            
            try {

                //Code below: XML over HTTP
                ManageXML mngXML = new ManageXML();
                
                Document data = mngXML.newDocument();
                Element root = data.createElement("login");
                
                Element username = data.createElement("username");
                username.setTextContent(viewModel.getUsername());
                
                Element password = data.createElement("password");
                password.setTextContent(viewModel.getPassword());
                
                Element remember = data.createElement("remember");
                remember.setTextContent(viewModel.getRemember() + "");
                
                root.appendChild(username);
                root.appendChild(password);
                root.appendChild(remember);
                
                data.appendChild(root);
                
                Document answer = hc.execute("/users/", data);
                responseViewModel.setError(Boolean.parseBoolean(answer.getElementsByTagName("hasError").item(0).getTextContent()));
                responseViewModel.setErrorMessage(answer.getElementsByTagName("errorMessage").item(0).getTextContent());

                if (!responseViewModel.hasError()) {
                    getAppletContext().showDocument(new URL(
                            getCodeBase().getProtocol(),
                            getCodeBase().getHost(),
                            getCodeBase().getPort(), "/application/tasks/"), "_self");
                } else {
                    errorMessage = responseViewModel.getErrorMessage();
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            txt_password.setBorder(new LineBorder(Color.red));
                            txt_user.setBorder(new LineBorder(Color.red));
                            lbl_error.setText(errorMessage);
                            lbl_error.setVisible(true);
                        }
                    });                    
                    
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
            
            //Invocato attraverso il metodo invoke and wait, per non interagire
            //con la GUI da un thread che non sia il GUI-thread
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    
                    Container cp = getContentPane();
                    cp.setLayout(null);
                    cp.setBackground(Color.decode("#6DBCDB"));
                    
                    btn_login.addActionListener(loginListener);
                    
                    lbl_user.setBounds(10, 10, 100, 25);
                    cp.add(lbl_user);                    
                    
                    txt_user.setBounds(100, 10, 160, 25);
                    cp.add(txt_user);
                    
                    lbl_password.setBounds(10, 40, 100, 25);
                    cp.add(lbl_password);
                    
                    txt_password.setBounds(100, 40, 160, 25);
                    cp.add(txt_password);
                    
                    btn_login.setBounds(10, 100, 80, 25);
                    cp.add(btn_login);   
                    
                    lbl_user.setFont(font);
                    lbl_password.setFont(font);
                    txt_user.setFont(font);
                    btn_login.setFont(font);
                    
                    chk_remember.setBounds(160, 100, 30, 25);
                    chk_remember.setBackground(Color.decode("#6DBCDB"));
                    cp.add(chk_remember);                 
                    
                    
                    lbl_remember.setBounds(190, 100, 100, 25);
                    cp.add(lbl_remember);
                    
                    lbl_error.setBounds(10, 70, 400, 30);
                    lbl_error.setForeground(Color.red);
                    lbl_error.setVisible(false);
                    cp.add(lbl_error);
                    
                    //Se sono presenti cookies passati come parametro alla applet, i campi vengono precompilati
                    if (getParameter("username") != null && getParameter("password") != null) {
                        txt_user.setText(getParameter("username"));                        
                        txt_password.setText(getParameter("password"));
                        chk_remember.setSelected(true);
                    }
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(LoginApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
